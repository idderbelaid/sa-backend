package bel.dev.sa_backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import bel.dev.sa_backend.dto.GuestPanierDTO;
import bel.dev.sa_backend.dto.PanierItemDTO;
import bel.dev.sa_backend.entities.Produit;
import bel.dev.sa_backend.exception.StockInsuffisantException;
import bel.dev.sa_backend.repository.ProduitRepository;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class InvitePanierService {

    private final RedisTemplate<String, GuestPanierDTO> redis;
    private final ProduitRepository produitRepository;

    public GuestPanierDTO addItem(String sessionId, String id, int amount, BigDecimal price, long ttlSeconds){
        
        
        var cart = getCart(sessionId);
        System.out.println("la carte que j'ai récupéré contient : " +cart);
        Produit produit = this.produitRepository.findById(id) 
        .orElseThrow( () -> new UsernameNotFoundException("Aucun produit avec cet identificant"));
        System.out.println("le produit que j'ai récupéré contient : " +produit.getName());
        if(amount > produit.getQuantity())
            throw new StockInsuffisantException(" insuffisant Stock !");
        
        BigDecimal priceDB = produit.getPrice();
        BigDecimal unitPriceBD = price;

        System.out.println("Il n'avait as eu de manipulation");
        if (priceDB.compareTo(unitPriceBD) != 0) 
            throw new RuntimeException("Manipulation de prix d'achat");

        List<PanierItemDTO> items = new ArrayList<>(cart.items() != null ? cart.items() : List.of());
        
        int idx = indexOfItemById(items, id);
        System.out.println("idx :" +idx);
        if (idx >= 0) {
            System.out.println("mettre à jour la quantité du produit");
            PanierItemDTO existing = items.get(idx);
            System.out.println("existing quantity: " +existing);
            int newQty = existing.amount() + amount;
            System.out.println("new quantity: " +newQty);
            items.set(idx, new PanierItemDTO(id,produit.getId(), produit.getName(), newQty, existing.price()));


        } else {
            items.add(new PanierItemDTO(id,produit.getId(), produit.getName(), amount, price));
        }
        BigDecimal total = computeTotal(items);
        System.out.println("total: " +total);
        var updated = new GuestPanierDTO(items, total);
        System.out.println("updated: " +updated);
        var k = key(sessionId);
        redis.opsForValue().set(k, updated);
        if (ttlSeconds > 0) {
            redis.expire(k, java.time.Duration.ofSeconds(ttlSeconds));
        }
        return updated;

    }
    
    private BigDecimal computeTotal(List<PanierItemDTO> items) {
        
            return items.stream()
                .map(i -> i.price().multiply(BigDecimal.valueOf(i.amount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

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
    
    private static int indexOfItemById(List<PanierItemDTO> items, String id) {
        for (int i = 0; i < items.size(); i++) {
            PanierItemDTO it = items.get(i);
            if (Objects.equals(it.id(), id)) return i;
        }
        return -1;
    }

    public GuestPanierDTO delteItemFromCart(String sessionId, String id_product, long ttlSeconds) {
        System.out.println("id roduit à supprimer" +id_product);
        var cart = getCart(sessionId);
        List<PanierItemDTO> items = new ArrayList<>(cart.items() != null ? cart.items() : List.of());

        int idx = indexOfItemById(items, id_product);
        System.out.println("id roduit à supprimer idx" +idx);
        
        if(idx >= 0)
            items.remove(idx);
        BigDecimal total = computeTotal(items);
        GuestPanierDTO updated = new GuestPanierDTO(items, total);
        var k = key(sessionId);
        redis.opsForValue().set(k, updated);
        if (ttlSeconds > 0) {
            redis.expire(k, java.time.Duration.ofSeconds(ttlSeconds));
        }
        return updated;
    
    }

    public void deleteAllItems(String sessionId) {
       
        String k = key(sessionId);
        var cart = getCart(sessionId);
        // Supprime entièrement la clé du panier
        if(cart != null)
            redis.delete(k);
        else{
            throw new RuntimeException("session ID introuvable");
        }

    }

    public void diminuerQantity(String sessionId, String id, long ttlSeconds) {
        var cart = getCart(sessionId);
        List<PanierItemDTO> items = new ArrayList<>(cart.items() != null ? cart.items() : List.of());
        Produit produit = this.produitRepository.findById(id) 
        .orElseThrow( () -> new UsernameNotFoundException("Aucun produit avec cet identificant"));
        int idx = indexOfItemById(items, id);
        System.out.println("id roduit à diminuer idx" +idx);
        if(idx >= 0){
            System.out.println("mettre à jour la quantité du produit");
            PanierItemDTO existing = items.get(idx);
            System.out.println("existing quantity: " +existing);
            int newQty = existing.amount() - 1;
            System.out.println("new quantity: " + newQty);
            if(newQty <= 0){
                items.remove(idx);
            }
            else
            items.set(idx, new PanierItemDTO(id, produit.getId(),produit.getName(), newQty, existing.price()));
        }else{
            throw new RuntimeException("Produit introuvable dans le panier");
        }
        BigDecimal total = computeTotal(items);
        System.out.println("total: " +total);
        var updated = new GuestPanierDTO(items, total);
        System.out.println("updated: " +updated);
        var k = key(sessionId);
        redis.opsForValue().set(k, updated);
        if (ttlSeconds > 0) {
            redis.expire(k, java.time.Duration.ofSeconds(ttlSeconds));
        }
       

    }

}
