package bel.dev.sa_backend.controller;
import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import bel.dev.sa_backend.controller.requestDTO.CommandeInviteRequest;
import bel.dev.sa_backend.dto.CommandeResponseDTO;
import bel.dev.sa_backend.service.CommandeService;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping(path = "commande")
public class CommandeController {

    private CommandeService commandeService;
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
    public List<CommandeResponseDTO> retreiveCommande( Principal principal) {
        if (principal == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, 
                    "Authentification requise pour accéder à cette ressource."
            );
        }

        String username = principal.getName();
        return commandeService.retreive( username);
    }
    @GetMapping(path="admin/retreive", produces="Application/Json")
    public List<CommandeResponseDTO> adminRetreiveCommandes( Principal principal) {
         if (principal == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, 
                    "Authentification requise pour accéder à cette ressource."
            );
        }

        String username = principal.getName();
        return commandeService.adminRetreiveCommandes( username);
    }

    

}
