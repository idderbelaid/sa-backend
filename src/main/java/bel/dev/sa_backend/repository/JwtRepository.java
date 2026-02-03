package bel.dev.sa_backend.repository;


import java.util.List;
import java.util.Optional;



import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import bel.dev.sa_backend.entities.Jwt;

public interface JwtRepository extends CrudRepository<Jwt, Integer> {

    Optional<Jwt> findByValeur(String valeur);

    @Query("SELECT j FROM Jwt j WHERE j.utilisateur.email = :email AND j.desactive = :desactive AND j.expired = :expired")
    Optional<Jwt> findUserValidToken(String email, boolean desactive, boolean expired);

    @Query("SELECT j FROM Jwt j WHERE j.utilisateur.email = :email")
    List<Jwt> findUserAllValidToken(String email);

    @Query("SELECT j FROM Jwt j WHERE j.refreshToken.valeur = :refreshToken")
    Optional<Jwt> findByRefreshToken(String refreshToken);

    
    void deleteAllByExpiredTrueAndDesactiveTrue();

}
