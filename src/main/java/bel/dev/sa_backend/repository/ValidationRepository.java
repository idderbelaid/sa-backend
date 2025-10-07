package bel.dev.sa_backend.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import bel.dev.sa_backend.entities.Utilisateur;
import bel.dev.sa_backend.entities.Validation;

public interface ValidationRepository extends CrudRepository<Validation, Integer>{
    
    Optional<Validation> findByCode(String code);

    Optional<Validation> findByUtilisateur(Utilisateur user);

}
