
package bel.dev.sa_backend.dto;

import java.math.BigDecimal;
import java.util.List;

import bel.dev.sa_backend.Enums.PanierStatus;

public class PanierDTO {

    private String userId;
    private String panierId;
    private PanierStatus status;
    private List<PanierItemDTO> items;
    private BigDecimal total;

    // --- Constructeurs ---
    public PanierDTO() {}

    public PanierDTO(String userId, String panierId, PanierStatus status, List<PanierItemDTO> items, BigDecimal total) {
        this.userId = userId;
        this.panierId = panierId;
        this.status = status;
        this.items = items;
        this.total = total;
    }

    // --- Getters & Setters ---
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPanierId() {
        return panierId;
    }

    public void setPanierId(String panierId) {
        this.panierId = panierId;
    }

    public PanierStatus getStatus() {
        return status;
    }

    public void setStatus(PanierStatus status) {
        this.status = status;
    }

    public List<PanierItemDTO> getItems() {
        return items;
    }

    public void setItems(List<PanierItemDTO> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    @Override
    public String toString() {
        return "PanierDTO{" +
                "userId='" + userId + '\'' +
                ", panierId='" + panierId + '\'' +
                ", status=" + status +
                ", items=" + items +
                ", total=" + total +
                '}';
    }
}
