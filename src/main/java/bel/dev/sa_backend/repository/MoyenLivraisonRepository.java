package bel.dev.sa_backend.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import bel.dev.sa_backend.entities.MoyenLivraison;

public interface MoyenLivraisonRepository extends JpaRepository<MoyenLivraison, String> {
}
