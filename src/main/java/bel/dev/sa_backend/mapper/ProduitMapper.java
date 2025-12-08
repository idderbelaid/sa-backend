package bel.dev.sa_backend.mapper;

import bel.dev.sa_backend.dto.ProduitDTO;

import bel.dev.sa_backend.entities.Produit;


public class ProduitMapper {

     public static ProduitDTO toProduitDTO(Produit produit) {
        return new ProduitDTO(
            produit.getId_produit(), 
            produit.getName(), 
            produit.getCategory(), 
            produit.getLight(), 
            produit.getWater(), 
            produit.getCover(), 
            produit.getQuantity(), 
            produit.getPrice(), 
            produit.getDescription()
        );
    }
    
}
