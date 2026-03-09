package bel.dev.sa_backend.service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import bel.dev.sa_backend.Specification.ProduitSpecifications;
import bel.dev.sa_backend.dto.PageResponse;
import bel.dev.sa_backend.dto.ProduitDTO;
import bel.dev.sa_backend.entities.Produit;
import bel.dev.sa_backend.repository.ProduitRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.nio.file.*;


@Slf4j
@AllArgsConstructor
@Service
public class ProduitService {

    private final ProduitRepository produitRepository;
    
    private final Path storageRoot = Paths.get("uploads/products");


    public PageResponse<ProduitDTO>  rechercher(String search, String category,int page, int size, String sort) {
       
        System.out.println("sort ..............."+ sort);
        
        Pageable pageable = PageRequest.of(page, size);

        if (sort != null && !sort.isBlank()) {
            pageable = PageRequest.of(page, size, buildSort(sort));
        }
     

        
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
  
    public ProduitDTO toDTO(Produit produit) {
        return new ProduitDTO(  
            produit.getId(), 
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


    public ProduitDTO creer(ProduitDTO produit)  {
        try{
            Produit product = this.dtoToProduct(produit);
            //product.setCover(imageUrl);
            System.out.println("voici le produit : " + produit.getCategory());
            String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
            System.out.println("random : " + random);
            product.setId(random);
            product = this.produitRepository.save(product); 
            return this.toDTO(product);
        }catch (Exception e) {
            e.printStackTrace();
            return null; // ou null / un DTO "vide"
        }

    }

    public void upload(String id, MultipartFile file) throws IOException{

        Produit product = this.produitRepository.findById(id).orElseThrow( () -> new UsernameNotFoundException("Aucun produit avec cet identificant"));
        String imageUrl = null;

        if (file != null && !file.isEmpty()) {
            // Validation simple
            if (file.getSize() > 8 * 1024 * 1024) { // 8MB
                throw new IllegalArgumentException("Image trop volumineuse");
            }
            String contentType = file.getContentType();
            if (contentType == null || !(contentType.equals("image/jpeg")
                    || contentType.equals("image/png") || contentType.equals("image/webp"))) {
                throw new IllegalArgumentException("Format non supporté (JPG/PNG/WEBP)");
            }

            // Nom de fichier unique
            String ext = switch (contentType) {
                case "image/png" -> ".png";
                case "image/webp" -> ".webp";
                default -> ".jpg";
            };
            String filename = UUID.randomUUID() + "-" + Instant.now().toEpochMilli() + ext;

            // Enregistrement local
            Path dest = storageRoot.resolve(filename);
            Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

            // URL publique
            imageUrl = "/files/products/" + id + "/" + filename;

            product.setCover(imageUrl);
             this.produitRepository.save(product);
        }


    }
    public void modifie(String id, ProduitDTO produit) {
        Produit product = this.produitRepository.findById(id)
            .orElseThrow( () -> new UsernameNotFoundException("Aucun produit avec cet identificant"));
        System.out.println("produit trouvé :" +product.getName());
        System.out.println("on va changer les valeurs de light  :" +produit.getLight());
        System.out.println("on va changer les valeurs de  water :" +produit.getWater());
        // compare chauqe element
        if(product.getName() != null &&!product.getName().equals(produit.getName()))
            product.setName(produit.getName());
        if(product.getCategory() != null && ! product.getCategory().equals(produit.getCategory()))
            product.setCategory(produit.getCategory());
        if(product.getDescription() != null && !product.getDescription().equals(produit.getDescription()))
            product.setDescription(produit.getDescription());
        if(product.getLight()!= null && ! product.getLight().equals(produit.getLight()))
        {
            System.out.println("je modifie la valeur de light");
            product.setLight(produit.getLight());
        }
            
        if(product.getWater()!= null && !product.getWater().equals(produit.getWater())){
             System.out.println("je modifie la valeur de water");
            product.setWater(produit.getWater());

        }
            
        if(product.getPrice()!= null && !product.getPrice().equals(produit.getPrice()))
            product.setPrice(produit.getPrice());
        if(product.getQuantity() != null && !product.getQuantity().equals(produit.getQuantity()))
            product.setQuantity(produit.getQuantity());
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

    
    private Sort buildSort(String sort) {
        return switch (sort) {
            case "PRICE_ASC"  -> Sort.by(Sort.Direction.ASC, "price");
            case "PRICE_DESC" -> Sort.by(Sort.Direction.DESC, "price");
            case "NAME_ASC"   -> Sort.by(Sort.Direction.ASC, "name");
            case "NAME_DESC"  -> Sort.by(Sort.Direction.DESC, "name");
            default -> Sort.unsorted();
        };
    }

    public ProduitDTO infoById(String id){
        Produit produit = this.produitRepository.findById(id).orElseThrow( () -> new UsernameNotFoundException("Aucun produit avec cet identifiant"));
        return this.toDTO(produit);
    }

    public Produit dtoToProduct(ProduitDTO product){
        Produit produit =  new Produit();
        produit.setName(product.getName());
        produit.setCategory(product.getCategory());
        produit.setDescription(product.getDescription());
        produit.setLight(product.getLight());
        produit.setWater(product.getWater());
        produit.setPrice(product.getPrice());
        produit.setQuantity(product.getQuantity());
        return produit;
    }


   

    
}
