package bel.dev.sa_backend.mapper;



import java.util.Set;
import java.util.stream.Collectors;

import bel.dev.sa_backend.dto.UtilisateurCreationDTO;
import bel.dev.sa_backend.dto.UtilisateurResponseDTO;
import bel.dev.sa_backend.entities.Role;
import bel.dev.sa_backend.entities.Utilisateur;
import bel.dev.sa_backend.repository.RoleRepository;

public class UtilisateurMapper {

    private RoleRepository rolerRepository;

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
        UtilisateurResponseDTO dto = new UtilisateurResponseDTO();
        dto.setId(utilisateur.getId());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setEmail(utilisateur.getEmail());
        return dto;
    }
}