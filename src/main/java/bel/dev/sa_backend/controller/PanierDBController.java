package bel.dev.sa_backend.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import bel.dev.sa_backend.dto.PanierDTO;

import bel.dev.sa_backend.service.PanierService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("cart")
public class PanierDBController {

    private final PanierService panierService;

    @PostMapping("/merge")
    public PanierDTO merge(@Valid @RequestBody MergeRequest req, Principal principal) {
        String userId = principal.getName(); // id du user depuis le token
        PanierDTO merged = panierService.mergeGuestCartIntoUser(req.getSessionId(), userId);
        return merged;
    }

    @GetMapping("/me")
    public PanierDTO myCart(Principal principal) {
        String userId = principal.getName();
        System.out.println("le user : -------------------"+userId);
        return this.panierService.findOrCreateUserCart(userId);
        
    }
    
    // GET: lister le contenu du panier
    @GetMapping
    public ResponseEntity<List<CartItemResponse>> list(@PathVariable String cartId) {
        return ResponseEntity.ok(panierService.listItems(cartId));
    }

    // POST: ajouter/sauvegarder un item
    @PostMapping
    public ResponseEntity<CartItemResponse> add(
            @PathVariable String cartId,
            @Valid @RequestBody CreateCartItemRequest request
    ) {
        CartItemResponse created = panierService.addItem(cartId, request);
        // Location vers la ressource de l'item
        return ResponseEntity.ok(created);
    }

    // PUT: mettre à jour quantité d’un item
    @PutMapping("/{itemId}")
    public ResponseEntity<CartItemResponse> update(
            @PathVariable String cartId,
            @PathVariable String itemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return ResponseEntity.ok(panierService.updateItem(cartId, itemId, request));
    }

    // DELETE: supprimer un item du panier
    @DeleteMapping("/{cartId}/{itemId}")
    public ResponseEntity<Void> delete(
            @PathVariable String cartId,
            @PathVariable String itemId
    ) {
        panierService.deleteItem(cartId, itemId);
        return ResponseEntity.noContent().build();
    }

    // DELETE: vider tout le panier
    @DeleteMapping("/{cartId}")
    public ResponseEntity<Void> clear(@PathVariable String cartId) {
        panierService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }



    public record CreateCartItemRequest(
            @NotNull String productId,
            @Min(1) int quantity,
            @NotNull BigDecimal unitPrice
    ) {}

    public record UpdateCartItemRequest(@Min(1) int quantity) {}

    public record CartItemResponse(
            String id, String productId, int quantity, BigDecimal unitPrice, BigDecimal lineTotal
    ) {}



}
