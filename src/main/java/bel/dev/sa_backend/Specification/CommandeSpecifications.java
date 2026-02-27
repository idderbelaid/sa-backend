package bel.dev.sa_backend.Specification;

import org.springframework.data.jpa.domain.Specification;

import bel.dev.sa_backend.entities.Commande;


public class CommandeSpecifications {
     public static Specification<Commande> nameContains(String search) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("numeroCommande")), "%" + search.toLowerCase() + "%");
    }
    
    public static Specification<Commande> utilisateurIdEquals(String userId) {
        return (root, query, cb) -> 
                    cb.equal(root.get("userInfo").get("utilisateur").get("id"), userId);
    }

}
