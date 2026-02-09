package bel.dev.sa_backend.service.rabbitMQ;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import bel.dev.sa_backend.dto.UtilisateurResponseDTO;

import lombok.extern.slf4j.Slf4j;

@Slf4j

@Service
public class KafkaProducer {

    private KafkaTemplate<String, UtilisateurResponseDTO> kafkaTemplate;
    
    @Value("${spring.kafka.topic.name}")
    private String topic;

    public KafkaProducer(KafkaTemplate<String, UtilisateurResponseDTO> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(UtilisateurResponseDTO user) {
        log.info("Publication d'un nouvel utilisateur dans my-topic kafka");
        kafkaTemplate.send(topic, user); 
        log.info("message publié");

    }

}
