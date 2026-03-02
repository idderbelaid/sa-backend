package bel.dev.sa_backend.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;


import bel.dev.sa_backend.entities.Favoris;
import bel.dev.sa_backend.entities.Utilisateur;

public interface FavorisRepository extends CrudRepository<Favoris, UUID>, JpaSpecificationExecutor<Favoris> {

    public Optional<Favoris> findByUtilisateur(Utilisateur user); 

}
