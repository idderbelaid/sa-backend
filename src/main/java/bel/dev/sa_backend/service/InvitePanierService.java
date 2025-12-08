package bel.dev.sa_backend.service;

import java.math.BigDecimal;
import java.util.HashMap;

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

    public GuestPanierDTO addItem(String sessionId, String productId, int quantity, BigDecimal unitPrice, long ttlSeconds){
        
        var cart = getCart(sessionId);
        Produit produit = this.produitRepository.findById(productId) 
        .orElseThrow( () -> new UsernameNotFoundException("Aucun produit avec cet identificant"));
        
        if(quantity > produit.getQuantity())
            throw new StockInsuffisantException(" insuffisant Stock !");
       
        BigDecimal price = produit.getPrice();
        BigDecimal unitPriceBD = unitPrice;


        if (price.compareTo(unitPriceBD) != 0) 
            throw new RuntimeException("Manipulation de prix d'achat");

        var items = new HashMap<>(cart.items());

        var existing = items.get(productId);
        if (existing != null) {
            var newQty = existing.quantity() + quantity;
            items.put(productId, new PanierItemDTO(productId, newQty, existing.unitPrice()));
        } else {
            items.put(productId, new PanierItemDTO(productId, quantity, unitPrice));
        }

        var total = items.values().stream()
                .map(i -> i.unitPrice().multiply(BigDecimal.valueOf(i.quantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        var updated = new GuestPanierDTO(items, total);

        var k = key(sessionId);
        redis.opsForValue().set(k, updated);
        if (ttlSeconds > 0) {
            redis.expire(k, java.time.Duration.ofSeconds(ttlSeconds));
        }
        return updated;

    }
    
    public GuestPanierDTO getCart(String sessionId) {
        var obj = redis.opsForValue().get(key(sessionId));

        if (obj instanceof GuestPanierDTO) {
            return (GuestPanierDTO) obj;
        }
        return new GuestPanierDTO(new java.util.HashMap<>(), java.math.BigDecimal.ZERO);
    }
    
    private String key(String sessionId) {
        return "cart:guest:" + sessionId;
    }


}
