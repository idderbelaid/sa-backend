package bel.dev.sa_backend.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import bel.dev.sa_backend.Enums.TypeSentiment;
import bel.dev.sa_backend.dto.AuthentificationDTO;
import bel.dev.sa_backend.dto.SentimentDTO;
import bel.dev.sa_backend.dto.UtilisateurCreationDTO;
import bel.dev.sa_backend.dto.UtilisateurResponseDTO;
import bel.dev.sa_backend.service.JwtService;
import bel.dev.sa_backend.service.UtilisateurService;
import bel.dev.sa_backend.service.ValidationService;
import lombok.AllArgsConstructor;


@AllArgsConstructor
@RestController
@RequestMapping(path = "/utilisateurs")
public class UtilisateurController {


    private AuthenticationManager authenticationManager;
    private JwtService jwtService;
    private UtilisateurService utilisateurService;
    private ValidationService validationService;
    
    @PostMapping(path = "/inscription")
    public ResponseEntity<UtilisateurResponseDTO> inscription(@Valid @RequestBody UtilisateurCreationDTO utilisateur) {
        UtilisateurResponseDTO reponse = this.utilisateurService.inscription(utilisateur);
        return  ResponseEntity.status(HttpStatus.CREATED).body(reponse);
    }
    @PostMapping(path = "/activation")
    public void activationCompte(@RequestBody Map<String, String> activation) {
        
        this.validationService.activationCompte(activation);
    }
    @PostMapping(path = "/modifierPassword")
    public void modifierPassword(@RequestBody Map<String, String> email) {
        this.utilisateurService.modifierPassword(email);
    }

    @PostMapping(path = "/reinitiliserPassword")
    public void reinitiliserPassword(@RequestBody Map<String, String> parametres) {
        this.utilisateurService.reinitiliserPassword(parametres);
    }

   @PostMapping(path = "/refresh-token")
    public @ResponseBody  Map<String, String> refreshToken(@RequestBody Map<String, String> refreshTokenRequest) {
        return this.jwtService.refreshToken(refreshTokenRequest);
    }

    @PostMapping(path = "/connexion")
    public Map<String, String> connexion(@RequestBody AuthentificationDTO authDTO ) {
        System.out.println("La connexion ...");
        final Authentication authentication = authenticationManager
            .authenticate(new UsernamePasswordAuthenticationToken(authDTO.username(), authDTO.password()));
            
        System.out.println("le user est : " +authentication.isAuthenticated());
        if(authentication.isAuthenticated()) {
            return this.jwtService.genererToken(authDTO.username());
        }
        return null;
    }

    @PostMapping(path = "/deconnexion")
    public void deconnexion() {   
        this.jwtService.deconnexion();
    }
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping(path = "utilisateurs")
    public @ResponseBody List<UtilisateurResponseDTO> liste(){
        return this.utilisateurService.liste();
    }



}
