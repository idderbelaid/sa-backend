package bel.dev.sa_backend.service.utils;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;

import bel.dev.sa_backend.dto.GuestPanierDTO;

import bel.dev.sa_backend.dto.PanierItemDTO;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GestionPanier {
    private final RedisTemplate<String, GuestPanierDTO> redis;


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

    
    public BigDecimal computeTotal(List<PanierItemDTO> items) {
        
            return items.stream()
                .map(i -> i.price().multiply(BigDecimal.valueOf(i.amount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

}
