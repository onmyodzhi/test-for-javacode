package com.javacode.test.execeptions;

import com.javacode.test.execeptions.responces.WalletErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class InsufficientFundsException extends WalletException {
    private final BigDecimal balance;

    public InsufficientFundsException(BigDecimal balance, UUID walletId) {
        super(new WalletErrorResponse("Insufficient funds", walletId, balance), HttpStatus.UNPROCESSABLE_ENTITY);
        this.balance = balance;
    }
}
