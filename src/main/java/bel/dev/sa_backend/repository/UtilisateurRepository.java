package bel.dev.sa_backend.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import bel.dev.sa_backend.entities.Utilisateur;

public interface UtilisateurRepository extends CrudRepository<Utilisateur, String> {

    Optional<Utilisateur> findByEmail(String email);

}
