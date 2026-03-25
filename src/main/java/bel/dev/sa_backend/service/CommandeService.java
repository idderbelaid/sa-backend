package bel.dev.sa_backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;



import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import bel.dev.sa_backend.Enums.CommandeStatus;
import bel.dev.sa_backend.Enums.PaiementStatus;
import bel.dev.sa_backend.Specification.CommandeSpecifications;

import bel.dev.sa_backend.controller.requestDTO.CommandeInviteRequest;
import bel.dev.sa_backend.controller.requestDTO.InfoUserInviteDTO;
import bel.dev.sa_backend.controller.requestDTO.ItemCommandeDTO;
import bel.dev.sa_backend.dto.AddressDTO;
import bel.dev.sa_backend.dto.AddressResponseDTO;
import bel.dev.sa_backend.dto.CommandeLivraisonResponseDTO;
import bel.dev.sa_backend.dto.CommandeResponseDTO;
import bel.dev.sa_backend.dto.PageResponse;
import bel.dev.sa_backend.entities.Commande;
import bel.dev.sa_backend.entities.CommandeItem;
import bel.dev.sa_backend.entities.CommandeUserInfo;
import bel.dev.sa_backend.entities.Paiement;
import bel.dev.sa_backend.entities.Panier;
import bel.dev.sa_backend.entities.Produit;
import bel.dev.sa_backend.entities.Utilisateur;
import bel.dev.sa_backend.mapper.CommandeMapper;
import bel.dev.sa_backend.repository.CommandeRepository;
import bel.dev.sa_backend.repository.CommandeUserInfoRepository;
import bel.dev.sa_backend.repository.PanierRepository;
import bel.dev.sa_backend.repository.ProduitRepository;
import bel.dev.sa_backend.repository.UtilisateurRepository;
import io.micrometer.common.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@AllArgsConstructor
@Service
public class CommandeService{

    private final UtilisateurRepository utilisateurRepository;
    private final ProduitRepository produitRepository;
    private final CommandeRepository commandeRepository;
    private final PanierService panierService;
    private final InvitePanierService invitePanierService;
    private final PanierRepository panierRepository;
    private final CommandeUserInfoRepository commandeUserInfoRepository;


