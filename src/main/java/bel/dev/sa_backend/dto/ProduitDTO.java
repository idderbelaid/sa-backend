package bel.dev.sa_backend.dto;

import java.math.BigDecimal;
import bel.dev.sa_backend.Enums.Category;

public class ProduitDTO {

    private String id;
    private String name;
    private Category category;
    private Integer light;
    private Integer water;
    private String cover;
    private Integer quantity;
    private BigDecimal price;
    private String description;

    // ----- CONSTRUCTEUR PRINCIPAL (équivalent au record) -----
    public ProduitDTO(String id, String name, Category category,
                      Integer light, Integer water, String cover,
                      Integer quantity, BigDecimal price, String description) {

        this.id = id;
        this.name = name;
        this.category = category;
        this.light = light;
        this.water = water;
        this.cover = cover;
        this.quantity = quantity;
        this.price = price;
        this.description = description;
    }

    // ----- CONSTRUCTEUR SECONDAIRE EXACTEMENT IDENTIQUE AU RECORD -----
    public ProduitDTO(String id, String name, Category category,
                      int light, int water, String cover,
                      int quantity, double price, String description) {
        this(id, name, category, light, water, cover, quantity,
                BigDecimal.valueOf(price), description);
    }

    // ----- CONSTRUCTEUR PAR DÉFAUT OBLIGATOIRE POUR SPRING/JACKSON -----
    public ProduitDTO() {}

    // ----- GETTERS & SETTERS -----
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Integer getLight() { return light; }
    public void setLight(Integer light) { this.light = light; }

    public Integer getWater() { return water; }
    public void setWater(Integer water) { this.water = water; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}