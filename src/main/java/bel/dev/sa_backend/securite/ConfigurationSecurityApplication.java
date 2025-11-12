package bel.dev.sa_backend.securite;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;


@Configuration
@EnableMethodSecurity
@EnableWebSecurity
public class ConfigurationSecurityApplication {
    

    private final JwtFilter jwtFilter;
  


    public ConfigurationSecurityApplication(JwtFilter jwtFilter,BCryptPasswordEncoder bCryptPasswordEncoder ){
        this.jwtFilter = jwtFilter;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;

    }
      
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(
                auth -> auth
                            .requestMatchers(HttpMethod.POST, "/utilisateurs/inscription").permitAll()
                            .requestMatchers(HttpMethod.POST, "/utilisateurs/activation").permitAll()
                            .requestMatchers(HttpMethod.POST, "/utilisateurs/connexion").permitAll()
                            .requestMatchers(HttpMethod.POST, "/utilisateurs/deconnexion").permitAll()
                            .requestMatchers(HttpMethod.POST, "/utilisateurs/modifierPassword").permitAll()
                            .requestMatchers(HttpMethod.POST, "/utilisateurs/reinitiliserPassword").permitAll()
                            .requestMatchers(HttpMethod.POST, "/utilisateurs/refresh-token").permitAll()
                            .requestMatchers(HttpMethod.GET, "/sentiment/sentiments" ).hasRole("ADMIN")
                            .anyRequest().authenticated()
            )
            .sessionManagement(
                httpSEcuritySessionMnagementConfigurer -> 
                    httpSEcuritySessionMnagementConfigurer.sessionCreationPolicy(SessionCreationPolicy.STATELESS) //on vérifie à chaque requête la session

            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);


        return http.build();
    }

   

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

   
    
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder
                                                                ) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

}
