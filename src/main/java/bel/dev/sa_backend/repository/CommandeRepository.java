package bel.dev.sa_backend.repository;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import bel.dev.sa_backend.entities.Commande;

public interface CommandeRepository extends CrudRepository<Commande, UUID>, JpaSpecificationExecutor<Commande>  {
    Page<Commande> findByUserInfo_Utilisateur_Id(String userId, Pageable pageable);
    Page<Commande> findAll(Pageable pageable);
}
