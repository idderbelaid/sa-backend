package bel.dev.sa_backend.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import bel.dev.sa_backend.dto.ClientDto;
import bel.dev.sa_backend.entities.Client;
import bel.dev.sa_backend.mapper.ClientMapper;
import bel.dev.sa_backend.repository.ClientRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ClientService {
    private final ClientMapper clientMapper;
    private ClientRepository clientRepository;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    

    public ClientService(ClientRepository clientRepository, ClientMapper clientMapper){
        this.clientRepository = clientRepository;
        this.clientMapper = clientMapper;
    }
    
    public List<String> getListClient(){
        return List.of("un", "deux", "trois");
    }

    public void creer(Client client){
        Client clientBDD = this.clientRepository.findByEmail(client.getEmail());
        if(clientBDD == null)
            this.clientRepository.save(client);
        
    }

    public Stream<ClientDto> rechercher(){
        return  this.clientRepository.findAll()
                    .stream().map(clientMapper);
    }

    public Client lire(int id){
        Optional<Client> client = this.clientRepository.findById(id);
        return client.orElseThrow(
            () -> new EntityNotFoundException("Aucun clinet n'existe avec cet id")
        );
    }

    public Client lireOuCreer(Client client){
        Client clientBDD = this.clientRepository.findByEmail(client.getEmail());
        if(clientBDD == null)
            clientBDD= this.clientRepository.save(client);
        
        return clientBDD; 
    }

    public void modifier(int id, Client client) {
        Client clientBDD = this.lire(id);
        if(clientBDD != null){
            clientBDD.setEmail(client.getEmail());
            clientBDD.setTelephone(client.getTelephone());
            this.clientRepository.save(clientBDD);
        }
    
    }

    


    public List<String> getTables() {
        return jdbcTemplate.queryForList(
            "SHOW TABLES", String.class
        );
    }


}
