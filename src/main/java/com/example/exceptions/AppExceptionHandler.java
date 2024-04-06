package com.example.exceptions;

import com.example.dto.UiSuccessContainer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class AppExceptionHandler extends ResponseEntityExceptionHandler {
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                HttpHeaders headers,
                                                                HttpStatusCode status,
                                                                WebRequest request) {
    Map<String, Object> body = new HashMap<>();
    body.put("errors",
        ex.getBindingResult().getAllErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage).filter(Objects::nonNull)
            .filter(s -> !s.isBlank())
            .toList());
    return new ResponseEntity<>(body, status);
  }

  @ExceptionHandler({ImageNotFoundException.class})
  public ResponseEntity<?> notFoundException(ImageNotFoundException exception) {
    return ResponseEntity
        .status(HttpStatus.NOT_FOUND)
        .body(new UiSuccessContainer(false, exception.getMessage()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> notFoundException(Exception exception) {
    return ResponseEntity
        .status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new UiSuccessContainer(false, exception.getMessage()));
  }
}
