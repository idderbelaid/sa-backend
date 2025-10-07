package bel.dev.sa_backend.mapper;

import bel.dev.sa_backend.dto.SentimentDTO;

import bel.dev.sa_backend.entities.Sentiment;


public class SentimentMapper {


     public static SentimentDTO toResponseDTO(Sentiment sentiment) {
        SentimentDTO dto = new SentimentDTO();
        dto.setId(sentiment.getId());
        dto.setTexte(sentiment.getTexte());
        dto.setUtilisateurEmail(sentiment.getUtilisateur().getEmail());
        return dto;
    }
}
