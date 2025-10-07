package bel.dev.sa_backend.service;

import java.time.Instant;

import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Random;

import org.springframework.stereotype.Service;

import bel.dev.sa_backend.entities.Utilisateur;
import bel.dev.sa_backend.entities.Validation;
import bel.dev.sa_backend.repository.UtilisateurRepository;
import bel.dev.sa_backend.repository.ValidationRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@AllArgsConstructor
@Service
public class ValidationService {


    private ValidationRepository validationRepository;
    private NotificationService notificationService;
    private UtilisateurRepository utilisateurRepository;

    public void enregister(Utilisateur utilisateur) {
        Validation validation = new Validation();
        validation.setUtilisateur(utilisateur);
        validation.setCreatedAt(Instant.now());
        validation.setExpiresAt(validation.getCreatedAt().plus(10, ChronoUnit.MINUTES));
        Random random = new Random();
        random.nextInt(999999);
        String code =String.format("%06d", random.nextInt(999999));
        validation.setCode(code);
        validation = this.validationRepository.save(validation);
        this.notificationService.envoyer(validation);
        
    }

    public void activationCompte(Map<String,String> activation) {
        String code = activation.get("code");
        Validation validation = this.validationRepository.findByCode(code).orElseThrow(()-> new IllegalArgumentException("Code invalide"));
        if(validation.getExpiresAt().isBefore(Instant.now())) {
            throw new IllegalArgumentException("Code expiré");
        }
        Utilisateur utilisateurActive  = this.utilisateurRepository.findById(validation.getUtilisateur().getId())
            .orElseThrow(()-> new IllegalArgumentException("Utilisateur introuvable"));
        validation.setActivatedAt(Instant.now());
        this.validationRepository.save(validation);
        utilisateurActive.setEstActif(true);
        this.utilisateurRepository.save(utilisateurActive);
        //this.validationRepository.delete(validation);
       
    }

    public Validation findCodeBDD(String code) {
        Validation validation = this.validationRepository.findByCode(code) 
            .orElseThrow( () -> new RuntimeException("Le code est invalides ou inexistant"));
        return validation;
        
    }

    public Validation findByUtilisateur(Utilisateur user) {
        Validation validation = this.validationRepository.findByUtilisateur(user) 
            .orElseThrow( () -> new RuntimeException("Le code est invalides ou inexistant"));
        return validation;
    }

    public void update(Utilisateur user) {
       Validation validation = this.findByUtilisateur(user);
       if(validation != null ){
            log.info("update validation");
            Random random = new Random();
            random.nextInt(999999);
            String code =String.format("%06d", random.nextInt(999999));
            log.info("code validation : " + code);
            validation.setCode(code);
            validation.setCreatedAt(Instant.now());
            validation.setExpiresAt(validation.getCreatedAt().plus(10, ChronoUnit.MINUTES));
            validation.setActivatedAt(null);
            log.info("save validation  ");
            this.validationRepository.save(validation);
        }

        this.enregister(user);
    }

   

    
}
