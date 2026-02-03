package bel.dev.sa_backend.dto;

import java.math.BigDecimal;
import java.util.List;


public record GuestPanierDTO(List<PanierItemDTO> items, BigDecimal total) {

}
