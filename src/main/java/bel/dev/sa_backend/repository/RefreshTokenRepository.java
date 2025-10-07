package bel.dev.sa_backend.repository;

import org.springframework.data.repository.CrudRepository;

import bel.dev.sa_backend.entities.RefreshToken;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, Integer> {

}
