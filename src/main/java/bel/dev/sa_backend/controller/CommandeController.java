package bel.dev.sa_backend.controller;
import java.security.Principal;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


import bel.dev.sa_backend.controller.requestDTO.CommandeInviteRequest;
import bel.dev.sa_backend.controller.requestDTO.UpdateStatusRequest;
import bel.dev.sa_backend.dto.CommandeLivraisonResponseDTO;
import bel.dev.sa_backend.dto.CommandeResponseDTO;
import bel.dev.sa_backend.dto.PageResponse;
import bel.dev.sa_backend.service.CommandeService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping(path = "commande")
public class CommandeController {

    private final CommandeService commandeService;



    @PostMapping(path = "creer", consumes = "Application/Json")
    public CommandeResponseDTO creerCommandeInvite(@RequestBody @Valid CommandeInviteRequest commande, Principal principal,
    @RequestHeader(value = "X-Session-Id", required = false) String sessionId) {

        if (principal != null) {
            // Utilisateur authentifié
            String username = principal.getName();
            return commandeService.creer(commande, username, null );
        }

        // Utilisateur invité
        if (sessionId == null || sessionId.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, 
                    "Pour un invité, le header X-Session-Id est obligatoire."
            );
        }
       
        return commandeService.creer(commande, null, sessionId);
        
        
    }

    @GetMapping(path="retreive", produces="Application/Json")
    public @ResponseBody PageResponse<CommandeResponseDTO> retreiveUserCommande( Principal principal,
         @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    ) {
        if (principal == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, 
                    "Authentification requise pour accéder à cette ressource."
            );
        }
        System.out.println("logique de recup commandes user");
        String username = principal.getName();
        try {
            return commandeService.retreive(username, search, page, size, sort);
       } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Erreur lors de la création de la commande invité."
            );
        }
        
    }


    @GetMapping(path="admin/retreive", produces="Application/Json")
    public @ResponseBody PageResponse<CommandeResponseDTO> adminRetreiveCommandes(Principal principal,
         @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String sort
    ) {
        if (principal == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, 
                    "Authentification requise pour accéder à cette ressource."
            );
        }

        System.out.println("logique de recup commandes admin");
        String username = principal.getName();
        System.out.println("username dans le controller admin retreive commandes : " + username + ", search : " + search + ", page : " + page + ", size : " + size + ", sort : " + sort);
        try {
            return commandeService.adminRetreiveAllCommandes(username, search, page, size, sort);
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Erreur lors de la récupération des commandes admin."+e.getMessage()
            );
        }
        
    }

    @GetMapping(path="admin/retreive/{id}", produces="Application/Json")
    public CommandeResponseDTO adminRetreiveCommandeById( Principal principal, @PathVariable String id) {
         if (principal == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, 
                    "Authentification requise pour accéder à cette ressource."
            );
        }

        String username = principal.getName();
        UUID uuid = UUID.fromString(id);
        return commandeService.getCommandeById( username, uuid);
    }
    @PostMapping(path="admin/update-status/{id}", produces="Application/Json")
    public CommandeResponseDTO adminUpdateCommandeStatus(Principal principal, @PathVariable String id, @RequestBody @Valid UpdateStatusRequest status) {
         if (principal == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, 
                    "Authentification requise pour accéder à cette ressource."
            );
        }

        String username = principal.getName();
        UUID uuid = UUID.fromString(id);
        return commandeService.updateCommandeStatus( username, uuid, status.getStatus());
    }
    @GetMapping(path="user/info-commande/{id}", produces="Application/Json")
    public CommandeLivraisonResponseDTO userRetreiveCommandeById( Principal principal, @PathVariable String id) {
         if (principal == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, 
                    "Authentification requise pour accéder à cette ressource."
            );
        }

        String username = principal.getName();
        UUID uuid = UUID.fromString(id);
        return commandeService.getUserCommandeById( username, uuid);
    }
    @PostMapping(path="user/cancel-commande/{id}", produces="Application/Json")
    public CommandeResponseDTO adminUpdateCommandeStatus(Principal principal, @PathVariable String id) {
         if (principal == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, 
                    "Authentification requise pour accéder à cette ressource."
            );
        }

        String username = principal.getName();
        UUID uuid = UUID.fromString(id);
        return commandeService.cancelUserCommande( username, uuid);
    }


    

}
