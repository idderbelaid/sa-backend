package bel.dev.sa_backend.service;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import bel.dev.sa_backend.Enums.Category;
import bel.dev.sa_backend.dto.PageResponse;
import bel.dev.sa_backend.dto.ProduitDTO;
import bel.dev.sa_backend.entities.Produit;
import bel.dev.sa_backend.repository.ProduitRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests du service ProduitService")
class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private ProduitService produitService;

    private Produit produit1;
    private Produit produit2;
    private ProduitDTO produitDTO;

    @BeforeEach
    void setUp() {
        // Initialisation des données de test
        produit1 = new Produit();
        produit1.setId_produit("PROD01");
        produit1.setName("Rose");
        produit1.setCategory(Category.CLASSIQUE);
        produit1.setLight(1);
        produit1.setWater(2);
        produit1.setCover("Extérieur");
        produit1.setQuantity(10);
        produit1.setPrice(BigDecimal.valueOf(15.0));
        produit1.setDescription("Belle rose rouge");

        produit2 = new Produit();
        produit2.setId_produit("PROD02");
        produit2.setName("Tulipe");
        produit2.setCategory(Category.FLEURIE);
        produit2.setLight(2);
        produit2.setWater(3);
        produit2.setCover("Intérieur");
        produit2.setQuantity(5);
        produit2.setPrice(BigDecimal.valueOf(12.0));
        produit2.setDescription("Tulipe jaune");

        produitDTO = new ProduitDTO(
            "PROD01",
            "Rose Modifiée",
            Category.FLEURIE,
            2,
            1,
            "Extérieur",
            20,
            BigDecimal.valueOf(12.0),
            "Rose modifiée"
        );
    }

    // =========================
    // ✅ TEST : getCategories()
    // =========================
    @Test
    @DisplayName("Test recherche avec tous les paramètres")
    void testRechercherAvecTousLesParametres() {
        // Arrange
        List<Produit> produits = Arrays.asList(produit1, produit2);
        Page<Produit> page = new PageImpl<>(produits, PageRequest.of(0, 10), produits.size());
        
        when(produitRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);

        // Act
        PageResponse<ProduitDTO> result = produitService.rechercher(
            "Rose", 
            "FLEURIE", 
            0, 
            10, 
            "name", 
            "asc"
        );

        // Assert
        assertNotNull(result);
        assertEquals(2, result.content().size());
        assertEquals(0, result.number());
        assertEquals(10, result.size());
        assertEquals(2, result.totalElements());
        assertEquals(1, result.totalPages());
        assertTrue(result.first());
        assertTrue(result.last());
        
        verify(produitRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    // =========================
    // ✅ TEST : creer()
    // =========================
    
    @Test
    @DisplayName("Test recherche sans tri")
    void testRechercherSansTri() {
        // Arrange
        List<Produit> produits = Arrays.asList(produit1);
        Page<Produit> page = new PageImpl<>(produits, PageRequest.of(0, 10), produits.size());
        
        when(produitRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);

        // Act
        PageResponse<ProduitDTO> result = produitService.rechercher(
            null, 
            null, 
            0, 
            10, 
            null, 
            null
        );

        // Assert
        assertNotNull(result);
        assertEquals(1, result.content().size());
        verify(produitRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }

    @Test
    @DisplayName("Test recherche avec tri descendant")
    void testRechercherAvecTriDescendant() {
        // Arrange
        List<Produit> produits = Arrays.asList(produit1, produit2);
        Page<Produit> page = new PageImpl<>(produits, PageRequest.of(0, 10), produits.size());
        
        when(produitRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(page);

        // Act
        PageResponse<ProduitDTO> result = produitService.rechercher(
            null, 
            null, 
            0, 
            10, 
            "price", 
            "desc"
        );

        // Assert
        assertNotNull(result);
        assertEquals(2, result.content().size());
        verify(produitRepository, times(1)).findAll(any(Specification.class), any(Pageable.class));
    }


    // =========================
    // ✅ TEST : modifie()
    // =========================
    @Test
    @DisplayName("Test récupération des catégories")
    void testGetCategories() {
        // Arrange
        List<String> categories = Arrays.asList("FLEURIE", "Arbustes", "Plantes");
        when(produitRepository.findDistinctCategories()).thenReturn(categories);

        // Act
        List<String> result = produitService.getCategories();

        // Assert
        assertNotNull(result);
        assertEquals(3, result.size());
        assertTrue(result.contains("FLEURIE"));
        assertTrue(result.contains("Arbustes"));
        assertTrue(result.contains("Plantes"));
        
        verify(produitRepository, times(1)).findDistinctCategories();
    }

    @Test
    @DisplayName("Test création d'un produit")
    void testCreer() {
        // Arrange
        Produit nouveauProduit = new Produit();
        nouveauProduit.setName("Orchidée");
        nouveauProduit.setCategory(Category.FLEURIE);
        
        when(produitRepository.save(any(Produit.class))).thenReturn(nouveauProduit);

        // Act
        produitService.creer(nouveauProduit);

        // Assert
        assertNotNull(nouveauProduit.getId_produit());
        assertEquals(6, nouveauProduit.getId_produit().length());
        verify(produitRepository, times(1)).save(nouveauProduit);
    }

    @Test
    @DisplayName("Test modification d'un produit - tous les champs")
    void testModifierTousLesChamps() {
        // Arrange
        when(produitRepository.findById("PROD01")).thenReturn(Optional.of(produit1));
        when(produitRepository.save(any(Produit.class))).thenReturn(produit1);

        // Act
        produitService.modifie("PROD01", produitDTO);

        // Assert
        verify(produitRepository, times(1)).findById("PROD01");
        verify(produitRepository, times(1)).save(produit1);
        
        assertEquals("Rose Modifiée", produit1.getName());
        assertEquals(2, produit1.getLight());
        assertEquals(1, produit1.getWater());
        assertEquals(20, produit1.getQuantity());
        assertEquals(BigDecimal.valueOf(12.0), produit1.getPrice());
    }

    @Test
    @DisplayName("Test modification - produit non trouvé")
    void testModifierProduitNonTrouve() {
        // Arrange
        when(produitRepository.findById("INEXISTANT")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            produitService.modifie("INEXISTANT", produitDTO);
        });
        
        verify(produitRepository, times(1)).findById("INEXISTANT");
        verify(produitRepository, never()).save(any(Produit.class));
    }

    @Test
    @DisplayName("Test modification - certains champs seulement")
    void testModifierCertainsChamps() {
        // Arrange
        ProduitDTO partielDTO = new ProduitDTO(
            "PROD01",
            "Rose",  // même nom - ne doit pas changer
            Category.FLEURIE, // même catégorie
            2, // changement
            1, // même valeur
            "Extérieur",
            10, // même quantité
            BigDecimal.valueOf(15.99), // même prix
            "Belle rose rouge" // même description
        );
        
        when(produitRepository.findById("PROD01")).thenReturn(Optional.of(produit1));
        when(produitRepository.save(any(Produit.class))).thenReturn(produit1);

        // Act
        produitService.modifie("PROD01", partielDTO);

        // Assert
        assertEquals(2, produit1.getLight()); // Doit être modifié
        verify(produitRepository, times(1)).save(produit1);
    }

    @Test
    @DisplayName("Test suppression d'un produit")
    void testSupprimer() {
        // Arrange
        when(produitRepository.findById("PROD01")).thenReturn(Optional.of(produit1));
        doNothing().when(produitRepository).delete(produit1);

        // Act
        produitService.supprimer("PROD01");

        // Assert
        verify(produitRepository, times(1)).findById("PROD01");
        verify(produitRepository, times(1)).delete(produit1);
    }

    @Test
    @DisplayName("Test suppression - produit non trouvé")
    void testSupprimerProduitNonTrouve() {
        // Arrange
        when(produitRepository.findById("INEXISTANT")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            produitService.supprimer("INEXISTANT");
        });
        
        verify(produitRepository, times(1)).findById("INEXISTANT");
        verify(produitRepository, never()).delete(any(Produit.class));
    }

    @Test
    @DisplayName("Test recherche - page vide")
    void testRechercherPageVide() {
        // Arrange
        Page<Produit> pageVide = new PageImpl<>(Arrays.asList(), PageRequest.of(0, 10), 0);
        when(produitRepository.findAll(any(Specification.class), any(Pageable.class)))
            .thenReturn(pageVide);

        // Act
        PageResponse<ProduitDTO> result = produitService.rechercher(
            "ProduitInexistant", 
            null, 
            0, 
            10, 
            null, 
            null
        );

        // Assert
        assertNotNull(result);
        assertEquals(0, result.content().size());
        assertEquals(0, result.totalElements());
        assertTrue(result.first());
        assertTrue(result.last());
    }

    @Test
    @DisplayName("Test création - vérification unicité ID")
    void testCreerIdUnique() {
        // Arrange
        Produit produit3 = new Produit();
        produit3.setName("Jasmin");
        produit3.setCategory(Category.FLEURIE);

        Produit produit4 = new Produit();
        produit4.setName("Lavande");
        produit4.setCategory(Category.PLANTE_GRASSE);

        when(produitRepository.save(any(Produit.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        produitService.creer(produit3);
        produitService.creer(produit4);

        // Assert
        assertNotNull(produit3.getId_produit());
        assertNotNull(produit4.getId_produit());
        assertNotEquals(produit3.getId_produit(), produit4.getId_produit());
        verify(produitRepository, times(2)).save(any(Produit.class));
    }
}