package bel.dev.sa_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import bel.dev.sa_backend.Enums.TypeSentiment;
import bel.dev.sa_backend.controller.requestDTO.SentimentRequest;


import bel.dev.sa_backend.dto.SentimentDTO;
import bel.dev.sa_backend.entities.Produit;
import bel.dev.sa_backend.entities.Sentiment;
import bel.dev.sa_backend.entities.Utilisateur;
import bel.dev.sa_backend.repository.ProduitRepository;
import bel.dev.sa_backend.repository.SentimentRepository;
import bel.dev.sa_backend.repository.UtilisateurRepository;
import bel.dev.sa_backend.mapper.SentimentMapper;

@Service
public class SentimentService {


    private UtilisateurRepository utilisateurRepository;
    private SentimentRepository sentimentRepository;

    private final ProduitRepository produitRepository;
 

    public SentimentService(SentimentRepository sentimentRepository, 
        UtilisateurRepository utilisateurRepository, 
        ProduitRepository produitRepository) {
        this.sentimentRepository = sentimentRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.produitRepository = produitRepository;

    }

    public void creer(SentimentRequest sentiment){
        Utilisateur user = this.utilisateurRepository.findByEmail(sentiment.getUtilisateurEmail())
                                    .orElseThrow(()-> new UsernameNotFoundException("Aucun utilisateur à cet identifiant"));;
        Produit produit = this.produitRepository.findById(sentiment.getProduitId()).orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        //Utilisateur user =(Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Sentiment commentaire = new Sentiment();
        commentaire.setTexte(sentiment.getTexte());
        commentaire.setUtilisateur(user);
        commentaire.setProduit(produit);
        commentaire.setCreation(new java.util.Date());
        //commentaire le sentiment

        if(sentiment.getTexte().contains("pas"))
            commentaire.setType(TypeSentiment.NEGATIF);
        else
            commentaire.setType(TypeSentiment.POSITIF);
        this.sentimentRepository.save(commentaire);
    }


    
    public List<SentimentDTO> rechercher(TypeSentiment type)
    {
        if(type == null){
            return this.sentimentRepository.findAll()
                        .stream()
                        .map(sentiment -> SentimentMapper.toResponseDTO(sentiment))
                        .collect(Collectors.toList());
        }else{
            return this.sentimentRepository.findByType(type).stream()
                        .map(sentiment -> SentimentMapper.toResponseDTO(sentiment))
                        .collect(Collectors.toList());
        }
        
    }

    public void supprimer(int id) {
        this.sentimentRepository.deleteById(id);
    }

	public List<SentimentDTO> rechercherParProduit(String produitId) {
		Produit produit = this.produitRepository.findById(produitId).orElseThrow(() -> new RuntimeException("Produit non trouvé"));
        return this.sentimentRepository.findByProduit(produit).stream()
                        .map(sentiment -> SentimentMapper.toResponseDTO(sentiment))
                        .collect(Collectors.toList());
	}

}
