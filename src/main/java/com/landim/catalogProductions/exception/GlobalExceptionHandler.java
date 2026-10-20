package com.landim.catalogProductions.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> resourceNotFound(ResourceNotFoundException e){
        Map<String, String> error = new HashMap<>();
        error.put("status", "404");
        error.put("message", e.getMessage());
        return error;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> validationException(MethodArgumentNotValidException e){
        Map<String, Object> error = new HashMap<>();
        error.put("status", "400");

        List<String> messages = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();

        error.put("errors", messages);
        return error;
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> conflictException(ResourceAlreadyExistsException e){
        Map<String, String> error = new HashMap<>();
        error.put("status", "409");
        error.put("message", e.getMessage());
        return error;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> genericException(Exception e){
        Map<String, String> error = new HashMap<>();
        error.put("status", "500");
        error.put("message", "Erro interno no servidor");
        e.printStackTrace();
        return error;
    }
}
