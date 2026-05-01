package com.n11.bootcamp.ecommerce.shopping_cart_service.service.impl;

import com.n11.bootcamp.ecommerce.shopping_cart_service.entity.Product;
import com.n11.bootcamp.ecommerce.shopping_cart_service.entity.ShoppingCart;
import com.n11.bootcamp.ecommerce.shopping_cart_service.exception.ShoppingCartNotFoundException;
import com.n11.bootcamp.ecommerce.shopping_cart_service.repository.ProductRepository;
import com.n11.bootcamp.ecommerce.shopping_cart_service.repository.ShoppingCartRepository;
import com.n11.bootcamp.ecommerce.shopping_cart_service.service.ShoppingCartService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ShoppingCartServiceImpl.class);

    private final ShoppingCartRepository shoppingCartRepository;
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;

    private static final String PRODUCT_SERVICE_BASE = "http://PRODUCT-SERVICE";


    public ShoppingCartServiceImpl(ShoppingCartRepository shoppingCartRepository,
                                   ProductRepository productRepository,
                                   RestTemplate restTemplate) {
        this.shoppingCartRepository = shoppingCartRepository;
        this.productRepository = productRepository;
        this.restTemplate = restTemplate;
    }


    @Override
    public ResponseEntity<ShoppingCart> createShoppingCart(String shoppingCartName) {
        LOGGER.info("SERVICE: Creating shopping cart by name: {}", shoppingCartName);
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setShoppingCartName(shoppingCartName);

        ShoppingCart savedCart = shoppingCartRepository.save(shoppingCart);
        LOGGER.debug("SERVICE: Shopping cart saved with ID: {}", savedCart.getId());

        return ResponseEntity.ok(savedCart);
    }


    @Override
    public ResponseEntity<ShoppingCart> addProductsToShoppingCart(Long shoppingCartId, List<Product> products) {
        LOGGER.info("SERVICE: Adding {} products to shopping cart ID: {}", products.size(), shoppingCartId);
        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> {
                    LOGGER.error("SERVICE ERROR: Shopping Cart not found by ID: {}", shoppingCartId);
                    return new ShoppingCartNotFoundException(shoppingCartId);
                });

        List<Product> persistedProducts = new ArrayList<>();

        for (Product incoming : products) {
            if (incoming == null) continue;

            Product entity = productRepository.findById(incoming.getId())
                    .orElseGet(() -> {
                        LOGGER.debug("SERVICE: Product ID {} not found in local DB", incoming.getId());
                        Product product = new Product();
                        product.setId(incoming.getId());
                        return product;
                    });


            if (incoming.getTitle() != null && !incoming.getTitle().isBlank()) {
                entity.setTitle(incoming.getTitle());
            }
            if (incoming.getImg() != null && !incoming.getImg().isBlank()) {
                entity.setImg(incoming.getImg());
            }
            if (incoming.getBrand() != null && !incoming.getBrand().isBlank()) {
                entity.setBrand(incoming.getBrand());
            }
            if (incoming.getPrice() > 0) {
                entity.setPrice(incoming.getPrice());
            }
            if (incoming.getCategory() != null && !incoming.getCategory().isBlank()) {
                entity.setCategory(incoming.getCategory());
            }


            Product saved = productRepository.saveAndFlush(entity);
            persistedProducts.add(saved);
        }

        Set<Product> existingProducts = shoppingCart.getProducts();
        if (existingProducts == null) existingProducts = new HashSet<>();
        existingProducts.addAll(persistedProducts);

        shoppingCart.setProducts(existingProducts);
        LOGGER.info("SERVICE: Successfully updated shopping cart ID: {} with new products", shoppingCartId);
        return ResponseEntity.ok(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    public ResponseEntity<ShoppingCart> removeProductFromShoppingCart(Long shoppingCartId, Long productId) {
        LOGGER.warn("SERVICE: Removing product ID: {} from shopping cart ID: {}", productId, shoppingCartId);
        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> {
                    LOGGER.error("SERVICE ERROR: Shopping Cart ID: {} not found for removal", shoppingCartId);
                    return new ShoppingCartNotFoundException(shoppingCartId);
                });


        Set<Product> existingProducts = shoppingCart.getProducts();
        if (existingProducts == null) return ResponseEntity.ok(shoppingCart);

        existingProducts.removeIf(product -> product.getId() == productId);
        shoppingCart.setProducts(existingProducts);

        LOGGER.info("SERVICE: Successfully removed shopping cart ID: {}", shoppingCartId);
        return ResponseEntity.ok(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    public ResponseEntity<Map<String, String>> getShoppingCartPrice(Long shoppingCartId) {
        LOGGER.info("SERVICE: Calculating total price for shopping cart ID: {}", shoppingCartId);
        Map<String, String> response = new HashMap<>();


        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> {
                    LOGGER.error("SERVICE ERROR: Shopping cart ID: {} not found for total price calculation", shoppingCartId);
                    return new ShoppingCartNotFoundException(shoppingCartId);
                });

        int totalPrice = shoppingCart.getProducts()
                .stream()
                .map(product -> restTemplate.getForObject(
                        PRODUCT_SERVICE_BASE + "/api/product/" + product.getId(), HashMap.class))
                .mapToInt(productResponse -> (int) productResponse.get("price"))
                .sum();

        response.put("total_price", Double.toString(totalPrice));
        LOGGER.info("SERVICE: Calculated total price: {} for shopping cart ID: {}", totalPrice, shoppingCartId);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ShoppingCart> getShoppingCartById(Long shoppingCartId) {
        LOGGER.info("SERVICE: Fetching shopping cart by ID: {}", shoppingCartId);

        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> {
                    LOGGER.error("SERVICE ERROR: Shopping cart ID: {} not found", shoppingCartId);
                    return new ShoppingCartNotFoundException(shoppingCartId);
                });

        return ResponseEntity.ok(shoppingCart);
    }

    @Override
    public ResponseEntity<ShoppingCart> getShoppingCartByShoppingCartName(String shoppingCartName) {
        LOGGER.info("SERVICE: Fetching shopping cart by name: {}", shoppingCartName);
        Optional<ShoppingCart> shoppingCartOptional = shoppingCartRepository.findByShoppingCartName(shoppingCartName);

        if (shoppingCartOptional.isPresent()) {
            ShoppingCart cart = shoppingCartOptional.get();
            LOGGER.info("SERVICE: Fetching shopping cart is present by name: {}", shoppingCartName);
            return ResponseEntity.ok(cart);
        } else {
            LOGGER.error("SERVICE ERROR: HttpStatus.NOT_FOUND");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<String> deleteShoppingCartById(Long shoppingCartId) {
        LOGGER.warn("SERVICE: Deleting cart ID: {}", shoppingCartId);

        if (shoppingCartRepository.existsById(shoppingCartId)) {
            shoppingCartRepository.deleteById(shoppingCartId);

            LOGGER.info("Deleted Shopping cart by Id successfully");
            return ResponseEntity.ok("Deleted Shopping cart by Id successfully");
        }
        else {
            LOGGER.error("SERVICE ERROR: Delete failed, shopping cart ID: {} not found", shoppingCartId);
            throw new ShoppingCartNotFoundException(shoppingCartId);
        }
    }

    @Override
    public ResponseEntity<List<ShoppingCart>> getAllShoppingCarts() {
        LOGGER.info("SERVICE: Fetching all shopping carts");
        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findAll();

        return ResponseEntity.ok(shoppingCarts);
    }

    @Override
    public ResponseEntity<String> deleteAllShoppingCarts() {
        LOGGER.warn("SERVICE: Deleting ALL shopping carts");
        shoppingCartRepository.deleteAll();
        return ResponseEntity.ok("All Shopping Carts deleted successfully");
    }
}
