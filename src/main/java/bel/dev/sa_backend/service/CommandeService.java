package bel.dev.sa_backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.stereotype.Service;

import bel.dev.sa_backend.Enums.PaiementStatus;
import bel.dev.sa_backend.controller.requestDTO.CommandeInviteRequest;
import bel.dev.sa_backend.controller.requestDTO.InfoUserInviteDTO;
import bel.dev.sa_backend.controller.requestDTO.ItemCommandeDTO;
import bel.dev.sa_backend.dto.AddressDTO;
import bel.dev.sa_backend.dto.CommandeResponseDTO;
import bel.dev.sa_backend.entities.Commande;
import bel.dev.sa_backend.entities.CommandeItem;
import bel.dev.sa_backend.entities.CommandeUserInfo;
import bel.dev.sa_backend.entities.Paiement;
import bel.dev.sa_backend.entities.Panier;
import bel.dev.sa_backend.entities.Produit;
import bel.dev.sa_backend.entities.Utilisateur;
import bel.dev.sa_backend.mapper.CommandeMapper;
import bel.dev.sa_backend.repository.CommandeRepository;
import bel.dev.sa_backend.repository.PanierRepository;
import bel.dev.sa_backend.repository.ProduitRepository;
import bel.dev.sa_backend.repository.UtilisateurRepository;
import io.micrometer.common.lang.Nullable;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@AllArgsConstructor
@Service
public class CommandeService {

    private UtilisateurRepository utilisateurRepository;
    private ProduitRepository produitRepository;
    private CommandeRepository commandeRepository;
    private PanierService panierService;
    private InvitePanierService invitePanierService;
    private PanierRepository panierRepository;


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
            System.out.println("Calcul du total de la ligne");
            System.out.println("itemNew.getQuantity() : " + itemNew.getQuantity());
            System.out.println("itemNew.getUnitPriceExclTax() : " + itemNew.getUnitPriceExclTax());
            Long lineTotal = itemNew.getUnitPriceExclTax() * itemNew.getQuantity();
            System.out.println("lineTotal : " + lineTotal);
            itemNew.setLineTotalInclTax(lineTotal);
            itemNew.setOrder(order); // Lien vers la commande
            totalCommande += lineTotal;
            System.out.println("montant de la commande "+ totalCommande);
            itemsCommande.add(itemNew);
        }
        order.setItems(itemsCommande);
        
        //Enregistrer le paiement (simple pour l’instant)
        Paiement paiement = new Paiement();
        paiement.setAmount(totalCommande);
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

        order.attachUserInfo(user_info);
        order.setMontantTotal(totalCommande);
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



	public List<CommandeResponseDTO> retreive(String username) {
		Utilisateur user = this.utilisateurRepository.findByEmail(username).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        List<Commande> commandes =  this.commandeRepository.findByUserInfo_Utilisateur_Id(user.getId());
        List<CommandeResponseDTO> response = new ArrayList<>();
        for(Commande cmd : commandes){
            response.add(CommandeMapper.toCommandeResponseDTO(cmd));
        }
        return response;
	}
}
