package bel.dev.sa_backend.entities;

import java.math.BigDecimal;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;   
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "commande_items")
public class CommandeItem {

    @Id @GeneratedValue 
    private UUID id;

    @ManyToOne(optional = false) @JoinColumn(name = "order_id")
    private Commande order;

    private Integer lineNumber;

    private String productId;

    @Column(length = 64) 
    private String sku;

    @Column(nullable = false) 
    private String productName;

    @Column(nullable = false) 
    private Integer quantity;

    // Prix en centimes + TVA
    @Column(nullable = false) 
    private Long unitPriceExclTax;
    @Column(nullable = false) 
    private Long unitTaxAmount;
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal taxRate; // ex: 5.50

    @Column(nullable = false) 
    private Long lineDiscountAmount = 0L;
    @Column(nullable = false) 
    private Long lineTotalInclTax;

    @Column(columnDefinition = "TEXT")
    private String variantAttributesJson;

}
