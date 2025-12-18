package bel.dev.sa_backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import bel.dev.sa_backend.entities.PanierItem;

public interface PanierItemRepository extends JpaRepository<PanierItem, String> {
    
    @Query("SELECT pi FROM PanierItem pi WHERE pi.panier.id = :panierId")
    List<PanierItem> findByPanierId(@Param("panierId") String panierId);
    Optional<PanierItem> findById(String panierId);
    Optional<PanierItem> findByPanierIdAndProduitId(String panierId, String produitId);
    void deleteByPanierId(String cartId);

}
