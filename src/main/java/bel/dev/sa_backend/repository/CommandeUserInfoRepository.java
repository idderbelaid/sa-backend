package bel.dev.sa_backend.repository;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import bel.dev.sa_backend.entities.Commande;
import bel.dev.sa_backend.entities.CommandeUserInfo;

public interface CommandeUserInfoRepository extends CrudRepository<CommandeUserInfo, UUID> {
    CommandeUserInfo getCommandeUserInfoByCommande(Commande commande);
}
