package com.devsenior.cdiaz.bibliokeep.config;

import java.io.IOException;
import java.net.URI;

import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.devsenior.cdiaz.bibliokeep.exception.InvalidCredentialsException;
import com.devsenior.cdiaz.bibliokeep.exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    private final ObjectMapper objectMapper;

    public GlobalExceptionHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleMethodArgumentNotValid(
        org.springframework.web.bind.MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST,
            "Errores de validación en los datos proporcionados");
        problem.setType(URI.create("https://api.bibliokeep.com/errors/validation"));
        problem.setTitle("Validación fallida");
        
        var errors = ex.getBindingResult().getFieldErrors().stream()
            .collect(java.util.stream.Collectors.toMap(
                org.springframework.validation.FieldError::getField,
                org.springframework.validation.FieldError::getDefaultMessage));
        problem.setProperty("errors", errors);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFoundException(NotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
        problem.setType(URI.create("https://api.bibliokeep.com/errors/unauthorized"));
        problem.setTitle(ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleInvalidCredentialsException(InvalidCredentialsException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.UNAUTHORIZED,
                "Credenciales inválidas");
        problem.setType(URI.create("https://api.bibliokeep.com/errors/unauthorized"));
        problem.setTitle("Credenciales inválidas");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(problem);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleEntityNotFound(EntityNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND,
                ex.getMessage());
        problem.setType(URI.create("https://api.bibliokeep.com/errors/not-found"));
        problem.setTitle("Recurso no encontrado");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST,
                ex.getMessage());
        problem.setType(URI.create("https://api.bibliokeep.com/errors/bad-request"));
        problem.setTitle("Solicitud inválida");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problem);
    }

    @ExceptionHandler(HttpMessageNotWritableException.class)
    public void handleMessageNotWritable(HttpMessageNotWritableException ex, HttpServletResponse response) {
        if (response.isCommitted() || isClosedOrClientDisconnect(ex)) {
            log.debug("Respuesta no escribible (cliente desconectado o stream cerrado): {}", ex.getMessage());
            return;
        }
        log.warn("No se pudo escribir el cuerpo HTTP: {}", ex.getMessage());
    }

    @ExceptionHandler(ClientAbortException.class)
    public void handleClientAbort(ClientAbortException ex) {
        log.debug("Cliente cerró la conexión: {}", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public void handleGenericException(Exception ex, HttpServletResponse response) throws IOException {
        if (response.isCommitted() || isClosedOrClientDisconnect(ex)) {
            log.debug("Error omitido tras respuesta enviada o cliente desconectado: {}", ex.getMessage());
            return;
        }
        log.error("Error interno no manejado", ex);
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor");
        problem.setType(URI.create("https://api.bibliokeep.com/errors/internal-error"));
        problem.setTitle("Error interno");
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        response.setContentType(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), problem);
    }

    private static boolean isClosedOrClientDisconnect(Throwable ex) {
        for (Throwable t = ex; t != null; t = t.getCause()) {
            if (t instanceof ClientAbortException) {
                return true;
            }
            if (t instanceof IOException e) {
                String m = e.getMessage();
                if (m != null) {
                    String lower = m.toLowerCase();
                    if (lower.contains("broken pipe")
                            || lower.contains("connection reset")
                            || lower.contains("may not be written to once it has been closed")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
