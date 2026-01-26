package bel.dev.sa_backend.controller;


import java.security.Principal;
import java.util.List;


import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import bel.dev.sa_backend.dto.AddressDTO;
import bel.dev.sa_backend.dto.ProfileDTO;
import bel.dev.sa_backend.service.AddressService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("address")
public class AddressController {


    private final AddressService addressService;


    


    // ✅ READ : Récupérer une adresse par ID
    @GetMapping("/{id}")
    public AddressDTO getAddressById(@PathVariable String id) {
        return addressService.getAddressById(id);
    }

    // ✅ READ ALL : Récupérer toutes les adresses de l'utilisateur
    @GetMapping
    public List<AddressDTO> getAllAddresses(Principal principal) {
        System.out.println("le user : -------------------"+principal);
        String userId = principal.getName();
        System.out.println("le user : -------------------"+userId);
        return addressService.getAllAddresses(userId);
    }

    // ✅ UPDATE : Modifier une adresse existante
    @PutMapping("/{id}")
    public AddressDTO updateAddress(@PathVariable String id, @RequestBody AddressDTO addressDTO, Principal principal) {
        String userId = principal.getName();
        return addressService.updateAddress(userId, id, addressDTO);
    }

    // ✅ DELETE : Supprimer une adresse
    @DeleteMapping("/{id}")
    public void deleteAddress(@PathVariable String id, Principal principal) {
        String userId = principal.getName();
        addressService.deleteAddress(userId, id);
    }


    // ✅ Récupérer l'adresse de l'utilisateur connecté
    @GetMapping("/me")
    public ProfileDTO myAddress(Principal principal) {
        System.out.println("le user : -------------------"+principal);
        String userId = principal.getName();
        System.out.println("Utilisateur connecté : " + userId);
        return addressService.findMyAddress(userId);
    }

    // ✅ CREATE : Ajouter une nouvelle adresse
    @PostMapping
    public AddressDTO createAddress(@RequestBody AddressDTO addressDTO, Principal principal) {
        String userId = principal.getName();
        return addressService.createAddress(userId, addressDTO);
    }

}
