package bel.dev.sa_backend.configuration;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;

import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;

import bel.dev.sa_backend.dto.UtilisateurResponseDTO;
import bel.dev.sa_backend.entities.Utilisateur;



@Configuration
public class UtilisateurConfiguration {


        @Bean
        public MessageConverter jsonMessageConverter(){
                ObjectMapper objectMapper=new ObjectMapper();
                return new Jackson2JsonMessageConverter(objectMapper);
        }
        @Bean
        public ProducerFactory<String, UtilisateurResponseDTO> producerFactory() {
                Map<String, Object> config = new HashMap<>();
                config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
                config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
                config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
                return new DefaultKafkaProducerFactory<>(config);
        }
        @Bean
        public KafkaTemplate<String, UtilisateurResponseDTO> kafkaTemplate() {
                return new KafkaTemplate<>(producerFactory());
        }
}
