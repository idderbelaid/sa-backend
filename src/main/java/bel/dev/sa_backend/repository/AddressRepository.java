package bel.dev.sa_backend.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import bel.dev.sa_backend.entities.Address;


public interface AddressRepository extends CrudRepository<Address, String>{

    Optional<Address> findById(String id);

}