    public CommandeResponseDTO creer(CommandeInviteRequest commande, @Nullable String username, @Nullable String sessionId){
        System.out.println("Création de la commande pour invité");
        //enregistrer l'utilisateur invité
        InfoUserInviteDTO utilisateur = commande.getInfoUserInvite();
        Utilisateur user = null;
        if(username != null)
            user = this.utilisateurRepository.findByEmail(username).orElse(null);
        if(user == null)
            user = this.utilisateurRepository.findByEmail(utilisateur.getEmail()).orElse(null);
        CommandeUserInfo user_info = new CommandeUserInfo();
         if(user != null)
            user_info.setUtilisateur(user);
        user_info.setNom(utilisateur.getNom());
        user_info.setPrenom(utilisateur.getPrenom());
        user_info.setEmail(utilisateur.getEmail());
        user_info.setTelephone(utilisateur.getTelephone());



        //Verficiation de la livraison price.
        if(commande.getDeliveryPrice() < 0){
            throw new RuntimeException("Le prix de la livraion ne peut pas être négatif");
        }else{
            //logique pour vérifier le prix de la livraison selon l'adresse de shipping et le moyen de livraison choisi
            //pour l'instant on accepte le prix envoyé par le client
        }
        if(commande.getSousTotalPrice()<0){
            throw new RuntimeException("Le prix de sous total ne peut pas être négatif");
        }
         if(commande.getTotalPrice() < 0){
            throw new RuntimeException("Le prix de total ne peut pas être négatif");
        }
        //enregistrer la commande

        Commande order = new Commande();
        order.setNumeroCommande(this.generateNumeroCommande());
        
        order.setStatus(bel.dev.sa_backend.Enums.CommandeStatus.CREATED);
        List<CommandeItem> itemsCommande = new ArrayList<>();
        
        //enregistrer les items de la commande
        ItemCommandeDTO[] orderItems = commande.getItemsCommande();
        long totalCommande = 0;
        for (ItemCommandeDTO item : orderItems) {
            //logique pour enregistrer chaque item
            Produit p = this.produitRepository.findById(item.productId()).orElseThrow(() -> new RuntimeException("Produit non trouvé"));
            
            CommandeItem itemNew = new CommandeItem();
            itemNew.setProductId(item.productId());
            itemNew.setQuantity(item.quantity());
            itemNew.setUnitPriceExclTax(item.unitPrice());
            itemNew.setProductName(p.getName());
            // SKU ici → selon l’option choisie
            itemNew.setSku(p.getId()); // Option A simplissime

            // TVA simple (si tu n’en as pas encore)
            itemNew.setTaxRate(BigDecimal.ZERO);
            itemNew.setUnitTaxAmount(0L);
    
            Long lineTotal = itemNew.getUnitPriceExclTax() * itemNew.getQuantity();
            itemNew.setLineTotalInclTax(lineTotal);
            itemNew.setOrder(order); // Lien vers la commande
            totalCommande += lineTotal;
            
            itemsCommande.add(itemNew);
        }
        order.setItems(itemsCommande);

        //Enregistrer le paiement (simple pour l’instant)
        Paiement paiement = new Paiement();
        paiement.setAmount(totalCommande + commande.getDeliveryPrice());
        paiement.setCurrency("EUR");
        paiement.setProvider("Stripe");
        paiement.setMethod("CARD");
        paiement.setStatus(PaiementStatus.PENDING);
        paiement.setOrder(order);
        order.setPaiement(paiement);
        //enregister l'adresse de shipping
        AddressDTO addressShipping = commande.getAdresseShipping();
        System.out.println("Adresse de shipping : " + addressShipping);


        user_info.setNumeroEtvoie(addressShipping.numeroEtvoie());
        user_info.setComplementAdresse(addressShipping.complementAddress());
        user_info.setVille(addressShipping.ville());
        user_info.setCodePostal(addressShipping.codePostal());
        user_info.setPays(addressShipping.pays());
        order.setSous_total(totalCommande);
        order.attachUserInfo(user_info);
        order.setMontantTotal(totalCommande + commande.getDeliveryPrice());
        order.setCreatedAt(java.time.Instant.now());
        order.setUpdatedAt(java.time.Instant.now());
        order.setPlacedAt(java.time.Instant.now());
        Commande cmd = this.commandeRepository.save(order);
        //Gérer le stock des produits
        for (ItemCommandeDTO item : orderItems) {
            //logique pour enregistrer chaque item
            Produit p = this.produitRepository.findById(item.productId()).orElseThrow(() -> new RuntimeException("Produit non trouvé"));
            p.setQuantity(p.getQuantity() - item.quantity());
            this.produitRepository.save(p);
        }
        //vider mon panier
        if(username != null){
            //utilisateur connecté
            Panier userCart = this.panierRepository.findByUserId(user.getId()).orElse(null);
            if(userCart != null)
                this.panierService.clearCart(userCart.getId());
        }else{ 
            //invité
            System.out.println("gérr le cas d'un user invité");
            if(sessionId != null){
                this.invitePanierService.deleteAllItems(sessionId);
            }   

        }
          
        return CommandeMapper.toCommandeResponseDTO(cmd);
    }



    public String generateNumeroCommande() {
        String prefix = "ORD";
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + "-" + date + "-" + random;
    }



