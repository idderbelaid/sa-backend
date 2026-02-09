package bel.dev.sa_backend.dto;

import java.time.Instant;

import bel.dev.sa_backend.Enums.CommandeStatus;
import bel.dev.sa_backend.controller.requestDTO.ItemCommandeDTO;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;




@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CommandeResponseDTO{
   
    private String id;
    private String numeroCommande;
    private long montantTotal;
    private CommandeStatus statut;
    private ItemCommandeDTO[] itemsCommande;
    private Instant dateCreation;

}
