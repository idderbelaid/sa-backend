package bel.dev.sa_backend.mapper;

import java.util.function.Function;


import org.springframework.stereotype.Component;

import bel.dev.sa_backend.dto.ClientDto;
import bel.dev.sa_backend.entities.Client;

@Component
public class ClientMapper implements Function<Client, ClientDto>{

    public ClientDto apply(Client client){
        return new ClientDto(client.getId(), client.getEmail(), client.getTelephone());
    }

}
