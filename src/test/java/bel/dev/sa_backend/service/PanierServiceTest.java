package bel.dev.sa_backend.service;




import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;



import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import bel.dev.sa_backend.controller.PanierDBController.CartItemResponse;
import bel.dev.sa_backend.controller.PanierDBController.CreateCartItemRequest;
import bel.dev.sa_backend.controller.PanierDBController.UpdateCartItemRequest;
import bel.dev.sa_backend.dto.PanierDTO;
import bel.dev.sa_backend.entities.Panier;
import bel.dev.sa_backend.entities.PanierItem;
import bel.dev.sa_backend.entities.Produit;
import bel.dev.sa_backend.mapper.PanierMapper;
import bel.dev.sa_backend.repository.PanierItemRepository;
import bel.dev.sa_backend.repository.PanierRepository;
import bel.dev.sa_backend.repository.ProduitRepository;
import bel.dev.sa_backend.service.utils.GestionPanier;

import java.math.BigDecimal;

import java.util.*;

class PanierServiceTest {

    @InjectMocks
    private PanierService panierService;

    @Mock
    private PanierRepository panierRepository;
    @Mock
    private PanierItemRepository panierItemRepository;
    @Mock
    private ProduitRepository produitRepository;
    @Mock
    private PanierMapper panierMapper;
   
    @Mock
    private StringRedisTemplate stringRedis;

    @Mock
    private ValueOperations<String, String> valueOps;

    
    @Mock
    private GestionPanier gestionPanier;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(stringRedis.opsForValue()).thenReturn(valueOps);
    }

    @Test
    void testMergeGuestCartIntoUser_WhenGuestCartEmpty_ShouldReturnUserCart() {
        String sessionId = "session123";
        String userId = "user123";

        PanierDTO userCart = new PanierDTO();
        userCart.setPanierId("cart123");

        when(valueOps.setIfAbsent(anyString(), any(), any())).thenReturn(true);
        when(panierService.findOrCreateUserCart(userId)).thenReturn(userCart);
        when(panierItemRepository.findByPanierId(userCart.getPanierId())).thenReturn(Collections.emptyList());
        //when(panierMapper.toPanierDTO(eq(userCart), anyList())).thenReturn(new PanierDTO());

        PanierDTO result = panierService.mergeGuestCartIntoUser(sessionId, userId);

        assertNotNull(result);
        verify(panierService).findOrCreateUserCart(userId);
        verify(panierItemRepository).findByPanierId(userCart.getPanierId());
    }

    @Test
    void testFindOrCreateUserCart_ShouldReturnPanierDTO() {
        String userId = "user123";
        PanierDTO panier = new PanierDTO();
        panier.setPanierId("cart123");

        when(panierService.findOrCreateUserCart(userId)).thenReturn(panier);
        when(panierItemRepository.findByPanierId(panier.getPanierId())).thenReturn(Collections.emptyList());
        //when(panierMapper.toPanierDTO(eq(panier), anyList())).thenReturn(new PanierDTO());

        PanierDTO result = panierService.findOrCreateUserCart(userId);

        assertNotNull(result);
        verify(panierService).findOrCreateUserCart(userId);
    }

    @Test
    void testListItems_ShouldReturnEmptyList() {
        String cartId = "cart123";
        when(panierItemRepository.findByPanierId(cartId)).thenReturn(Collections.emptyList());

        List<CartItemResponse> result = panierService.listItems(cartId);

        assertTrue(result.isEmpty());
        verify(panierItemRepository).findByPanierId(cartId);
    }

    @Test
    void testAddItem_ShouldAddNewItem() {
        String cartId = "cart123";
        CreateCartItemRequest request = new CreateCartItemRequest("prod123", 2, BigDecimal.valueOf(10));

        Panier panier = new Panier();
        panier.setId(cartId);

        Produit produit = new Produit();
        produit.setId("prod123");
        produit.setPrice(BigDecimal.valueOf(10));
        produit.setQuantity(10);

        when(panierRepository.findById(cartId)).thenReturn(Optional.of(panier));
        when(produitRepository.findById(request.productId())).thenReturn(Optional.of(produit));
        when(panierItemRepository.findByPanierIdAndProduitId(cartId, request.productId())).thenReturn(Optional.empty());

        PanierItem savedItem = new PanierItem();
        savedItem.setId("item123");
        savedItem.setQuantity(2);
        savedItem.setUnitPrice(BigDecimal.valueOf(10));

        when(panierItemRepository.save(any())).thenReturn(savedItem);

        CartItemResponse response = panierService.addItem(cartId, request);

        assertNotNull(response);
        assertEquals(2, response.quantity());
        verify(panierItemRepository).save(any());
    }

    @Test
    void testUpdateItem_ShouldUpdateQuantity() {
        String cartId = "cart123";
        String itemId = "item123";
        UpdateCartItemRequest request = new UpdateCartItemRequest(5);

        Panier panier = new Panier();
        panier.setId(cartId);

        PanierItem item = new PanierItem();
        item.setId(itemId);
        item.setPanier(panier);
        item.setQuantity(2);
        item.setUnitPrice(BigDecimal.valueOf(10));

        when(panierItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(panierItemRepository.save(any())).thenReturn(item);

        CartItemResponse response = panierService.updateItem(cartId, itemId, request);

        assertEquals(5, response.quantity());
        verify(panierItemRepository).save(item);
    }

    @Test
    void testDeleteItem_ShouldRemoveItem() {
        String cartId = "cart123";
        String itemId = "item123";

        Panier panier = new Panier();
        panier.setId(cartId);

        PanierItem item = new PanierItem();
        item.setId(itemId);
        item.setPanier(panier);

        when(panierItemRepository.findById(itemId)).thenReturn(Optional.of(item));

        panierService.deleteItem(cartId, itemId);

        verify(panierItemRepository).delete(item);
    }

    @Test
    void testClearCart_ShouldDeleteAllItems() {
        String cartId = "cart123";

        panierService.clearCart(cartId);

        verify(panierItemRepository).deleteByPanierId(cartId);
    }
}