	public PageResponse<CommandeResponseDTO> retreive(String username, String search, int page, int size, String sort) {
		Utilisateur user = this.utilisateurRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        Pageable pageable = PageRequest.of(page, size);

        if (sort != null && !sort.isBlank()) {
            pageable = PageRequest.of(page, size, buildSort(sort));
        }
         // Construire la Specification dynamique
        Specification<Commande> spec = (root, query, cb) -> cb.conjunction();;

        if (search != null && !search.isBlank()) {
            spec = spec.and(CommandeSpecifications.nameContains(search));
        }
        spec = spec.and(CommandeSpecifications.utilisateurIdEquals(user.getId()));
        Page<Commande> commandes =  this.commandeRepository.findAll(spec, pageable);
        
        List<CommandeResponseDTO> response = new ArrayList<>();

         response = commandes.stream()
                .map(CommandeMapper::toCommandeResponseDTO)
                .toList();
         return new PageResponse<>(
            response,
            commandes.getNumber(),
            commandes.getSize(),
            commandes.getTotalElements(),
            commandes.getTotalPages(),                
            commandes.isFirst(),
            commandes.isLast()
        );
	}
    public PageResponse<CommandeResponseDTO> adminRetreiveAllCommandes(String username,String search, int page, int size, String sort)
    {
        System.out.println("Je suis la pour récup les commandes admin");
        Utilisateur user = this.utilisateurRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if(user.hasRole("ADMIN")){
            System.out.println("logique de recup toutes les commandes admin");
            Pageable pageable = PageRequest.of(page, size);

            if (sort != null && !sort.isBlank()) {
                pageable = PageRequest.of(page, size, buildSort(sort));
            }
            // Construire la Specification dynamique
            Specification<Commande> spec = (root, query, cb) -> cb.conjunction();;

            if (search != null && !search.isBlank()) {
                spec = spec.and(CommandeSpecifications.nameContains(search));
            }

            Page<Commande> commandes =  this.commandeRepository.findAll(spec, pageable);
            List<CommandeResponseDTO> response = new ArrayList<>();
            response = commandes.stream()
                .map(CommandeMapper::toCommandeResponseDTO)
                .toList();
            return new PageResponse<>(
                response,
                commandes.getNumber(),
                commandes.getSize(),
                commandes.getTotalElements(),
                commandes.getTotalPages(),                
                commandes.isFirst(),
                commandes.isLast()
            );
        }
        System.out.println("pas logique de recup toutes les commandes admin");
        return null;

    }

    public CommandeResponseDTO getCommandeById(String username, UUID id){
        Utilisateur user = this.utilisateurRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if(user.hasRole("ADMIN")){
            Commande commande = this.commandeRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
            return CommandeMapper.toCommandeResponseDTO(commande);
        }
        return new CommandeResponseDTO();
    }



    public CommandeResponseDTO updateCommandeStatus(String username, UUID uuid, CommandeStatus status) {
        Utilisateur user = this.utilisateurRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if(user.hasRole("ADMIN")){
            Commande commande = this.commandeRepository.findById(uuid)
                                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
            commande.setStatus(status);
            Commande updateCommande = this.commandeRepository.save(commande);
            return CommandeMapper.toCommandeResponseDTO(updateCommande);
        }
        return null;
    }



    public CommandeLivraisonResponseDTO getUserCommandeById(String username, UUID uuid) {
        Utilisateur user = this.utilisateurRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        Commande commande = this.commandeRepository.findById(uuid)
                            .orElseThrow(() -> new RuntimeException("Commande non trouvée"));

        CommandeUserInfo orderInfo = this.commandeUserInfoRepository.getCommandeUserInfoByCommande(commande);
        AddressResponseDTO address = this.extraireAddress(orderInfo);
        CommandeResponseDTO orderDTO = CommandeMapper.toCommandeResponseDTO(commande); 
        return new CommandeLivraisonResponseDTO(orderDTO, address);
        
    }



    public CommandeResponseDTO cancelUserCommande(String username, UUID uuid) {
        Utilisateur user = this.utilisateurRepository.findByEmail(username)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        
        Commande commande = this.commandeRepository.findById(uuid)
                                .orElseThrow(() -> new RuntimeException("Commande non trouvée"));
        commande.setStatus(CommandeStatus.CANCELLED);
        Commande updateCommande = this.commandeRepository.save(commande);
        return CommandeMapper.toCommandeResponseDTO(updateCommande);
        
       
    }

    public AddressResponseDTO extraireAddress(CommandeUserInfo cui){
        AddressResponseDTO address = new AddressResponseDTO(); 
        if(cui != null){
            address.setNumeroEtvoie(cui.getNumeroEtvoie());
            if(cui.getComplementAdresse() != null)
                address.setComplementAddress(cui.getComplementAdresse());
            address.setCodePostal(cui.getCodePostal());
            address.setVille(cui.getVille());
            address.setPays(cui.getPays());
        }
        return address;
    }

    private Sort buildSort(String sort) {
        return switch (sort) {
            case "DATE_ASC"  -> Sort.by(Sort.Direction.ASC, "createdAt");
            case "DATE_DESC" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "STATUS_ASC"   -> Sort.by(Sort.Direction.ASC, "status");
            case "STATUS_DESC"  -> Sort.by(Sort.Direction.DESC, "status");
            default -> Sort.unsorted();
        };
    }  

}
