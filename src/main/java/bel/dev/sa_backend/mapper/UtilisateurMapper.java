package bel.dev.sa_backend.mapper;



import java.util.Set;
import java.util.stream.Collectors;

import bel.dev.sa_backend.dto.UtilisateurCreationDTO;
import bel.dev.sa_backend.dto.UtilisateurResponseDTO;
import bel.dev.sa_backend.entities.Role;
import bel.dev.sa_backend.entities.Utilisateur;
import bel.dev.sa_backend.repository.RoleRepository;

public class UtilisateurMapper {



    // Mapping du DTO de création vers l'entité
    public static Utilisateur toEntity(UtilisateurCreationDTO dto) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(dto.getId());
        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setPassword(dto.getPassword());
        
        Set<Role> attachedRoles = dto.getRoles().stream()
                .map(role -> {
                    Role r = new Role();
                    r.setId(role.getId());
                    r.setLibelle(role.getLibelle());
                    return r;
                })
                .collect(Collectors.toSet());
        utilisateur.setRoles(attachedRoles);

       
        return utilisateur;
    }

    // Mapping de l'entité vers le DTO de réponse
    public static UtilisateurResponseDTO toResponseDTO(Utilisateur utilisateur) {
        System.out.println("toResponseDTO " );
        UtilisateurResponseDTO dto = new UtilisateurResponseDTO();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setEmail(utilisateur.getEmail());
        System.out.println("je pense que le problème vientd e ce qui suit " );
        dto.setRoles(utilisateur.getRoles().stream()
                                 .map(Role::getLibelle).collect(Collectors.toSet()));
        System.out.println("je suis la le problème est pas la " );
        return dto;
    }
}