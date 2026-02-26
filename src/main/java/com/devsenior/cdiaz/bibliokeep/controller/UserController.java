package com.devsenior.cdiaz.bibliokeep.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.devsenior.cdiaz.bibliokeep.model.dto.UserRequestDTO;
import com.devsenior.cdiaz.bibliokeep.model.dto.UserResponseDTO;
import com.devsenior.cdiaz.bibliokeep.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponseDTO createUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return userService.createUser(userRequestDTO);
    }

    @GetMapping("/{id}")
    public UserResponseDTO getUserById(@PathVariable("id") String id) {
        return userService.getUserById(UUID.fromString(id));
    }

    @GetMapping
    public List<UserResponseDTO> getAllUsers() {
        return userService.getAllUsers();
    }

    @PreAuthorize("hasRole('ADMIN') or principal.userId == '#id'")
    @PutMapping("/{id}")
    public UserResponseDTO updateUser(
            @PathVariable("id") String id,
            @Valid @RequestBody UserRequestDTO userRequestDTO) {
        return userService.updateUser(UUID.fromString(id), userRequestDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable("id") String id) {
        userService.deleteUser(UUID.fromString(id));
    }
}
