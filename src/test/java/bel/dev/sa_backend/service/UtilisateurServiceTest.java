package bel.dev.sa_backend.service;

import bel.dev.sa_backend.dto.UtilisateurCreationDTO;
import bel.dev.sa_backend.dto.UtilisateurResponseDTO;
import bel.dev.sa_backend.entities.Role;
import bel.dev.sa_backend.entities.Utilisateur;
import bel.dev.sa_backend.entities.Validation;
import bel.dev.sa_backend.Enums.TypeDeRole;
import bel.dev.sa_backend.repository.RoleRepository;
import bel.dev.sa_backend.repository.UtilisateurRepository;
import bel.dev.sa_backend.service.rabbitMQ.RabbitMQService;
import bel.dev.sa_backend.service.rabbitMQ.KafkaProducer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UtilisateurServiceTest {

    @Mock
    private ValidationService validationService;
    @Mock
    private UtilisateurRepository utilisateurRepository;
    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RabbitMQService rabbitMQService;
    @Mock
    private KafkaProducer kafkaProducer;

    @InjectMocks
    private UtilisateurService utilisateurService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void inscription_shouldThrowIfEmailInvalid() {
        UtilisateurCreationDTO dto = new UtilisateurCreationDTO();
        dto.setEmail("invalidemail");
        dto.setPassword("pass");
        assertThrows(IllegalStateException.class, () -> utilisateurService.inscription(dto));
    }

    @Test
    void inscription_shouldThrowIfEmailExists() {
        UtilisateurCreationDTO dto = new UtilisateurCreationDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("pass");
        when(utilisateurRepository.findByEmail("test@test.com")).thenReturn(Optional.of(new Utilisateur()));
        assertThrows(IllegalStateException.class, () -> utilisateurService.inscription(dto));
    }

    @Test
    void inscription_shouldCreateUser() {
        UtilisateurCreationDTO dto = new UtilisateurCreationDTO();
        dto.setEmail("test@test.com");
        dto.setPassword("pass");
        when(utilisateurRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedPass");
        Role role = new Role();
        role.setLibelle(TypeDeRole.USER);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(utilisateurRepository.save(any(Utilisateur.class))).thenAnswer(i -> i.getArgument(0));
        doNothing().when(validationService).enregister(any(Utilisateur.class));
        doNothing().when(kafkaProducer).sendMessage(any());

        UtilisateurResponseDTO response = utilisateurService.inscription(dto);
        assertNotNull(response);
        assertEquals("test@test.com", response.getEmail());
    }

    @Test
    void loadUserByUsername_shouldThrowIfNotFound() {
        when(utilisateurRepository.findByEmail("notfound@test.com")).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> utilisateurService.loadUserByUsername("notfound@test.com"));
    }

    @Test
    void loadUserByUsername_shouldReturnUser() {
        Utilisateur user = new Utilisateur();
        user.setEmail("found@test.com");
        when(utilisateurRepository.findByEmail("found@test.com")).thenReturn(Optional.of(user));
        Utilisateur result = utilisateurService.loadUserByUsername("found@test.com");
        assertEquals("found@test.com", result.getEmail());
    }

    @Test
    void generateCustomId_shouldReturnFormattedId() {
        String id = utilisateurService.generateCustomId();
        assertTrue(id.startsWith("USR-"));
        assertEquals(19, id.length());
    }

    @Test
    void modifierPassword_shouldThrowIfUserNotFound() {
        Map<String, String> param = new HashMap<>();
        param.put("email", "notfound@test.com");
        when(utilisateurRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        assertThrows(UsernameNotFoundException.class, () -> utilisateurService.modifierPassword(param));
    }

    @Test
    void reinitiliserPassword_shouldThrowIfCodeInvalid() {
        Map<String, String> param = new HashMap<>();
        param.put("email", "test@test.com");
        param.put("code", "123456");
        param.put("password", "newpass");
        Utilisateur user = new Utilisateur();
        user.setEmail("test@test.com");
        when(utilisateurRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        Validation validation = new Validation();
        validation.setUtilisateur(new Utilisateur());
        validation.setExpiresAt(Instant.now().minusSeconds(3600));
        when(validationService.findCodeBDD(anyString())).thenReturn(validation);
        assertThrows(NullPointerException.class, () -> utilisateurService.reinitiliserPassword(param));
    }

    @Test
    void liste_shouldReturnUserDTOs() {
        Utilisateur user = new Utilisateur();
        user.setEmail("test@test.com");
        when(utilisateurRepository.findAll()).thenReturn(Arrays.asList(user));
        List<UtilisateurResponseDTO> result = utilisateurService.liste();
        assertEquals(1, result.size());
        assertEquals("test@test.com", result.get(0).getEmail());
    }
}