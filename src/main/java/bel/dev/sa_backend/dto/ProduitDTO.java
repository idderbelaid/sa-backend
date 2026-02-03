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

    
} 
