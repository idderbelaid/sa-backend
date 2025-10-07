package bel.dev.sa_backend.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import bel.dev.sa_backend.Enums.TypeSentiment;
import bel.dev.sa_backend.dto.SentimentDTO;
import bel.dev.sa_backend.entities.Sentiment;
import bel.dev.sa_backend.entities.Utilisateur;
import bel.dev.sa_backend.repository.SentimentRepository;
import bel.dev.sa_backend.mapper.SentimentMapper;

@Service
public class SentimentService {


    private UtilisateurService utilisateurService;
    private SentimentRepository sentimentRepository;
 

    public SentimentService(SentimentRepository sentimentRepository, UtilisateurService utilisateurService){
        this.sentimentRepository = sentimentRepository;
        this.utilisateurService = utilisateurService;

    }

    public void creer(Sentiment sentiment){
        Utilisateur user = this.utilisateurService.lireOuCreer(sentiment.getUtilisateur());
        //Utilisateur user =(Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        sentiment.setUtilisateur(user);

        //Analyse le sentiment

        if(sentiment.getTexte().contains("pas"))
            sentiment.setType(TypeSentiment.NEGATIF);
        else
            sentiment.setType(TypeSentiment.POSITIF);
        this.sentimentRepository.save(sentiment);
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

}
