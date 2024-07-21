package com.javacode.test.execeptions.handlers;

import com.fasterxml.jackson.core.JsonParseException;
import com.javacode.test.execeptions.WalletException;
import com.javacode.test.execeptions.responces.MessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class WalletExceptionHandler {
    @ExceptionHandler(JsonParseException.class)
    public ResponseEntity<MessageResponse> handleJsonParseException(JsonParseException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new MessageResponse(ex.getMessage()));
    }

    @ExceptionHandler(WalletException.class)
    public ResponseEntity<?> handleWalletException(WalletException ex) {
        log.error(ex.getMessage());
        return ResponseEntity.status(ex.getHttpStatus()).body(ex.getPayload());
    }
}
