package bel.dev.sa_backend.service.rabbitMQ;

import java.util.Map;


import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import bel.dev.sa_backend.entities.Utilisateur;


import lombok.extern.slf4j.Slf4j;


@Slf4j
@Component
public class RabbitMQService {

    private  RabbitTemplate template;
    private  String exchangeName ;
   

    public RabbitMQService(RabbitTemplate template, @Value("${application.exchange.user.name}") String exchangeName, @Value("${application.exchange.user.routing-key}") String routineKey) {
        this.template = template;
        this.exchangeName = exchangeName;
        this.routineKey = routineKey;
    }
    
    public void publier(Utilisateur user){
        log.info("Publication d'un nouvel utilisateur dans RabbitMQ");
        
      
        this.template.convertAndSend(exchangeName, 
                                    null, 
                                    transformerUtilisateur(user),
                                    messageProcessor -> {
                                        final Map<String, String> headers = Map.of("type", "user", "action", "new");
                                        messageProcessor.getMessageProperties().getHeaders().putAll(headers);
                                        return messageProcessor;
                                    }
        );
        log.info("Message publié dans RabbitMQ");
    }

    
    private Utilisateur transformerUtilisateur(Utilisateur user) {
    user.setNom(user.getNom().toUpperCase()); // Exemple : nom en majuscules
    user.setEmail(user.getEmail().toUpperCase()); // Exemple : activation automatique
    return user;
}

}
