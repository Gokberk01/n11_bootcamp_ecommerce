package com.n11.bootcamp.ecommerce.user_service.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.n11.bootcamp.ecommerce.user_service.dto.request.LoginRequestDto;
import com.n11.bootcamp.ecommerce.user_service.dto.request.SignUpRequestDto;
import com.n11.bootcamp.ecommerce.user_service.dto.request.UpdateUserRequestDto;
import com.n11.bootcamp.ecommerce.user_service.dto.response.JwtResponseDto;
import com.n11.bootcamp.ecommerce.user_service.dto.response.MessageResponseDto;
import com.n11.bootcamp.ecommerce.user_service.entity.ShoppingCart;
import com.n11.bootcamp.ecommerce.user_service.entity.User;
import com.n11.bootcamp.ecommerce.user_service.exception.UserAlreadyExistsException;
import com.n11.bootcamp.ecommerce.user_service.exception.UserNotFoundException;
import com.n11.bootcamp.ecommerce.user_service.repository.UserRepository;
import com.n11.bootcamp.ecommerce.user_service.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Value("${JWT_ISSUER_URI:http://localhost:8080/realms/microservice-realm/protocol/openid-connect/token}")   String jwtIssuerUri;
    @Value("${jwt.client_id}")    String jwtClientId;
    @Value("${jwt.client_secret}")String jwtClientSecret;
    @Value("${jwt.grant_type}")   String jwtGrantType;
    @Value("${jwt.scope}")        String jwtScope;

    private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserRepository userRepository;

    private final RestTemplate restTemplate;

    private static final  String SHOPPING_CART_BASE = "http://SHOPPING-CART-SERVICE";


    public UserServiceImpl(UserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<?> authenticateUser(LoginRequestDto loginRequestDto)
    {
        LOGGER.info("SERVICE: Authenticating user: {}", loginRequestDto.getUsername());

        User user = userRepository.findByUsername(loginRequestDto.getUsername())
                .orElseThrow(() -> {
                    LOGGER.error("SERVICE ERROR: User not found: {}", loginRequestDto.getUsername());
                    return new UserNotFoundException(loginRequestDto.getUsername());
                });


        PasswordEncoder encoder = new BCryptPasswordEncoder();
        HttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(jwtIssuerUri.trim());

        List<BasicNameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("grant_type", jwtGrantType.trim()));
        params.add(new BasicNameValuePair("client_id", jwtClientId.trim()));
        params.add(new BasicNameValuePair("client_secret", jwtClientSecret.trim()));
        params.add(new BasicNameValuePair("username", loginRequestDto.getUsername().trim()));
        params.add(new BasicNameValuePair("password", loginRequestDto.getPassword().trim()));
        // params.add(new BasicNameValuePair("scope", jwtScope)); // Opsiyonel

        LOGGER.debug("SERVICE: Fetching token from issuer for user: {}", user.getUsername());
        String accessToken = "";
        try {

            httpPost.setEntity(new UrlEncodedFormEntity(params));
            HttpResponse response = httpClient.execute(httpPost);
            String responseBody = EntityUtils.toString(response.getEntity());
            accessToken = extractAccessToken(responseBody);

        } catch (Exception e) {
            LOGGER.error("SERVICE ERROR: JWT fetching failed: {}", e.getMessage());
        }

        return ResponseEntity.ok(new JwtResponseDto(accessToken, user.getId(), user.getUsername(), user.getEmail(),user.getRole()));
    }


    private static String extractAccessToken(String jsonResponse) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            return rootNode.path("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ResponseEntity<?> registerUser(SignUpRequestDto signUpRequestDto) {

        LOGGER.info("SERVICE: Registering new user: {}", signUpRequestDto.getUsername());
        if (userRepository.existsByUsername(signUpRequestDto.getUsername())) {
            LOGGER.warn("SERVICE WARN: Username already taken: {}", signUpRequestDto.getUsername());
            throw new UserAlreadyExistsException("Username is already taken: " + signUpRequestDto.getUsername());
        }

        if (userRepository.existsByEmail(signUpRequestDto.getEmail())) {
            throw new UserAlreadyExistsException("Email is already in use: " + signUpRequestDto.getEmail());
        }

        PasswordEncoder encoder = new BCryptPasswordEncoder();
        User user = new User(
                signUpRequestDto.getUsername(),
                signUpRequestDto.getEmail(),
                encoder.encode(signUpRequestDto.getPassword()),
                "Customer"
        );

        userRepository.save(user);
        LOGGER.info("SERVICE: Creating remote shopping cart for user: {}", user.getUsername());
        ShoppingCart userShoppingCart = new ShoppingCart();
        userShoppingCart.setShoppingCartName(user.getUsername());

        restTemplate.postForObject(
                SHOPPING_CART_BASE + "/api/shopping-cart",
                userShoppingCart.getShoppingCartName(),
                String.class
        );

        return ResponseEntity.ok(new MessageResponseDto("User registered successfully!"));
    }

    public ResponseEntity<?> deleteUser(Long userId) {
        LOGGER.warn("SERVICE: Deleting user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

            try {
                LOGGER.info("SERVICE: Deleting remote shopping cart for user: {}", user.getUsername());
                ShoppingCart shoppingCart = restTemplate.getForObject(
                        SHOPPING_CART_BASE + "/api/shopping-cart/by-name/" + user.getUsername(),
                        ShoppingCart.class);

                if (shoppingCart != null) {
                    LOGGER.info("SERVICE: Deleting remote shopping cart ID: {}", shoppingCart.getId());
                    restTemplate.delete(SHOPPING_CART_BASE + "/api/shopping-cart/" + shoppingCart.getId());
                }
            } catch (Exception e) {
                LOGGER.warn("SERVICE WARN: Remote cart could not be handled for user: {}. Proceeding with user deletion.", user.getUsername());
            }

            userRepository.delete(user);
            LOGGER.info("SERVICE: User ID {} deleted successfully.", userId);

            return ResponseEntity.ok(new MessageResponseDto("User account deleted successfully!"));

    }

    public ResponseEntity<?> updateUser(Long userId, UpdateUserRequestDto updateUserRequestDto) {
        LOGGER.info("SERVICE: Updating user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId.toString()));

        if (updateUserRequestDto.getPassword() != null && !updateUserRequestDto.getPassword().isEmpty()) {
            PasswordEncoder encoder = new BCryptPasswordEncoder();
            user.setPassword(encoder.encode(updateUserRequestDto.getPassword()));
        }

        if (updateUserRequestDto.getEmail() != null && !updateUserRequestDto.getEmail().isEmpty()) {
            if (userRepository.existsByEmail(updateUserRequestDto.getEmail())) {
                LOGGER.warn("SERVICE WARN: Update failed. Email already in use: {}", updateUserRequestDto.getEmail());
                throw new UserAlreadyExistsException("Email is already in use: " + updateUserRequestDto.getEmail());
            }
            user.setEmail(updateUserRequestDto.getEmail());
        }

        userRepository.save(user);
        LOGGER.info("SERVICE: User updated successfully: {}", user.getUsername());
        return ResponseEntity.ok(new MessageResponseDto("User account updated successfully!"));

    }
}
