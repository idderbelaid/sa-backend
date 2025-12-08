package bel.dev.sa_backend.Specification;

import org.springframework.data.jpa.domain.Specification;

import bel.dev.sa_backend.entities.Produit;


public class ProduitSpecifications {
    public static Specification<Produit> nameContains(String search) {
        return (root, query, cb) -> cb.like(cb.lower(root.get("name")), "%" + search.toLowerCase() + "%");
    }

    public static Specification<Produit> categoryEquals(String category) {
        return (root, query, cb) -> cb.equal(root.get("category"), category);
    }
}
