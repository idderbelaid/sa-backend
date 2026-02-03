package bel.dev.sa_backend.entities;

import java.math.BigDecimal;

import jakarta.persistence.Table;

import bel.dev.sa_backend.Enums.Category;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "PRODUIT")
public class Produit {

    @Id
    @Column(length = 19)
    private String id;

    
    private String name;

    @Enumerated(EnumType.STRING)
    private Category category;
    private Integer light;
    private Integer water;
    private String cover;
    private Integer quantity;
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    private String description;
    
}
