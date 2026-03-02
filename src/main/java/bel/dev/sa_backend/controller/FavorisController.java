package bel.dev.sa_backend.controller;

import java.security.Principal;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import bel.dev.sa_backend.dto.FavorisDTO;

import bel.dev.sa_backend.service.FavorisService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping(path = "favoris")
public class FavorisController {


    private final FavorisService favorisService;
    @GetMapping(path = "retreive", produces = "Application/Json")
    public @ResponseBody FavorisDTO rechercher(Principal principal){
        String username = principal.getName();
        return favorisService.getFavorisByUsername(username);
    }

    
    @DeleteMapping(path = "delete/{produitId}", produces = "Application/Json")
    public void supprimer(Principal principal, @PathVariable String produitId){
        String username = principal.getName();
        favorisService.supprimerProduitFavori(username, produitId);
    }

    @PostMapping(path = "add/{produitId}", produces = "Application/Json")
    public void ajouter(Principal principal, @PathVariable String produitId){
        String username = principal.getName();
        favorisService.ajouterProduitFavori(username, produitId);
    }



}
