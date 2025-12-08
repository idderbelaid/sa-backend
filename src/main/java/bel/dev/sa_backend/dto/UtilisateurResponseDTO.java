package bel.dev.sa_backend.dto;


import java.util.HashSet;
import java.util.Set;

import bel.dev.sa_backend.Enums.TypeDeRole;
import bel.dev.sa_backend.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UtilisateurResponseDTO {

    private String id;
    private String nom;
    private String prenom;
    private String email;
    private Set<TypeDeRole> roles = new HashSet<>();

   

    // Getters & Setters

}