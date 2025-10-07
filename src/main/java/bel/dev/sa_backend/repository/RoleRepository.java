package bel.dev.sa_backend.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import bel.dev.sa_backend.Enums.TypeDeRole;
import bel.dev.sa_backend.entities.Role;

public interface RoleRepository extends CrudRepository<Role, Integer>{
    Optional<Role> findByLibelle(TypeDeRole user);
}
