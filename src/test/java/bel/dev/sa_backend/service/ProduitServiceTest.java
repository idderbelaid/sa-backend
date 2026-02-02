package bel.dev.sa_backend.service;


import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.test.util.ReflectionTestUtils;

import bel.dev.sa_backend.Enums.Category;
import bel.dev.sa_backend.dto.PageResponse;
import bel.dev.sa_backend.dto.ProduitDTO;
import bel.dev.sa_backend.entities.Produit;
import bel.dev.sa_backend.repository.ProduitRepository;

@ExtendWith(MockitoExtension.class)
class ProduitServiceTest {

    @Mock
    private ProduitRepository produitRepository;

    @InjectMocks
    private ProduitService produitService;

    // =========================
    // ✅ TEST : rechercher()
    // =========================
    @Test
    void rechercher_shouldReturnPagedProducts() {
        // GIVEN
        Produit produit = new Produit();
        ReflectionTestUtils.setField(produit, "id", "P001"); // au lieu de produit.setId("P001")        produit.setName("Ficus");
        produit.setCategory(Category.CLASSIQUE);
    

        Page<Produit> page = new PageImpl<>(
                List.of(produit),
                PageRequest.of(0, 10),
                1
        );

        when(produitRepository.findAll(org.mockito.ArgumentMatchers.<Specification<Produit>>any(),any(PageRequest.class)))
                .thenReturn(page);

        // WHEN
        PageResponse<ProduitDTO> result =
                produitService.rechercher("Ficus", "Plante", 0, 10, "name", "asc");

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.content()).hasSize(1);
        assertThat(result.content().get(0).name()).isEqualTo("Ficus");
        assertThat(result.totalElements()).isEqualTo(1);

        verify(produitRepository).findAll(org.mockito.ArgumentMatchers.<Specification<Produit>>any(), any(PageRequest.class));
    }

    // =========================
    // ✅ TEST : getCategories()
    // =========================
    @Test
    void getCategories_shouldReturnDistinctCategories() {
        // GIVEN
        when(produitRepository.findDistinctCategories())
                .thenReturn(List.of("Plante", "Fleur"));

        // WHEN
        List<String> categories = produitService.getCategories();

        // THEN
        assertThat(categories)
                .containsExactly("Plante", "Fleur");

        verify(produitRepository).findDistinctCategories();
    }

    // =========================
    // ✅ TEST : creer()
    // =========================
    @Test
    void creer_shouldGenerateIdAndSaveProduct() {
        // GIVEN
        Produit produit = new Produit();
        produit.setName("Monstera");

        // WHEN
        produitService.creer(produit);

        // THEN
        assertThat(result.content().get(0).id()).isNotNull();
        assertThat(result.content().get(0).id()).hasSize(6);

        verify(produitRepository).save(produit);
    }

    // =========================
    // ✅ TEST : modifie()
    // =========================
    @Test
    void modifie_shouldUpdateProductFields() {
        // GIVEN
        Produit existing = new Produit();
        ReflectionTestUtils.setField(existing, "id", "P123");        
        existing.setName("Ancien nom");
        existing.setLight(2);
        existing.setWater(3);

        
        ProduitDTO dto = new ProduitDTO(
            "P123",
            "Nouveau nom",
            Category.CLASSIQUE,
            2,
            3,
            "", // ✅ String vide
            5,
            java.math.BigDecimal.valueOf(20.0),
            "Ceci est une description de la plante"
        );

     

        when(produitRepository.findById("P123"))
                .thenReturn(Optional.of(existing));

        // WHEN
        produitService.modifie("P123", dto);

        // THEN
        assertThat(existing.getName()).isEqualTo("Nouveau nom");
        assertThat(existing.getLight()).isEqualTo(2);
        assertThat(existing.getWater()).isEqualTo(3);

        verify(produitRepository).save(existing);
    }

    // =========================
    // ✅ TEST : modifie() -> NOT FOUND
    // =========================
    @Test
    void modifie_shouldThrowExceptionIfProductNotFound() {
        // GIVEN
        when(produitRepository.findById("404"))
                .thenReturn(Optional.empty());

        ProduitDTO dto = mock(ProduitDTO.class);

        // THEN
        assertThatThrownBy(() -> produitService.modifie("404", dto))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("Aucun produit");

        verify(produitRepository, never()).save(any());
    }

    // =========================
    // ✅ TEST : supprimer()
    // =========================
    @Test
    void supprimer_shouldDeleteProduct() {
        // GIVEN
        Produit produit = new Produit();

        ReflectionTestUtils.setField(produit, "id", "P001");

        when(produitRepository.findById("P001"))
                .thenReturn(Optional.of(produit));

        // WHEN
        produitService.supprimer("P001");

        // THEN
        verify(produitRepository).delete(produit);
    }

    // =========================
    // ✅ TEST : supprimer() -> NOT FOUND
    // =========================
    @Test
    void supprimer_shouldThrowExceptionIfNotFound() {
        when(produitRepository.findById("404"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> produitService.supprimer("404"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
