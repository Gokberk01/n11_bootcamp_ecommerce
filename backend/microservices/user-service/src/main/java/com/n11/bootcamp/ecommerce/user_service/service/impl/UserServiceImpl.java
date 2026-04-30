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


    private final UserRepository userRepository;

    private final RestTemplate restTemplate;

    private static final  String SHOPPING_CART_BASE = "http://SHOPPING-CART-SERVICE";


    public UserServiceImpl(UserRepository userRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<?> authenticateUser(LoginRequestDto loginRequestDto)
    {
        User user;
        try {
            user = userRepository.findByUsername(loginRequestDto.getUsername()).orElseThrow();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(new MessageResponseDto("Cannot get UserById. User does not exist in DB"));
        }


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

        String accessToken = "";
        try {

            httpPost.setEntity(new UrlEncodedFormEntity(params));

            HttpResponse response = httpClient.execute(httpPost);

            String responseBody = EntityUtils.toString(response.getEntity());
            System.out.println("responseBody = " + responseBody);

            accessToken = extractAccessToken(responseBody);

        } catch (Exception e) {
            e.printStackTrace();
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

        if (userRepository.existsByUsername(signUpRequestDto.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponseDto("Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequestDto.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponseDto("Email is already in use!"));
        }

        PasswordEncoder encoder = new BCryptPasswordEncoder();


        User user = new User(
                signUpRequestDto.getUsername(),
                signUpRequestDto.getEmail(),
                encoder.encode(signUpRequestDto.getPassword()),
                "Customer"
        );

        userRepository.save(user);

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
        try {

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found!"));

            try {

                ShoppingCart shoppingCart = restTemplate.getForObject(
                        SHOPPING_CART_BASE + "/api/shopping-cart/by-name/" + user.getUsername(),
                        ShoppingCart.class);

                restTemplate.delete(SHOPPING_CART_BASE + "/api/shopping-cart/" + shoppingCart.getId());
            } catch (Exception e) {
                // Continue deleting user
            }


            userRepository.delete(user);

            return ResponseEntity.ok(new MessageResponseDto("User account deleted successfully!"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new MessageResponseDto("Internal Server Error"));
        }
    }

    public ResponseEntity<?> updateUser(Long userId, UpdateUserRequestDto updateUserRequestDto) {
        try {

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new EntityNotFoundException("User not found!"));

            if (updateUserRequestDto.getPassword() != null && !updateUserRequestDto.getPassword().isEmpty()) {
                PasswordEncoder encoder = new BCryptPasswordEncoder();
                user.setPassword(encoder.encode(updateUserRequestDto.getPassword()));
            }

            if (updateUserRequestDto.getEmail() != null && !updateUserRequestDto.getEmail().isEmpty()) {
                if (userRepository.existsByEmail(updateUserRequestDto.getEmail())) {
                    return ResponseEntity.badRequest().body(new MessageResponseDto("Email is already in use!"));
                }
                user.setEmail(updateUserRequestDto.getEmail());
            }

            userRepository.save(user);

            return ResponseEntity.ok(new MessageResponseDto("User account updated successfully!"));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(new MessageResponseDto(e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(new MessageResponseDto("Internal Server Error"));
        }
    }
}
