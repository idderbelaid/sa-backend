package bel.dev.sa_backend.dto;

import java.math.BigDecimal;

public record PanierItemDTO(String productId, int quantity, BigDecimal unitPrice) {}