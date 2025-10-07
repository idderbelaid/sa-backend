package bel.dev.sa_backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import bel.dev.sa_backend.entities.Client;

public interface ClientRepository extends JpaRepository<Client, Integer>{

    Client findByEmail(String email);
}
