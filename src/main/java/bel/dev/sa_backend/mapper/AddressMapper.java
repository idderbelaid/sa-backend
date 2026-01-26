package bel.dev.sa_backend.mapper;

import bel.dev.sa_backend.dto.AddressDTO;
import bel.dev.sa_backend.entities.Address;


public class AddressMapper {

    public static AddressDTO toDTOAddress(Address address) {
        return new AddressDTO(
            address.getId(),
            address.getNumeroEtvoie(),
            address.getComplementAddress(),
            address.getVille(),
            address.getCodePostal(),
            address.getPays()
        );
           
    }

}
