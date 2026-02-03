package bel.dev.sa_backend.controller;

import java.math.BigDecimal;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
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
        var panier = this.invitePanierService.addItem(sessionId, req.id(), req.amount(), req.price(), 1800);
        return ResponseEntity.ok(new AddItemResponse(sessionId, panier));
    }

    @PostMapping(path = "{sessionId}/items")
    public ResponseEntity<GuestPanierDTO> addItem(
            @PathVariable String sessionId,
            @RequestBody AddItemRequest req
    ){
        System.out.println("hola am here");
        var panier = this.invitePanierService.addItem(sessionId, req.id, req.amount, req.price, 1800);
        System.out.println("panier : " + panier);
        return ResponseEntity.ok(panier);


    }

    @GetMapping(path = "items/{sessionId}")
    public ResponseEntity<GuestPanierDTO> getItems(
            @PathVariable String sessionId
    ){
        var panier = this.invitePanierService.getCart(sessionId);

        return ResponseEntity.ok(panier);


    }
    @DeleteMapping(path = "items/delete/{sessionId}/{id}")
    public ResponseEntity<GuestPanierDTO> deleteItem(
            @PathVariable String sessionId,  @PathVariable String id
    ){
        var panier = this.invitePanierService.delteItemFromCart(sessionId, id, 1800);

        return ResponseEntity.ok(panier);


    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping(path = "items/clearItems/{sessionId}")
    public void deleteItem(
            @PathVariable String sessionId
    ){
        this.invitePanierService.deleteAllItems(sessionId);

    }

    
   


    public record AddItemRequest(String id,String name, int amount, BigDecimal price) {}
    public record AddItemResponse(String sessionId, GuestPanierDTO cart) {}

}
