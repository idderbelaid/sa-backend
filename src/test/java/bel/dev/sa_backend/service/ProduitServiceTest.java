
package bel.dev.sa_backend.service;

import bel.dev.sa_backend.dto.ProduitDTO;
import bel.dev.sa_backend.entities.Produit;
import bel.dev.sa_backend.repository.ProduitRepository;
import bel.dev.sa_backend.mapper.ProduitMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private ProduitService produitService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void rechercher_shouldReturnEmptyList_whenRepositoryReturnsNull() {
        // Arrange
        when(produitRepository.findAll()).thenReturn(null);

        // Act
        List<ProduitDTO> result = produitService.rechercher();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(produitRepository, times(1)).findAll();
    }

    @Test
    void rechercher_shouldReturnMappedDTOList_whenRepositoryReturnsProducts() {
        // Arrange
        Produit produit1 = new Produit();
        produit1.setId_produit("p1");
        produit1.setName("Monstera");
        produit1.setPrice(BigDecimal.valueOf(12.0));

        Produit produit2 = new Produit();
        produit2.setId_produit("p2");
        produit2.setName("Ficus");
        produit2.setPrice(BigDecimal.valueOf(15.0));

        when(produitRepository.findAll()).thenReturn(Arrays.asList(produit1, produit2));

        // Act
        List<ProduitDTO> result = produitService.rechercher();

        // Assert
        assertEquals(2, result.size());
        assertEquals("Monstera", result.get(0).name());
        assertEquals("Ficus", result.get(1).name());
        verify(produitRepository, times(1)).findAll();
    }

    @Test
    void rechercher_shouldReturnEmptyList_whenRepositoryReturnsEmptyIterable() {
        // Arrange
        when(produitRepository.findAll()).thenReturn(Collections.emptyList());

        // Act
        List<ProduitDTO> result = produitService.rechercher();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(produitRepository, times(1)).findAll();
    }
}

