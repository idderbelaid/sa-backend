package bel.dev.sa_backend.controller;


import java.util.List;
import java.util.stream.Stream;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import bel.dev.sa_backend.dto.ClientDto;
import bel.dev.sa_backend.dto.ErrorEntity;
import bel.dev.sa_backend.entities.Client;
import bel.dev.sa_backend.service.ClientService;
import jakarta.persistence.EntityNotFoundException;


@RestController
@RequestMapping(path = "client")
public class ClientController {
    
    
    private ClientService clientService;

    public ClientController(ClientService clientService){
        this.clientService = clientService;
    }

    @PostMapping(path = "creer", consumes = "Application/Json")
    public void creerClient(@RequestBody Client client){
         this.clientService.creer(client);
    }

 
    @GetMapping(path = "clientAll")
    public Stream<ClientDto> rechercher(){
        return this.clientService.rechercher();
    }

    @GetMapping(path = "{id}")
    public ResponseEntity<Object> lire(@PathVariable int id){
        try{
            Client client = this.clientService.lire(id);
            return ResponseEntity.ok(client);
       

        }catch(EntityNotFoundException exception){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorEntity(null, exception.getMessage()));
        }
       
    }
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PutMapping(path = "modifier/{id}", consumes = "Application/Json")
    public void modifier(@PathVariable int id, @RequestBody Client client){
        this.clientService.modifier(id, client);
    }


}
