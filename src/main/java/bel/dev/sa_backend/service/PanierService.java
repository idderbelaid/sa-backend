package bel.dev.sa_backend.service;



import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import bel.dev.sa_backend.Enums.PanierStatus;
import bel.dev.sa_backend.controller.PanierDBController.CartItemResponse;
import bel.dev.sa_backend.controller.PanierDBController.CreateCartItemRequest;
import bel.dev.sa_backend.controller.PanierDBController.UpdateCartItemRequest;
import bel.dev.sa_backend.dto.GuestPanierDTO;
import bel.dev.sa_backend.dto.PanierDTO;
import bel.dev.sa_backend.dto.PanierItemDTO;
import bel.dev.sa_backend.entities.Panier;
import bel.dev.sa_backend.entities.PanierItem;
import bel.dev.sa_backend.entities.Produit;
import bel.dev.sa_backend.entities.Utilisateur;
import bel.dev.sa_backend.exception.CartItemNotFoundException;
import bel.dev.sa_backend.exception.StockInsuffisantException;
import bel.dev.sa_backend.mapper.PanierMapper;
import bel.dev.sa_backend.repository.PanierItemRepository;
import bel.dev.sa_backend.repository.PanierRepository;
import bel.dev.sa_backend.repository.ProduitRepository;
import bel.dev.sa_backend.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PanierService {
     
    private final RedisTemplate<String, GuestPanierDTO> redis;
    private final StringRedisTemplate stringRedis;
    private final PanierRepository panierRepository;
    private final PanierMapper panierMapper;
    private final PanierItemRepository panierItemRepository;
    private final ProduitRepository produitRepository;
    private final UtilisateurRepository utilisateurRepository;

    @Transactional
    public PanierDTO  mergeGuestCartIntoUser(String session_id, String username){
        Utilisateur user = this.utilisateurRepository.findByEmail(username).
            orElseThrow(()-> new UsernameNotFoundException("Aucun utilisateur à cet identifiant"));
           
        String lockKey = "lock:merge:" + session_id;

        // 1) Lock concurrency
        String token  = tryAcquireLock(lockKey, Duration.ofSeconds(60));
        if (token == null) {
            throw new IllegalStateException("Fusion en cours, réessaye.");
        }
        try{
            
            GuestPanierDTO guest = this.getCart(session_id);
            // 2) Charger/Créer le panier utilisateur
            Panier userCart = panierRepository.findByUserId(user.getId())
                    .orElseGet(() -> {
                        Panier p = new Panier();
                        p.setUserId(user.getId());
                        p.setStatus(PanierStatus.ACTIVE);
                        p.setCreatedAt(Instant.now());
                        p.setUpdateAt(Instant.now());
                        p.setPrix_total(BigDecimal.ZERO);
                        return panierRepository.save(p);
                    });
            // 3) Si guest vide -> retourner le panier utilisateur avec ses items DB
            if (guest == null || guest.items() == null || guest.items().isEmpty()) {
                List<PanierItem> itemsDb = panierItemRepository.findByPanierId(userCart.getId());
                return panierMapper.toPanierDTO(userCart, itemsDb);
            }
            for (PanierItemDTO gi : guest.items()) {
                
                String produitId = gi.id();

                
                // recharger le produit depuis la DB (source de vérité pour le prix/nom)
                Produit produit = produitRepository.findById(produitId)
                        .orElseThrow(()-> new UsernameNotFoundException("Aucun produit à cet identifiant: " + produitId));
                
                int qtyAdd = Math.max(1, gi.amount());
                
                // chercher s'il existe déjà une ligne pour ce produit
                Optional<PanierItem> existingOpt =
                        panierItemRepository.findByPanierIdAndProduitId(userCart.getId(), produit.getId());
                
                if (existingOpt.isPresent()) {
                    PanierItem existing = existingOpt.get();
                    existing.setQuantity(existing.getQuantity() + qtyAdd);
                    existing.setUnitPrice(produit.getPrice());       // adapte à ton modèle
                    //existing.setEffectiveUnitPrice(produit.getPrixEffectifActuel()); // adapte à ton modèle
                    // line_total recalculé par @PreUpdate
                    panierItemRepository.save(existing);
                } else {
                    PanierItem newItem = new PanierItem();
                    newItem.setPanier(userCart);
                    newItem.setProduit(produit);
                    newItem.setQuantity(qtyAdd);
                    newItem.setUnitPrice(produit.getPrice());         // adapte
                    //newItem.setEffectiveUnitPrice(produit.getPrixEffectifActuel()); // adapte
                    // line_total calculé par @PrePersist
                    panierItemRepository.save(newItem);
                }
            }


            
            // 5) Marquer/supprimer le guest en Redis (idempotence)
            markGuestMergedAndDelete(session_id);

            // 6) Recharger les items DB et mapper vers DTO
            List<PanierItem> mergedItems = panierItemRepository.findByPanierId(userCart.getId());
            PanierDTO dto = panierMapper.toPanierDTO(userCart, mergedItems);

            // 7) (Option) mettre à jour le prix_total dans Panier
            userCart.setPrix_total(dto.getTotal());
            userCart.setUpdateAt(Instant.now());
            panierRepository.save(userCart);

            return dto;


                

        }catch (RuntimeException  e) {
            throw new IllegalStateException("Erreur lors de la récupération du panier invité", e);
        } finally {
            releaseLock(lockKey, token);
        }

    
    }
    
    private String tryAcquireLock(String key, Duration ttl) {
        // SETNX emulation
        
        String token = UUID.randomUUID().toString();
        Boolean ok = stringRedis.opsForValue().setIfAbsent(key, token, ttl);
        return Boolean.TRUE.equals(ok)? token : null;

      
    }

    
    private void releaseLock(String key, String token) {
        try {
            String current = stringRedis.opsForValue().get(key);
            if (token != null && token.equals(current)) {
                stringRedis.delete(key);
            }
        } catch (Exception ignored) {}
    }


    
    private void markGuestMergedAndDelete(String sessionId) {
        String cartKey = "cart:session:" + sessionId;
        // Option 1: supprimer
        redis.delete(cartKey);
        // Option 2 (alternative): mettre un flag merged=true puis supprimer
        // GuestPanier guest = ...
        // guest.setMerged(true);
        // redisTemplate.opsForValue().set(cartKey, objectMapper.writeValueAsString(guest));
        // redisTemplate.delete(cartKey);
    }

    public PanierDTO findOrCreateUserCart(String username) {
        Utilisateur user = this.utilisateurRepository.findByEmail(username).
            orElseThrow(()-> new UsernameNotFoundException("Aucun utilisateur à cet identifiant"));
           
        Panier userCart = panierRepository.findByUserId(user.getId())
        .orElseGet(() -> {
            Panier p = new Panier();
            p.setUserId(user.getId());
            p.setStatus(PanierStatus.ACTIVE);
            p.setCreatedAt(Instant.now());
            p.setUpdateAt(Instant.now());
            p.setPrix_total(BigDecimal.ZERO);
            return panierRepository.save(p);
        });

        List<PanierItem> itemsDb = panierItemRepository.findByPanierId(userCart.getId());
        return panierMapper.toPanierDTO(userCart, itemsDb);
    }

    public List<CartItemResponse> listItems(String cartId) {
       return this.panierItemRepository.findByPanierId(cartId).stream().map(this::toResponse).toList();
    }

    
    private CartItemResponse toResponse(PanierItem ci) {
        BigDecimal total = ci.getUnitPrice().multiply(BigDecimal.valueOf(ci.getQuantity()));
        return new CartItemResponse(ci.getId(), ci.getId(), ci.getQuantity(), ci.getUnitPrice(), total);
    }

    public CartItemResponse addItem(String cartId, CreateCartItemRequest request) {
        Panier panier = this.panierRepository.findById(cartId).orElseThrow(()-> new UsernameNotFoundException("Aucun panier pour cet utilisateur"));
        Produit produit = this.produitRepository.findById(request.productId()) 
        .orElseThrow( () -> new UsernameNotFoundException("Aucun produit avec cet identificant"));
        if(produit.getQuantity() < request.quantity())
            throw new StockInsuffisantException(" insuffisant Stock !");


        //je récupére l'item du panier si il existe
        
        // Si le produit existe déjà dans le panier, on incrémente la quantité
        Optional<PanierItem> existing = panierItemRepository.findByPanierIdAndProduitId(cartId, request.productId());

        PanierItem item = existing.orElseGet(() -> {
            var ci = new PanierItem();
            ci.setPanier(panier);
            ci.setProduit(produit);
            ci.setUnitPrice(request.unitPrice());
            ci.setQuantity(0);
            return ci;
        });
        
        item.setQuantity(item.getQuantity() + request.quantity());
        // Option: mettre à jour le prix unitaire si nécessaire:
        if(produit.getPrice() != request.unitPrice())
            item.setUnitPrice(produit.getPrice());
        return toResponse(panierItemRepository.save(item));      
    }

    public CartItemResponse updateItem(String cartId, String itemId, UpdateCartItemRequest request) {
      
        PanierItem item = panierItemRepository.findById(itemId)
                .filter(ci -> ci.getPanier().getId().equals(cartId))
                .orElseThrow(() -> new CartItemNotFoundException(itemId, cartId));
        item.setQuantity(request.quantity());
        return toResponse(panierItemRepository.save(item));


    }

    
    @Transactional
    public void deleteItem(String cartId, String itemId) {
        PanierItem item = panierItemRepository.findById(itemId)
                .filter(ci -> ci.getPanier().getId().equals(cartId))
                .orElseThrow(() -> new CartItemNotFoundException(itemId, cartId));
        panierItemRepository.delete(item);
    }

    @Transactional
    public void clearCart(String cartId) {
        panierItemRepository.deleteByPanierId(cartId);
    }
    
    @Transactional
    public PanierDTO findOrCreatePanierUser(String userId) {
        Panier cart = panierRepository.findByUserId(userId)
            .orElseGet(() -> {
                Panier p = new Panier();
                p.setUserId(userId);
                p.setStatus(PanierStatus.ACTIVE);
                p.setCreatedAt(Instant.now());
                p.setUpdateAt(Instant.now());
                p.setPrix_total(BigDecimal.ZERO);
                return panierRepository.save(p);
            });

        List<PanierItem> itemsDb = panierItemRepository.findByPanierId(cart.getId());
        return panierMapper.toPanierDTO(cart, itemsDb);
    }
    public GuestPanierDTO getCart(String sessionId) {
            GuestPanierDTO obj = redis.opsForValue().get(key(sessionId));
            if (obj != null) {
                // Normaliser si jamais nulls
                List<PanierItemDTO> items = obj.items() != null ? obj.items() : List.of();
                BigDecimal total = obj.total() != null ? obj.total() : BigDecimal.ZERO;
                return new GuestPanierDTO(items, total);
            }
            // Panier vide par défaut
            return new GuestPanierDTO(List.of(), BigDecimal.ZERO);
    }
    private String key(String sessionId) {
        return "cart:guest:" + sessionId;
    }






}
