package bel.dev.sa_backend.repository;

import java.util.Optional;


import org.springframework.data.jpa.repository.JpaRepository;

import bel.dev.sa_backend.entities.Panier;




public interface PanierRepository extends JpaRepository<Panier, String>{
    Optional<Panier> findById(String id);
    Optional<Panier> findByUserId(String userId);

}
