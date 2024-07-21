package com.javacode.test.execeptions;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE)
public abstract class WalletException extends RuntimeException {
    final HttpStatus httpStatus;
    final Object payload;

    public WalletException(Object payload, HttpStatus httpStatus) {
        super(payload.toString());
        this.payload = payload;
        this.httpStatus = httpStatus;
    }
}
