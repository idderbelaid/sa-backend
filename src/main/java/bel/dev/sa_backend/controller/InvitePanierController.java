package bel.dev.sa_backend.controller;

import java.math.BigDecimal;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import bel.dev.sa_backend.dto.GuestPanierDTO;
import bel.dev.sa_backend.service.InvitePanierService;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("guest-cart")
public class InvitePanierController {

    private final InvitePanierService invitePanierService;

    
    
 // 1) Premier ajout : sans sessionId (le serveur en génère un)
    @PostMapping("/items")
    public ResponseEntity<AddItemResponse> addFirstItem(@RequestBody AddItemRequest req) {
        String sessionId = java.util.UUID.randomUUID().toString();
        var panier = this.invitePanierService.addItem(sessionId, req.productId(), req.quantity(), req.unitPrice(), 1800);
        return ResponseEntity.ok(new AddItemResponse(sessionId, panier));
    }

    @PostMapping(path = "{sessionId}/items")
    public ResponseEntity<GuestPanierDTO> addItem(
            @PathVariable String sessionId,
            @RequestBody AddItemRequest req
    ){
        var panier = this.invitePanierService.addItem(sessionId, req.productId, req.quantity, req.unitPrice, 1800);

        return ResponseEntity.ok(panier);


    }

    public record AddItemRequest(String productId, int quantity, BigDecimal unitPrice) {}
    public record AddItemResponse(String sessionId, GuestPanierDTO cart) {}

}
