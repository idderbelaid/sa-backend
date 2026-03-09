package bel.dev.sa_backend.controller;


import bel.dev.sa_backend.Enums.Category;
import bel.dev.sa_backend.dto.PageResponse;
import bel.dev.sa_backend.dto.ProduitDTO;
import bel.dev.sa_backend.securite.JwtFilter;
import bel.dev.sa_backend.service.ProduitService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.access.SecurityConfig;
import org.springframework.test.context.ActiveProfiles;

import org.springframework.test.web.servlet.MockMvc;
import org.mockito.Mockito;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import org.springframework.context.annotation.FilterType;

import bel.dev.sa_backend.securite.JwtFilter;



@ActiveProfiles("test")
@WebMvcTest(
    controllers = ProduitController.class,
        
    excludeFilters = {
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class),
            @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = JwtFilter.class)


        },
        
 excludeAutoConfiguration = {
        SecurityAutoConfiguration.class,
        SecurityFilterAutoConfiguration.class
    }

)
class ProduitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    
    @MockBean
    private ProduitService produitService;



    // ============================
    // 1️⃣ /produit/produits
    // ============================
    @Test
    void rechercher_shouldReturnPagedProducts() throws Exception {

        ProduitDTO dto = new ProduitDTO(
                "ID123", "Ficus", Category.CLASSIQUE,
                2, 3, "img.jpg", 5, BigDecimal.valueOf(12.5), "Belle plante"
        );

        PageResponse<ProduitDTO> page = new PageResponse<>(
                List.of(dto), 0, 10, 1, 1, true, true
        );

        Mockito.when(produitService.rechercher("", null, 0, 10, null))
                .thenReturn(page);

        mockMvc.perform(get("/produit/produits"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Ficus"))
                .andExpect(jsonPath("$.content[0].id").value("ID123"));
    }



    // ============================
    // 2️⃣ /produit/categories
    // ============================
    @Test
    void getCategories_shouldReturnList() throws Exception {

        Mockito.when(produitService.getCategories()).thenReturn(
                List.of("Plante", "Fleur")
        );

        mockMvc.perform(get("/produit/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("Plante"))
                .andExpect(jsonPath("$[1]").value("Fleur"));
    }


    // ============================
    // 3️⃣ /produit/creer
    // ============================
    @Test
    void creerProduit_shouldReturnCreatedProduct() throws Exception {

        ProduitDTO dto = new ProduitDTO(
                "ID123", "Ficus", Category.CLASSIQUE,
                3, 2, "img.jpg", 8, BigDecimal.valueOf(20), "Belle plante"
        );

        Mockito.when(produitService.creer(any())).thenReturn(dto);

        String body = """
                {
                   "name": "Ficus",
                   "category": "CLASSIQUE",
                   "light": 3,
                   "water": 2,
                   "cover": "img.jpg",
                   "quantity": 8,
                   "price": 20,
                   "description": "Belle plante"
                }
                """;

        mockMvc.perform(post("/produit/creer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value("ID123"))
                .andExpect(jsonPath("$.name").value("Ficus"));
    }


    // ============================
    // 4️⃣ /produit/upload/{id}
    // ============================
    @Test
    void uploadImage_shouldReturnOk() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.jpg",
                "image/jpeg",
                "hello".getBytes()
        );

        mockMvc.perform(multipart("/produit/upload/ABC123").file(file))
                .andExpect(status().isOk())
                .andExpect(content().string("Image uploader successfully"));
    }



    // ============================
    // 5️⃣ /produit/update/{id}
    // ============================
    @Test
    void modifierProduit_shouldReturnAccepted() throws Exception {

        String body = """
                {
                   "name": "Monstera",
                   "category": "CLASSIQUE",
                   "light": 4,
                   "water": 1,
                   "cover": "img.jpg",
                   "quantity": 10,
                   "price": 22.5,
                   "description": "Plante tropicale"
                }
                """;

        mockMvc.perform(put("/produit/update/ABC123")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isAccepted());

        Mockito.verify(produitService).modifie(eq("ABC123"), any());
    }


    // ============================
    // 6️⃣ /produit/delete/{id}
    // ============================
    @Test
    void supprimerProduit_shouldReturnOk() throws Exception {

        mockMvc.perform(delete("/produit/delete/ABC123"))
                .andExpect(status().isOk());

        Mockito.verify(produitService).supprimer("ABC123");
    }



    // ============================
    // 7️⃣ /produit/info/{id}
    // ============================
    @Test
    void info_shouldReturnProduct() throws Exception {

        ProduitDTO dto = new ProduitDTO(
                "ABC123", "Palmier", Category.CLASSIQUE,
                3, 2, "img.jpg", 6, BigDecimal.valueOf(40), "Plante tropicale"
        );

        Mockito.when(produitService.infoById("ABC123")).thenReturn(dto);

        mockMvc.perform(get("/produit/info/ABC123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Palmier"))
                .andExpect(jsonPath("$.id").value("ABC123"));
    }
}
