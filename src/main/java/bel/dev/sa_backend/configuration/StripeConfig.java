package bel.dev.sa_backend.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StripeConfig {
    
  public StripeConfig(@Value("${stripe.secret-key}") String secretKey) {
    // Initialise le client Stripe au démarrage
    com.stripe.Stripe.apiKey = secretKey;
  }

}
