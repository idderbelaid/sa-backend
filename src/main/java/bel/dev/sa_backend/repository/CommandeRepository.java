package bel.dev.sa_backend.repository;
import java.util.List;
import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import bel.dev.sa_backend.entities.Commande;

public interface CommandeRepository extends CrudRepository<Commande, UUID> {
    List<Commande> findByUserInfo_Utilisateur_Id(String userId);
}
