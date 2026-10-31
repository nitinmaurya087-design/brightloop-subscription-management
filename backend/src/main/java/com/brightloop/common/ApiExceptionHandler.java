package com.brightloop.common;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Map<String,Object>> status(ResponseStatusException ex){
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of("status",ex.getStatusCode().value(),"message",ex.getReason()==null?"Request failed":ex.getReason(),"timestamp",Instant.now().toString()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String,Object>> invalid(MethodArgumentNotValidException ex){
        String message=ex.getBindingResult().getFieldErrors().stream().findFirst().map(e->e.getDefaultMessage()).orElse("Request validation failed");
        return ResponseEntity.badRequest().body(Map.of("status",400,"message",message,"timestamp",Instant.now().toString()));
    }
}
