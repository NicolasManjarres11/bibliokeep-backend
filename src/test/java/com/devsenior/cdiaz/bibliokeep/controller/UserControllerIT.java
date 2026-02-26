package com.devsenior.cdiaz.bibliokeep.controller;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import com.devsenior.cdiaz.bibliokeep.model.dto.UserRequestDTO;
import com.devsenior.cdiaz.bibliokeep.service.JwtService;

@SpringBootTest
public class UserControllerIT {
    
    @Autowired
    private JwtService jwtService;

    private RestTestClient restTestClient;

    @BeforeEach
    public void init(WebApplicationContext context) {
        restTestClient = RestTestClient.bindToApplicationContext(context)
                .build();
    }

    @Test
    @DisplayName("Crear un usuario con role de ADMIN")
    public void createUserWithAdminRole() {
        var claims = Map.<String, Object>of(
            "roles", List.of("ROLE_ADMIN"),
            "user-id", "e7b1c9d2-3f4a-4b6c-9d8e-0a1b2c3d4e5f"
        );
        var token = jwtService.generateToken(claims, "cdiaz@test.com", 60000);
        var request = new UserRequestDTO("cdiaz@test.org", "cdiaz123", Set.of(), 1, List.of("USER"));

        restTestClient.post()
            .uri("/api/users")
            .header("Authorization", String.format("Bearer %s", token))
            .body(request)
            .exchange()
            .expectStatus().isCreated()
            .expectBody()
            .jsonPath("$.id").isNotEmpty()
            .jsonPath("$.email").isEqualTo("cdiaz@test.org");

    }
}
