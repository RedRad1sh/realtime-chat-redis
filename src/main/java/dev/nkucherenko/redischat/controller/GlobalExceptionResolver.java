package dev.nkucherenko.redischat.controller;

import dev.nkucherenko.redischat.dto.exception.ErrorDto;
import dev.nkucherenko.redischat.exception.BusinessException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionResolver {
    @ExceptionHandler(value = {BusinessException.class})
    public ResponseEntity<ErrorDto> processBusinessException(BusinessException exception) {
        ErrorDto errorDto = new ErrorDto()
                .setDescription(exception.getMessage())
                .setCode(exception.getErrorCode());
        return ResponseEntity.status(exception.getErrorCode().getHttpCode()).body(errorDto);
    }
}
