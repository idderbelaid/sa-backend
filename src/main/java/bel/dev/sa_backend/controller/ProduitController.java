package bel.dev.sa_backend.controller;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import bel.dev.sa_backend.dto.PageResponse;
import bel.dev.sa_backend.dto.ProduitDTO;

import bel.dev.sa_backend.service.ProduitService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.IOException;
import java.nio.file.*;




@RestController
@RequestMapping(path = "/produit", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProduitController {

    private final ProduitService produitService;
    private final Path storageRoot = Paths.get("uploads/products");
    
    public ProduitController(ProduitService produitService) throws IOException{
        this.produitService = produitService;
        if(!Files.exists(storageRoot))
            Files.createDirectories(storageRoot);
    }

    @GetMapping(path = "produits")
    public @ResponseBody PageResponse<ProduitDTO> rechercher( 
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String category


    ){
 
        return this.produitService.rechercher(search, category, page, size, sort);
    }
    
    @GetMapping(path = "categories")
    public List<String> getCategories() {
        return produitService.getCategories();
    }


    @PostMapping(path = "creer", consumes = "application/json")
    public ResponseEntity<ProduitDTO> creerProduit(@RequestBody ProduitDTO produit){
        ProduitDTO created = produitService.creer(produit);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @PostMapping(path = "upload/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(@PathVariable String id, @RequestParam("file") MultipartFile file) throws IOException{
        produitService.upload(id, file);
        return ResponseEntity.ok("Image uploader successfully");
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

    @GetMapping(path = "info/{id}")
    public ProduitDTO info(@PathVariable("id") String id) {
        System.out.println("identifiant : "+id);
        return produitService.infoById(id);
    }



}
