package bel.dev.sa_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

import bel.dev.sa_backend.dto.PageResponse;
import bel.dev.sa_backend.dto.ProduitDTO;
import bel.dev.sa_backend.entities.Produit;
import bel.dev.sa_backend.entities.Sentiment;
import bel.dev.sa_backend.service.ProduitService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@AllArgsConstructor
@RestController
@RequestMapping(path = "/produit", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProduitController {

    private final ProduitService produitService;

    @GetMapping(path = "produits")
    public @ResponseBody PageResponse<ProduitDTO> rechercher( 
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
     
            @RequestParam(required = false) String sortField,
            @RequestParam(required = false) String sortDirection,
            @RequestParam(required = false) String category


    ){
                
        return this.produitService.rechercher(search, category, page, size, sortField, sortDirection);
    }
    
    @GetMapping(path = "categories")
    public List<String> getCategories() {
        return produitService.getCategories();
    }


    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "creer")
    public void creerProduit(@RequestBody Produit produit) {
        produitService.creer(produit);
    }
    
    @ResponseStatus(HttpStatus.ACCEPTED)
    @PutMapping(path = "update/{id}")
    public void modifieProduit(@PathVariable("id") String id,@RequestBody ProduitDTO produit) {
        System.out.println("identifiant du produit"+ id);
        produitService.modifie(id,produit);
        System.out.println("identifiant du produit modifié");
    }

    @ResponseStatus(HttpStatus.OK)
    @DeleteMapping(path = "delete/{id}")
    public void supprimerProduit(@PathVariable("id") String id) {
      
        produitService.supprimer(id);
      
    }


}
