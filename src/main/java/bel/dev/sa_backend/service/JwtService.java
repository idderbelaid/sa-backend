package bel.dev.sa_backend.service;

import java.security.Key;
import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Base64.Decoder;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;


import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import bel.dev.sa_backend.entities.Jwt;
import bel.dev.sa_backend.entities.RefreshToken;
import bel.dev.sa_backend.entities.Utilisateur;
import bel.dev.sa_backend.repository.JwtRepository;
import bel.dev.sa_backend.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import org.springframework.transaction.annotation.Transactional;


import io.jsonwebtoken.SignatureAlgorithm;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Transactional
@AllArgsConstructor
@Service    
public class JwtService {


   
    private JwtRepository jwtRepository;
    private final String ENCRYPTION_KEY = "ae661b4ac7132005b44b4c14bf75ccdafde219b7fe7b6dc50c3f2ecacbf13da7";
    private final String BEARER = "bearer";
    private final String REFRESH = "refresh";
    private UtilisateurService utilisateurService;
    
    
    public Map<String, String> genererToken(String username) {

        Utilisateur user = (Utilisateur) this.utilisateurService.loadUserByUsername(username);
        log.info("user name : " + user.getUsername());
        this.disableOldToken(user);
        log.info("disableOldToken ok ");
        final Map<String, String> jwtMap = new HashMap<>(this.generateJWT(user));
        log.info("generateJWT ok ");
        RefreshToken refreshToken = RefreshToken.builder()
                                    .valeur(UUID.randomUUID().toString())
                                    .expire(false)
                                    .creation(Instant.now())
                                    .expiration(Instant.now().plusMillis(30*60*1000))//30 minutes
                                    .build();
        log.info("refreshToken ok ");
        final Jwt jwt =  new Jwt();
        jwt.setValeur(jwtMap.get(BEARER));
        jwt.setExpired(false);
        jwt.setDesactive(false);
        jwt.setRefreshToken(refreshToken);
        jwt.setUtilisateur(user);
        jwtRepository.save(jwt);
      

        log.info("prepare return ok :" +refreshToken.getValeur());
        jwtMap.put("refresh", refreshToken.getValeur());
        return jwtMap;
    }

    public Jwt tokenbyValue(String token){
         return this.jwtRepository.findByValeur(token).orElseThrow(()-> new RuntimeException("Aucun jeton à cet identifiant"));
    }


  
   
    @Transactional
    public void disableOldToken(Utilisateur user) {
        List<Jwt> jwtStream = jwtRepository.findUserAllValidToken(user.getEmail());
        
        
        jwtStream.forEach(
            jwt -> {
                    jwt.setDesactive(true);
                    jwt.setExpired(true);
                }
        );
        jwtRepository.saveAll(jwtStream);
        
    }

    private Map<String, String> generateJWT(Utilisateur user) {
        log.info("generateJWT en cours... ");
        final long currentTime= System.currentTimeMillis();
        log.info("currentTime ok... " + currentTime);
        final long expirationTime  = currentTime + 60*1000;
        log.info("expirationTime ok... " + expirationTime);
        final Map<String, Object> claims = Map.of(
            "nom", user.getNom(),
            Claims.EXPIRATION, new Date(expirationTime),
            Claims.SUBJECT, user.getEmail()
        );
        log.info("claims ok... ");
        final String bearerToken  = Jwts.builder()
            .setIssuedAt(new Date(currentTime))
            .setExpiration(new Date(currentTime + 15 * 60 * 1000))
            .setSubject(user.getEmail())
            .setClaims(claims)
            .signWith( SignatureAlgorithm.HS256, getKey())
            .compact();
        log.info("bearer ok... ");
        return Map.of(BEARER, bearerToken);
    }

    private Key getKey() {
        Decoder decoder = java.util.Base64.getDecoder();
        byte[] keyBytes = decoder.decode(ENCRYPTION_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUsername(String token){
        return this.getClaims(token, Claims::getSubject);
        
    }

    public Boolean isTokenExprired(String token) {
        Date expirationDate = this.getClaims(token, Claims::getExpiration);;
        return expirationDate.before(new Date());
    }

   

    private <T> T getClaims(String token, Function<Claims, T> function){
        Claims claims = getAllClaims(token);
        return function.apply(claims);
    }

    private Claims getAllClaims(String token){
        return Jwts.parser()
                .setSigningKey(this.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public void deconnexion() {
        Utilisateur user = (Utilisateur) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Jwt jwt = this.jwtRepository.findUserValidToken(user.getEmail(),false, false).orElseThrow(()-> new RuntimeException("Aucun jeton actif pour cet utilisateur"));
        jwt.setDesactive(true); 
        jwt.setExpired(true); 
        this.jwtRepository.save(jwt);

    }

    @Scheduled(cron = "0 0 * * * *")
    public void removeToken(){
        log.info("Suppression des tokens expirées et désactivées");
        this.jwtRepository.deleteAllByExpiredTrueAndDesactiveTrue();
    }

     public Map<String, String> refreshToken(Map<String,String> refreshTokenRequest) {
       final Jwt jwt = this.jwtRepository.findByRefreshToken(refreshTokenRequest.get(REFRESH)).orElseThrow(()-> new RuntimeException("Aucun refreshToken actif pour cet utilisateur"));;
       if( jwt.getRefreshToken().getExpire() || jwt.getRefreshToken().getExpiration().isBefore(Instant.now())){
            throw new RuntimeException("token invalide");
       }else{
            Map<String, String> tokens = this.genererToken(jwt.getUtilisateur().getEmail());
            this.disableOldToken(jwt.getUtilisateur());
            return tokens;
       }
    }

}
