package bel.dev.sa_backend.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import bel.dev.sa_backend.dto.GuestPanierDTO;
import bel.dev.sa_backend.dto.PanierItemDTO;
import bel.dev.sa_backend.entities.Produit;
import bel.dev.sa_backend.exception.StockInsuffisantException;
import bel.dev.sa_backend.repository.ProduitRepository;
import lombok.AllArgsConstructor;
import lombok.val;

@AllArgsConstructor
@Service
public class InvitePanierService {

    private final RedisTemplate<String, GuestPanierDTO> redis;
    private final ProduitRepository produitRepository;

    public GuestPanierDTO addItem(String sessionId, String id, int amount, BigDecimal price, long ttlSeconds){
        
        
        var cart = getCart(sessionId);
        Produit produit = this.produitRepository.findById(id) 
        .orElseThrow( () -> new UsernameNotFoundException("Aucun produit avec cet identificant"));
        
        if(amount > produit.getQuantity())
            throw new StockInsuffisantException(" insuffisant Stock !");
        
        BigDecimal priceDB = produit.getPrice();
        BigDecimal unitPriceBD = price;


        if (priceDB.compareTo(unitPriceBD) != 0) 
            throw new RuntimeException("Manipulation de prix d'achat");
        List<PanierItemDTO> items = new ArrayList<>(cart.items() != null ? cart.items() : List.of());

        int idx = indexOfItemById(items, id);
        if (idx >= 0) {

            PanierItemDTO existing = items.get(idx);
            int newQty = existing.amount() + amount;
            items.set(idx, new PanierItemDTO(id, produit.getName(), newQty, existing.price()));

        } else {
            items.add(new PanierItemDTO(id, produit.getName(), amount, price));
        }
        BigDecimal total = computeTotal(items);

        var updated = new GuestPanierDTO(items, total);
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

}
