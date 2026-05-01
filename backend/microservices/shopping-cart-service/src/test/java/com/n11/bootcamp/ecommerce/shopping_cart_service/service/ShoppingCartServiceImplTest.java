package com.n11.bootcamp.ecommerce.shopping_cart_service.service;

import com.n11.bootcamp.ecommerce.shopping_cart_service.entity.Product;
import com.n11.bootcamp.ecommerce.shopping_cart_service.entity.ShoppingCart;
import com.n11.bootcamp.ecommerce.shopping_cart_service.exception.ShoppingCartNotFoundException;
import com.n11.bootcamp.ecommerce.shopping_cart_service.repository.ProductRepository;
import com.n11.bootcamp.ecommerce.shopping_cart_service.repository.ShoppingCartRepository;
import com.n11.bootcamp.ecommerce.shopping_cart_service.service.impl.ShoppingCartServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.*;

@ExtendWith(MockitoExtension.class)
class ShoppingCartServiceImplTest {
    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    private ShoppingCart sampleCart;
    private Product sampleProduct;

    @BeforeEach
    void setUp() {
        sampleCart = new ShoppingCart();
        sampleCart.setId(1L);
        sampleCart.setShoppingCartName("MyCart");
        sampleCart.setProducts(new HashSet<>());

        sampleProduct = new Product();
        sampleProduct.setId(101L);
        sampleProduct.setPrice(500);
        sampleProduct.setTitle("Laptop");
    }

    @Test
    void createShoppingCart_ShouldReturnSavedCart() {
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(sampleCart);

        ResponseEntity<ShoppingCart> response = shoppingCartService.createShoppingCart("MyCart");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("MyCart", response.getBody().getShoppingCartName());
        verify(shoppingCartRepository, times(1)).save(any());
    }

    @Test
    void addProductsToShoppingCart_WhenProductDoesNotExist_ShouldCreateNewProduct() {
        Long cartId = 1L;
        ShoppingCart cart = new ShoppingCart();
        cart.setId(cartId);
        cart.setProducts(new HashSet<>());

        Product incomingProduct = new Product();
        incomingProduct.setId(999L);
        incomingProduct.setTitle("New Item");
        incomingProduct.setPrice(100L);

        List<Product> productsToUpdate = List.of(incomingProduct);

        when(shoppingCartRepository.findById(cartId)).thenReturn(Optional.of(cart));
        when(productRepository.findById(999L)).thenReturn(Optional.empty());
        when(productRepository.saveAndFlush(any(Product.class))).thenAnswer(i -> i.getArguments()[0]);
        when(shoppingCartRepository.save(any(ShoppingCart.class))).thenReturn(cart);

        ResponseEntity<ShoppingCart> response = shoppingCartService.addProductsToShoppingCart(cartId, productsToUpdate);

        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());

