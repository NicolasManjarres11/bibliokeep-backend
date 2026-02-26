package com.devsenior.cdiaz.bibliokeep.controller;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;
import org.springframework.web.context.WebApplicationContext;

import com.devsenior.cdiaz.bibliokeep.model.dto.LoginRequestDTO;
import com.devsenior.cdiaz.bibliokeep.model.dto.LoginResponseDTO;
import com.fasterxml.jackson.core.JsonProcessingException;

@SpringBootTest
public class AuthControllerIT {

    private RestTestClient restTestClient;

    @BeforeEach
    public void init(WebApplicationContext context) {
        restTestClient = RestTestClient.bindToApplicationContext(context)
                .build();
    }

    @Test
    @DisplayName("Inicio de sesion con usuario y contrasena validos")
    public void loginSuccessMockMvc() throws JsonProcessingException, Exception {
        // Arrange
        var request = new LoginRequestDTO(
                "cdiaz@test.com",
                "cdiaz123");

        // Act
        var result = restTestClient.post()
                .uri("/auth/login")
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(LoginResponseDTO.class)
                .returnResult()
                .getResponseBody();

        // Assert
        assertNotNull(result);
        assertNotNull(result.accessToken());
    }

    @Test
    @DisplayName("Inicio de sesion falla con usuario no existente")
    public void loginFailsWithNonExistentUser() throws Exception {
        // Arrange
        var request = new LoginRequestDTO("nonexistent@test.com", "password123");

        // Act & Assert
        restTestClient.post()
                .uri("/auth/login")
                .body(request)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("Inicio de sesion falla con campos vacios")
    public void loginFailsWithEmptyFields() throws Exception {
        // Arrange
        var request = new LoginRequestDTO("", "");

        // Act & Assert
        restTestClient.post()
                .uri("/auth/login")
                .body(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

}
