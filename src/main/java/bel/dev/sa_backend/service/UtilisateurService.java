package bel.dev.sa_backend.service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import bel.dev.sa_backend.Enums.TypeDeRole;
import bel.dev.sa_backend.dto.UtilisateurCreationDTO;
import bel.dev.sa_backend.dto.UtilisateurResponseDTO;

import bel.dev.sa_backend.entities.Role;
import bel.dev.sa_backend.entities.Utilisateur;
import bel.dev.sa_backend.entities.Validation;
import bel.dev.sa_backend.mapper.SentimentMapper;
import bel.dev.sa_backend.mapper.UtilisateurMapper;
import bel.dev.sa_backend.repository.RoleRepository;
import bel.dev.sa_backend.repository.UtilisateurRepository;
import bel.dev.sa_backend.service.rabbitMQ.RabbitMQService;
import bel.dev.sa_backend.service.rabbitMQ.KafkaProducer;
import lombok.AllArgsConstructor;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
@Service
public class UtilisateurService implements UserDetailsService {

    private ValidationService validationService;
    private UtilisateurRepository utilisateurRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private RoleRepository roleRepository;
    private RabbitMQService rabbitMQService;
    private KafkaProducer KafkaProducer;



    
    public UtilisateurRepository getUtilisateurRepository() {
        return utilisateurRepository;
    }

    public void setUtilisateurRepository(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    public UtilisateurResponseDTO inscription(UtilisateurCreationDTO utilisateur) {
        if(!utilisateur.getEmail().contains("@")) {
            throw new IllegalStateException("L'email n'est pas valide");
        }
          if(!utilisateur.getEmail().contains(".")) {
            throw new IllegalStateException("L'email n'est pas valide");
        }

        this.utilisateurRepository.findByEmail(utilisateur.getEmail())
            .ifPresent(u -> {
                throw new IllegalStateException("L'email existe déjà");
            });
        utilisateur.setId(this.generateCustomId());
        String passwordString = bCryptPasswordEncoder.encode(utilisateur.getPassword());
        utilisateur.setPassword(passwordString);

        
        
        Set<Role> roles = new HashSet<>();


        Role roleUser = new Role();
        roleUser.setLibelle(TypeDeRole.USER);
        roleUser = this.roleRepository.save(roleUser);
        roles.add(roleUser);
        utilisateur.setRoles(roles);

        Utilisateur user = UtilisateurMapper.toEntity(utilisateur);
        Utilisateur userSaved = utilisateurRepository.save(user);
        validationService.enregister(userSaved);
        //this.rabbitMQService.publier(userSaved);
        this.KafkaProducer.sendMessage(UtilisateurMapper.toResponseDTO(userSaved));
        return UtilisateurMapper.toResponseDTO(userSaved);
    }
    public String generateCustomId() {
        String prefix = "USR";
        String date = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        String random = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return prefix + "-" + date + "-" + random;
    }

    @Override
    public Utilisateur loadUserByUsername(String username) throws UsernameNotFoundException {
        return this.utilisateurRepository
                .findByEmail(username)
                                    .orElseThrow(()-> new UsernameNotFoundException("Aucun utilisateur à cet identifiant"));
      
    }

    public Utilisateur lireOuCreer(Utilisateur client) {
        Utilisateur userBDD = this.loadUserByUsername(client.getEmail());
        if(userBDD == null)
            userBDD = this.utilisateurRepository.save(client);
        
        return userBDD; 
    }

    public void modifierPassword(Map<String, String> param) throws UsernameNotFoundException{
        Utilisateur user = this.utilisateurRepository.findByEmail(param.get("email"))
            .orElseThrow( () -> new UsernameNotFoundException("AUcun user avec cet identificant"));
        
        validationService.enregister(user);
    }

    public void reinitiliserPassword(Map<String, String> parametres) {
        Utilisateur user = this.utilisateurRepository.findByEmail(parametres.get("email"))
            .orElseThrow( () -> new UsernameNotFoundException("AUcun user avec cet identificant"));
        Validation validation = this.validationService.findCodeBDD(parametres.get("code"));
        if(validation.getUtilisateur().getEmail().equals(user.getEmail()) && validation.getExpiresAt().isAfter(Instant.now())){
            String passwordString = bCryptPasswordEncoder.encode(parametres.get("password"));
            user.setPassword(passwordString);
            
            utilisateurRepository.save(user);
            validationService.activationCompte(parametres);
        }
        else {
            throw new IllegalArgumentException("Code invalide ou expiré");
        }
        
    
        

    }

    public List<UtilisateurResponseDTO> liste() {
        log.info("rLister les users ");
        
        final Iterable<Utilisateur> utilisateurs = this.utilisateurRepository.findAll();
        List<UtilisateurResponseDTO> users = new java.util.ArrayList<>();
        for (Utilisateur utilisateur : utilisateurs) {
            users.add(UtilisateurMapper.toResponseDTO(utilisateur));
        }
        return users;
    }

   

   


   

}
