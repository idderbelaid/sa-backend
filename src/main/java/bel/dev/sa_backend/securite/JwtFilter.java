package bel.dev.sa_backend.securite;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import bel.dev.sa_backend.entities.Jwt;
import bel.dev.sa_backend.service.JwtService;
import bel.dev.sa_backend.service.UtilisateurService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@Service
public class JwtFilter extends OncePerRequestFilter{
    private HandlerExceptionResolver handlerExceptionResolver;
    private UtilisateurService utilisateurService;
    private JwtService jwtService;


    public JwtFilter(UtilisateurService utilisateurService, JwtService jwtService, HandlerExceptionResolver handlerExceptionResolver ){
        this.utilisateurService = utilisateurService;
        this.jwtService = jwtService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                String token = null;
                Jwt tokenBDD = null;
                String username = null;
                Boolean isTokenExpired = false;
                try{
                    final String authorization = request.getHeader("Authorization");
                    if(authorization != null && authorization.startsWith("Bearer ") ){
                        token = authorization.substring(7);
                        tokenBDD = this.jwtService.tokenbyValue(token);
                        isTokenExpired= jwtService.isTokenExprired(token);
                        username = jwtService.extractUsername(token);
                    }

                    if(!isTokenExpired 
                        &&  username != null 
                        && tokenBDD.getUtilisateur().getEmail().equals(username)
                        && SecurityContextHolder.getContext().getAuthentication() ==null 
                    ){
                        UserDetails userDetails = utilisateurService.loadUserByUsername(username);
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                    }

                    filterChain.doFilter(request, response);

                }catch(final Exception exception){
                    this.handlerExceptionResolver.resolveException(request, response, null, exception);
                }
                
                    
                
       
    }

}