        verify(productRepository).findById(999L);
        verify(productRepository).saveAndFlush(argThat(product ->
                product.getId() == 999L && "New Item".equals(product.getTitle())
        ));
    }

    @Test
    void addProductsToShoppingCart_ShouldSuccessfullyAdd() {
        when(shoppingCartRepository.findById(1L)).thenReturn(Optional.of(sampleCart));
        when(productRepository.findById(101L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.saveAndFlush(any())).thenReturn(sampleProduct);
        when(shoppingCartRepository.save(any())).thenReturn(sampleCart);

        List<Product> productsToAdd = Arrays.asList(sampleProduct);
        ResponseEntity<ShoppingCart> response = shoppingCartService.addProductsToShoppingCart(1L, productsToAdd);

        assertNotNull(response.getBody());
        verify(productRepository).saveAndFlush(any());
        verify(shoppingCartRepository).save(any());
    }

    @Test
    void addProductsToShoppingCart_ShouldThrowExceptionWhenCartNotFound() {
        when(shoppingCartRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ShoppingCartNotFoundException.class, () -> shoppingCartService.addProductsToShoppingCart(1L, new ArrayList<>()));
    }

    @Test
    void removeProductFromShoppingCart_ShouldRemoveProduct() {
        sampleCart.getProducts().add(sampleProduct);
        when(shoppingCartRepository.findById(1L)).thenReturn(Optional.of(sampleCart));
        when(shoppingCartRepository.save(any())).thenReturn(sampleCart);

        ResponseEntity<ShoppingCart> response = shoppingCartService.removeProductFromShoppingCart(1L, 101L);

        assertTrue(response.getBody().getProducts().isEmpty());
    }

    @Test
    void getShoppingCartPrice_ShouldCalculateCorrectly() {
        sampleCart.getProducts().add(sampleProduct);
        Map<String, Object> mockProductResponse = new HashMap<>();
        mockProductResponse.put("price", 500);

        when(shoppingCartRepository.findById(1L)).thenReturn(Optional.of(sampleCart));
        when(restTemplate.getForObject(anyString(), eq(HashMap.class))).thenReturn((HashMap) mockProductResponse);

        ResponseEntity<Map<String, String>> response = shoppingCartService.getShoppingCartPrice(1L);

        assertEquals("500.0", response.getBody().get("total_price"));
    }

    @Test
    void getShoppingCartById_ShouldReturnCart() {
        when(shoppingCartRepository.findById(1L)).thenReturn(Optional.of(sampleCart));
        ResponseEntity<ShoppingCart> response = shoppingCartService.getShoppingCartById(1L);
        assertEquals(1L, response.getBody().getId());
    }

    @Test
    void getShoppingCartByShoppingCartName_ShouldReturnCart() {
        when(shoppingCartRepository.findByShoppingCartName("MyCart")).thenReturn(Optional.of(sampleCart));
        ResponseEntity<ShoppingCart> response = shoppingCartService.getShoppingCartByShoppingCartName("MyCart");
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getShoppingCartByShoppingCartName_ShouldReturnNotFound() {
        when(shoppingCartRepository.findByShoppingCartName("None")).thenReturn(Optional.empty());
        ResponseEntity<ShoppingCart> response = shoppingCartService.getShoppingCartByShoppingCartName("None");
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void deleteShoppingCartById_ShouldDeleteWhenExists() {
        when(shoppingCartRepository.existsById(1L)).thenReturn(true);
        doNothing().when(shoppingCartRepository).deleteById(1L);

        ResponseEntity<String> response = shoppingCartService.deleteShoppingCartById(1L);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(shoppingCartRepository).deleteById(1L);
    }

    @Test
    void deleteShoppingCartById_ShouldThrowExceptionWhenNotExists() {
        when(shoppingCartRepository.existsById(1L)).thenReturn(false);
        assertThrows(ShoppingCartNotFoundException.class, () -> shoppingCartService.deleteShoppingCartById(1L));
    }

    @Test
    void getAllShoppingCarts_ShouldReturnList() {
        when(shoppingCartRepository.findAll()).thenReturn(Collections.singletonList(sampleCart));
        ResponseEntity<List<ShoppingCart>> response = shoppingCartService.getAllShoppingCarts();
        assertEquals(1, response.getBody().size());
    }

    @Test
    void deleteAllShoppingCarts_ShouldReturnSuccess() {
        doNothing().when(shoppingCartRepository).deleteAll();
        ResponseEntity<String> response = shoppingCartService.deleteAllShoppingCarts();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(shoppingCartRepository).deleteAll();
    }

    @Test
    void addProductsToShoppingCart_ShouldThrowException_WhenCartNotFound() {
        Long cartId = 999L;
        when(shoppingCartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(ShoppingCartNotFoundException.class, () -> {shoppingCartService.addProductsToShoppingCart(cartId, new ArrayList<>());});
        verify(shoppingCartRepository, times(1)).findById(cartId);
        verify(shoppingCartRepository, never()).save(any());
    }

    @Test
    void removeProductFromShoppingCart_ShouldThrowException_WhenCartNotFound() {
        Long cartId = 999L;
        when(shoppingCartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(ShoppingCartNotFoundException.class, () -> {
            shoppingCartService.removeProductFromShoppingCart(cartId, 1L);
        });
    }

    @Test
    void getShoppingCartPrice_ShouldThrowException_WhenCartNotFound() {
        Long cartId = 999L;
        when(shoppingCartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(ShoppingCartNotFoundException.class, () -> {
            shoppingCartService.getShoppingCartPrice(cartId);
        });
    }

    @Test
    void getShoppingCartById_ShouldThrowException_WhenCartNotFound() {
        Long cartId = 999L;
        when(shoppingCartRepository.findById(cartId)).thenReturn(Optional.empty());

        assertThrows(ShoppingCartNotFoundException.class, () -> {
            shoppingCartService.getShoppingCartById(cartId);
        });
    }

    @Test
    void deleteShoppingCartById_ShouldThrowException_WhenCartDoesNotExist() {
        Long cartId = 999L;
        when(shoppingCartRepository.existsById(cartId)).thenReturn(false);

        assertThrows(ShoppingCartNotFoundException.class, () -> {
            shoppingCartService.deleteShoppingCartById(cartId);
        });
        verify(shoppingCartRepository, never()).deleteById(anyLong());
    }
}
