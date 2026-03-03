package com.devsenior.cdiaz.bibliokeep.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.devsenior.cdiaz.bibliokeep.mapper.BookMapper;
import com.devsenior.cdiaz.bibliokeep.model.dto.BookRequestDTO;
import com.devsenior.cdiaz.bibliokeep.model.dto.BookResponseDTO;
import com.devsenior.cdiaz.bibliokeep.model.entity.Book;
import com.devsenior.cdiaz.bibliokeep.model.entity.BookStatus;
import com.devsenior.cdiaz.bibliokeep.model.vo.JwtUser;
import com.devsenior.cdiaz.bibliokeep.repository.BookRepository;
import com.devsenior.cdiaz.bibliokeep.service.BookService;

public class BookServiceImplTest {

    private BookRepository bookRepository;
    private BookMapper bookMapper;

    private BookService service;


    //////////
    @BeforeEach
    public void init() {
        bookRepository = mock(BookRepository.class);
        bookMapper = mock(BookMapper.class);
        service = new BookServiceImpl(bookRepository, bookMapper);

        var principal = new JwtUser("cdiaz@email.com", "e08a10f2-48a2-4790-a3e4-6fa52d77b940", List.of());
        var auth = new UsernamePasswordAuthenticationToken(principal, "header.payload.signature", List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    @DisplayName("Crear un libro de manera exitosa")
    public void testCreateBook() {
        // AAA -> Arrage, Act, Assert

        // Arrage: Preparar la prueba
        var requestDto = getRequestMockDto();
        var bookEntity = getBookEntity();
        var responseDto = getResponseDto();

        when(bookMapper.toEntity(requestDto)).thenReturn(bookEntity);
        when(bookMapper.toResponseDTO(bookEntity)).thenReturn(responseDto);
        when(bookRepository.save(bookEntity)).thenReturn(bookEntity);

        // Act: Ejecuto el metodo que vamos a probar
        var result = service.createBook(requestDto);

        // Assert: Validamos el resultado de la ejecucion
        assertNotNull(result);
        assertEquals("Prueba de Libro", result.title());
        assertEquals("e08a10f2-48a2-4790-a3e4-6fa52d77b940", result.ownerId().toString());
    }

    @Test
    @DisplayName("""
            Scenario: Consultar un libro del catalogo de libros
                Dado (Given) el ide del libro es '1' y el propietario tienel id 'e08a10f2-48a2-4790-a3e4-6fa52d77b940'
                Cuando (When) consulte el libro por el id 1
                Entonces (Then) retorne la informacion del libro del sistema
            """)
    public void testGetBookByIdWithIdExists() {
        // Arrange
        var id = 1L;

        var bookEntity = getBookEntity();
        var responseDto = getResponseDto();

        when(bookRepository.findById(id)).thenReturn(Optional.of(bookEntity));
        when(bookMapper.toResponseDTO(bookEntity)).thenReturn(responseDto);

        // Act
        var result = service.getBookById(id);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.id());
    }

    @Test
    public void testGetBookByIdWithIdNotExists() {
        // Arrange
        var id = 0L;

        when(bookRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        var result = assertThrows(RuntimeException.class, () -> service.getBookById(id));

        // Assert
        assertEquals("Libro no encontrado con ID: 0", result.getMessage());
    }

    @Test
    public void testGetBookByIdWithDifferentOwner() {
        // Arrange
        var bookEntity = getBookEntity();
        bookEntity.setOwnerId(UUID.fromString("e08a10f2-48a2-4790-a3e4-6fa52d77b941"));

        var id = 1L;

        when(bookRepository.findById(id)).thenReturn(Optional.of(bookEntity));

        // Act & Assert
        var result = assertThrows(RuntimeException.class, () -> service.getBookById(id));

        // Assert
        assertEquals("No tienes permiso para acceder a este libro", result.getMessage());

    }

    @Test
    public void testUpdateBookWithIdNotExists() {
        var requestDto = getRequestMockDto();

        var id = 0L;

        var result = assertThrows(RuntimeException.class, () -> service.updateBook(id, requestDto));
        assertEquals("Libro no encontrado con ID: 0", result.getMessage());
    }

    private BookRequestDTO getRequestMockDto() {
        return new BookRequestDTO(
                null,
                "123456789",
                "Prueba de Libro",
                null,
                null,
                null,
                BookStatus.DESEADO,
                1,
                null);
    }

    private Book getBookEntity() {
        var bookEntity = new Book();
        bookEntity.setOwnerId(UUID.fromString("e08a10f2-48a2-4790-a3e4-6fa52d77b940"));
        bookEntity.setIsbn("123456789");
        bookEntity.setTitle("Prueba de Libro");
        bookEntity.setStatus(BookStatus.DESEADO);
        bookEntity.setRating(1);
        return bookEntity;
    }

    private BookResponseDTO getResponseDto() {
        return new BookResponseDTO(
                1L,
                UUID.fromString("e08a10f2-48a2-4790-a3e4-6fa52d77b940"),
                "123456789",
                "Prueba de Libro",
                null,
                null,
                null,
                BookStatus.DESEADO,
                1,
                false);
    }

}
