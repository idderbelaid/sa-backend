package bel.dev.sa_backend.dto;

import java.math.BigDecimal;
import java.util.Map;

public record GuestPanierDTO(Map<String, PanierItemDTO> items, BigDecimal total) {

}
