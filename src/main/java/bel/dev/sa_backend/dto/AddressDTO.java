package bel.dev.sa_backend.dto;

public record AddressDTO(
     String id,
     String numeroEtvoie,
     String complementAddress,
     String ville,
     String codePostal,
     String pays
    ) {

}


