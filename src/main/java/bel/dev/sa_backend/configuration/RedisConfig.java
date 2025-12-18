package bel.dev.sa_backend.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;



import bel.dev.sa_backend.dto.GuestPanierDTO;

@Configuration
public class RedisConfig {

    
    @Bean
    public RedisTemplate<String, GuestPanierDTO> guestCartRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, GuestPanierDTO> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Clés en String
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());

        // Valeurs en JSON typé GuestCartDTO
        Jackson2JsonRedisSerializer<GuestPanierDTO> serializer =
                new Jackson2JsonRedisSerializer<>(GuestPanierDTO.class);
    

        template.setValueSerializer(serializer);
        template.setHashValueSerializer(serializer);

        template.afterPropertiesSet();
        return template;
    }
    
  @Bean
    public LettuceConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory("localhost", 6379);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(LettuceConnectionFactory factory) {
        return new StringRedisTemplate(factory);
    }


}
