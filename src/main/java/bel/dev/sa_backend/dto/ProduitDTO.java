package bel.dev.sa_backend.dto;

import java.math.BigDecimal;

import bel.dev.sa_backend.Enums.Category;

public record ProduitDTO(  
    
    String id,
    String name,
    Category category,
    Integer light,
    Integer water,
    String cover,
    Integer quantity,
    BigDecimal price,
    String description
    ) {

    public ProduitDTO(String id, String name, Category category, int light, int water, String cover, int quantity, double price,
            String description) {
        this(id, name, category, light, water, cover, quantity, BigDecimal.valueOf(price), description);
    }
} 
