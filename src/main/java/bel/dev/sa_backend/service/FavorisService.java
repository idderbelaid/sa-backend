package bel.dev.sa_backend.service;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import bel.dev.sa_backend.dto.FavorisDTO;

import bel.dev.sa_backend.entities.Favoris;
import bel.dev.sa_backend.entities.Produit;
import bel.dev.sa_backend.entities.Utilisateur;
import bel.dev.sa_backend.repository.FavorisRepository;
import bel.dev.sa_backend.repository.ProduitRepository;
import bel.dev.sa_backend.repository.UtilisateurRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@AllArgsConstructor
@Service
public class FavorisService {

    private final FavorisRepository favorisRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ProduitRepository produitRepository;
    private final ProduitService produitService;

    public void ajouterProduitFavori(String username, String produitId) {
        Utilisateur user = utilisateurRepository.findByEmail(username)
             .orElseThrow(() -> new RuntimeException("Utilisateur non trouvée"));

        Favoris favoris = favorisRepository.findByUtilisateur(user)
            .orElseGet(() -> {
                Favoris f = new Favoris();
                f.setUtilisateur(user);
                return favorisRepository.save(f);
            });

        Produit produit = produitRepository.findById(produitId)
            .orElseThrow();

        favoris.getProduits().add(produit);
        favorisRepository.save(favoris);
    }


    public void supprimerProduitFavori(String username, String produitId){
        Utilisateur user = utilisateurRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvée"));
        Favoris favoris = favorisRepository.findByUtilisateur(user)
            .orElseThrow(() -> new RuntimeException("Favoris non trouvée"));
        Produit produit = produitRepository.findById(produitId)
            .orElseThrow(() -> new RuntimeException("Produit non trouvée"));
        favoris.getProduits().remove(produit);
        favorisRepository.save(favoris);
    }

    public FavorisDTO getFavorisByUsername(String username){
        Utilisateur user = utilisateurRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvée"));
        Favoris favoris = favorisRepository.findByUtilisateur(user)
            .orElseThrow(() -> new RuntimeException("Favoris non trouvée"));
        FavorisDTO favorisDTO = new FavorisDTO();
        favorisDTO.setFavoris_id(favoris.getFavoris_id());
        favorisDTO.setUsername(username);
        favorisDTO.setProduits(favoris.getProduits().stream().map(p -> {
            return produitService.toDTO(p);
        }).collect(Collectors.toSet()));
        return favorisDTO;

    }

   

}
