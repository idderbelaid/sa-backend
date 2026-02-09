package bel.dev.sa_backend.dto;

import java.math.BigDecimal;

public record PanierItemDTO(String id, String produit_id, String name, int amount, BigDecimal price) {}