package bel.dev.sa_backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;

import bel.dev.sa_backend.service.PaiementService;


record CreatePaymentRequest(Long amount, String currency, String cartId, String sessionId) {}
record CreatePaymentResponse(String clientSecret,String cartId, String sessionId ) {}


@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaiementService paymentService;
    
    public PaymentController(PaiementService paymentService) {
        this.paymentService = paymentService;
    }

    
    @PostMapping("/create-payment-intent")
    public ResponseEntity<CreatePaymentResponse> create(@RequestBody CreatePaymentRequest req)
      throws StripeException {

        // ⚠️ Par sécurité, calcule idéalement amount côté serveur (depuis panier/session)
        long amount = req.amount();        // ex. 1200 (centimes)
        String currency = req.currency();  // ex. "eur"

        PaymentIntent pi = paymentService.createPaymentIntent(amount, currency);
        return ResponseEntity.ok(new CreatePaymentResponse(pi.getClientSecret(), req.cartId(), req.sessionId()));
    }


}
