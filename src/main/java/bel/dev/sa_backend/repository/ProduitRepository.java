package bel.dev.sa_backend.repository;


import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import bel.dev.sa_backend.entities.Produit;


public interface ProduitRepository extends JpaRepository<Produit, String>, JpaSpecificationExecutor<Produit>{
    Page<Produit> findAll(Pageable pageable);
    Page<Produit> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    @Query("SELECT DISTINCT p.category FROM Produit p")
    List<String> findDistinctCategories();

    
}
