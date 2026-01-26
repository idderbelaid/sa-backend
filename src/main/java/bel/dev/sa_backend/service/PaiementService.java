package bel.dev.sa_backend.service;
import org.springframework.stereotype.Service;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;



@Service
public class PaiementService {
       
public PaymentIntent createPaymentIntent(long amountInMinor, String currency) throws StripeException {
    PaymentIntentCreateParams params =
        PaymentIntentCreateParams.builder()
            .setAmount(amountInMinor)               // ex: 1200 = 12.00 EUR
            .setCurrency(currency)                  // "eur", "usd", ...
            .setAutomaticPaymentMethods(
                PaymentIntentCreateParams.AutomaticPaymentMethods
                    .builder().setEnabled(true).build()
            )
            // Optionnel : metadata, description, customer, receipt_email, etc.
            .build();

    return PaymentIntent.create(params);
  }


}
