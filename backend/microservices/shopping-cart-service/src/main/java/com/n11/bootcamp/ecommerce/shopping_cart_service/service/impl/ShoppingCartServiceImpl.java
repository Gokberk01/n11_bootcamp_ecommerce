package com.n11.bootcamp.ecommerce.shopping_cart_service.service.impl;

import com.n11.bootcamp.ecommerce.shopping_cart_service.entity.Product;
import com.n11.bootcamp.ecommerce.shopping_cart_service.entity.ShoppingCart;
import com.n11.bootcamp.ecommerce.shopping_cart_service.repository.ProductRepository;
import com.n11.bootcamp.ecommerce.shopping_cart_service.repository.ShoppingCartRepository;
import com.n11.bootcamp.ecommerce.shopping_cart_service.service.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Transactional
public class ShoppingCartServiceImpl implements ShoppingCartService {

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
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setShoppingCartName(shoppingCartName);

        return ResponseEntity.ok(shoppingCartRepository.save(shoppingCart));
    }


    @Override
    public ResponseEntity<ShoppingCart> addProductsToShoppingCart(Long shoppingCartId, List<Product> products) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> new RuntimeException("Cannot get ShoppingCartById. Shopping Cart does not exist in DB"));

        List<Product> persistedProducts = new ArrayList<>();

        for (Product incoming : products) {
            if (incoming == null) continue;

            Product entity = productRepository.findById(incoming.getId())
                    .orElseGet(() -> {
                        Product p = new Product();
                        p.setId(incoming.getId());
                        return p;
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
//            if (incoming.getDescription() != null && !incoming.getDescription().isBlank()) {
//                entity.setDescription(incoming.getDescription());
//            }
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
        return ResponseEntity.ok(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    public ResponseEntity<ShoppingCart> removeProductFromShoppingCart(Long shoppingCartId, Long productId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> new RuntimeException("Cannot get ShoppingCartById for removing Product. Shopping Cart does not exist in DB"));

        Set<Product> existingProducts = shoppingCart.getProducts();
        if (existingProducts == null) return ResponseEntity.ok(shoppingCart);

        existingProducts.removeIf(product -> product.getId() == productId);
        shoppingCart.setProducts(existingProducts);

        return ResponseEntity.ok(shoppingCartRepository.save(shoppingCart));
    }

    @Override
    public ResponseEntity<Map<String, String>> getShoppingCartPrice(Long shoppingCartId) {
        Map<String, String> response = new HashMap<>();

        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId)
                .orElseThrow(() -> new RuntimeException("Cannot get ShoppingCartById for getting Shopping Cart Price. Shopping Cart does not exist in DB"));

        int totalPrice = shoppingCart.getProducts()
                .stream()
                .map(product -> restTemplate.getForObject(
                        PRODUCT_SERVICE_BASE + "/api/product/" + product.getId(), HashMap.class))
                .mapToInt(productResponse -> (int) productResponse.get("price"))
                .sum();

        response.put("total_price", Double.toString(totalPrice));
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<ShoppingCart> getShoppingCartById(Long shoppingCartId) {
        ShoppingCart shoppingCart = shoppingCartRepository.findById(shoppingCartId)
            .orElseThrow(() -> new RuntimeException("Cannot get ShoppingCartById. Shopping Cart does not exist in DB"));

        return ResponseEntity.ok(shoppingCart);
    }

    @Override
    public ResponseEntity<ShoppingCart> getShoppingCartByShoppingCartName(String shoppingCartName) {
        Optional<ShoppingCart> opt = shoppingCartRepository.findByShoppingCartName(shoppingCartName);

        if (opt.isPresent()) {
            ShoppingCart cart = opt.get();
            return ResponseEntity.ok(cart);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<String> deleteShoppingCartById(Long shoppingCartId) {

        if (shoppingCartRepository.existsById(shoppingCartId)) {
            shoppingCartRepository.deleteById(shoppingCartId);
            return ResponseEntity.ok("Deleted Shopping Cart by Id successfully");
        }
        else {
            throw new RuntimeException("Cannot get ShoppingCartById for deleting Shopping Cart. Shopping Cart does not exist in DB");
        }
    }

    @Override
    public ResponseEntity<List<ShoppingCart>> getAllShoppingCarts() {
        List<ShoppingCart> shoppingCarts = shoppingCartRepository.findAll();

        return ResponseEntity.ok(shoppingCarts);
    }

    @Override
    public ResponseEntity<String> deleteAllShoppingCarts() {
        shoppingCartRepository.deleteAll();
        return ResponseEntity.ok("All Shopping Carts deleted successfully");
    }
}
