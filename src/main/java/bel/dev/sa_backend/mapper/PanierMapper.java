package bel.dev.sa_backend.mapper;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import bel.dev.sa_backend.dto.PanierDTO;
import bel.dev.sa_backend.dto.PanierItemDTO;
import bel.dev.sa_backend.entities.Panier;
import bel.dev.sa_backend.entities.PanierItem;


@Component
public class PanierMapper {

    

    public PanierDTO toPanierDTO(Panier panier, List<PanierItem> items) {
        if (panier == null)
            throw new RuntimeException("user doenst have a cart");
        PanierDTO dto = new PanierDTO();
        dto.setPanierId(panier.getId());
        dto.setStatus(panier.getStatus());
        dto.setUserId(panier.getUserId());
        // Map des items
        List<PanierItemDTO> itemDtos = (items == null || items.isEmpty())
                ? Collections.emptyList()
                : items.stream().map(PanierMapper::toItemDto).collect(Collectors.toList());
        dto.setItems(itemDtos);

        // Calcul du total à partir des lignes
        BigDecimal total = computeTotal(itemDtos);
        dto.setTotal(total);
        return dto;
    }

    /**
     * Map un PanierItem en DTO.
     */
    public static PanierItemDTO toItemDto(PanierItem item) {
        return new PanierItemDTO(
            item.getId().toString(), item.getProduit().getName(), item.getQuantity(), item.getUnitPrice()
        );

       
    }
    

    // Helpers
    private static BigDecimal nvl(BigDecimal v, BigDecimal dft) {
        return v == null ? dft : v;
    }

    private static Integer nvl(Integer v, Integer dft) {
        return v == null ? dft : v;
    }

    private static BigDecimal scale2(BigDecimal v) {
        return (v == null ? BigDecimal.ZERO : v).setScale(2, RoundingMode.HALF_UP);
    }

     public BigDecimal computeTotal(List<PanierItemDTO> items) {
        
            return items.stream()
                .map(i -> i.price().multiply(BigDecimal.valueOf(i.amount())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

    }

}
