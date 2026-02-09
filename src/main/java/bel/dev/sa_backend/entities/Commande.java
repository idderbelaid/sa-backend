package bel.dev.sa_backend.entities;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import bel.dev.sa_backend.Enums.CommandeStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.core.publisher.Sinks.One;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "commande", indexes = {
    @Index(name = "ux_numero_commande", columnList = "numeroCommande", unique = true),
    @Index(name = "ix_commande_user", columnList = "userId")
})

public class Commande {

    @Id @GeneratedValue 
    private UUID commande_id;

    @Column(nullable = false, unique = true, length = 32)
    private String numeroCommande;

    
    @OneToOne(mappedBy = "commande", cascade = CascadeType.ALL, optional = false)
    private CommandeUserInfo userInfo;


    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 24)
    private CommandeStatus status = CommandeStatus.CREATED;

    @Column(nullable = false, length = 3)
    private String currency = "EUR";



    private Instant createdAt;
    private Instant updatedAt;
    private Instant placedAt;

    private Long montantTotal; // soit le total de la commande en centimes

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<CommandeItem> items = new ArrayList<>();


    // One-to-One inverse side (non propriétaire)
    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Paiement paiement;

    
    public void attachUserInfo(CommandeUserInfo info) {
        info.setCommande(this);
        this.userInfo = info;
    }
    public void addItem(CommandeItem item) {
        item.setOrder(this);
        this.items.add(item);
    }
    



}
