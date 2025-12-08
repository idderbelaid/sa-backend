package bel.dev.sa_backend.service;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import bel.dev.sa_backend.Specification.ProduitSpecifications;
import bel.dev.sa_backend.dto.PageResponse;
import bel.dev.sa_backend.dto.ProduitDTO;
import bel.dev.sa_backend.entities.Produit;
import bel.dev.sa_backend.entities.Utilisateur;
import bel.dev.sa_backend.repository.ProduitRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@AllArgsConstructor
@Service
public class ProduitService {

    private final ProduitRepository produitRepository;

    public PageResponse<ProduitDTO>  rechercher(String search, String category,int page, int size, String sortField, String sortDirection) {
        Sort.Direction direction = 
        sortDirection != null
         && sortDirection.equalsIgnoreCase("desc") 
        ? Sort.Direction.DESC : Sort.Direction.ASC;
        
        
        Pageable pageable =
            (sortField == null || sortField.isBlank())
                ? PageRequest.of(page, size)  // pas de tri
                : PageRequest.of(page, size, Sort.by(direction, sortField));

        
        // Construire la Specification dynamique
        Specification<Produit> spec = (root, query, cb) -> cb.conjunction();;

        if (search != null && !search.isBlank()) {
            spec = spec.and(ProduitSpecifications.nameContains(search));
        }
        if (category != null && !category.isBlank()) {
            spec = spec.and(ProduitSpecifications.categoryEquals(category));
        }
        Page<Produit> produits =this.produitRepository.findAll(spec, pageable);
        List<ProduitDTO> products = new java.util.ArrayList<>();
           
        products = produits.stream()
                .map(this::toDTO)
                .toList();

        return new PageResponse<>(
            products,
            produits.getNumber(),
            produits.getSize(),
            produits.getTotalElements(),
            produits.getTotalPages(),                
            produits.isFirst(),
            produits.isLast()
        );

       
           
        
    }

    
    private ProduitDTO toDTO(Produit produit) {
        return new ProduitDTO(  
            produit.getId_produit(), 
            produit.getName(), 
            produit.getCategory(), 
            produit.getLight(), 
            produit.getWater(), 
            produit.getCover(), 
            produit.getQuantity(), 
            produit.getPrice(), 
            produit.getDescription());
    }
   


    public List<String> getCategories() {
       return this.produitRepository.findDistinctCategories();
    }


    public void creer(Produit produit) {
        System.out.println("voici le produit : " + produit.getCategory());
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        System.out.println("random : " + random);
        produit.setId_produit(random);
        this.produitRepository.save(produit);
    }


    public void modifie(String id, ProduitDTO produit) {
        Produit product = this.produitRepository.findById(id)
            .orElseThrow( () -> new UsernameNotFoundException("Aucun produit avec cet identificant"));
        System.out.println("produit trouvé :" +product.getName());
        System.out.println("on va changer les valeurs de light  :" +produit.light());
        System.out.println("on va changer les valeurs de  water :" +produit.water());
        // compare chauqe element
        if( product.getName() != null &&!product.getName().equals(produit.name()))
            product.setName(produit.name());
        if(product.getCategory() != null && ! product.getCategory().equals(produit.category()))
            product.setCategory(produit.category());
        if(product.getDescription() != null && !product.getDescription().equals(produit.description()))
            product.setDescription(produit.description());
        if(product.getLight()!= null && ! product.getLight().equals(produit.light()))
        {
            System.out.println("je modifie la valeur de light");
            product.setLight(produit.light());
        }
            
        if(product.getWater()!= null && !product.getWater().equals(produit.water())){
             System.out.println("je modifie la valeur de water");
            product.setWater(produit.water());

        }
            
        if(product.getPrice()!= null && !product.getPrice().equals(produit.price()))
            product.setPrice(produit.price());
        if(product.getQuantity() != null && !product.getQuantity().equals(produit.quantity()))
            product.setQuantity(produit.quantity());
        System.out.println("produit trouvé :" +product.getLight());
        System.out.println("produit trouvé :" +product.getWater());
        this.produitRepository.save(product);
    }


    public void supprimer(String id) {
        Produit product = this.produitRepository.findById(id)
            .orElseThrow( () -> new UsernameNotFoundException("Aucun produit avec cet identificant"));
        if(product != null)
            this.produitRepository.delete(product);
    }

   

    
}
