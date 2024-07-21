package com.javacode.test.execeptions.responces;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
@EqualsAndHashCode
public class WalletErrorResponse {
    private String message;
    private UUID walletId;
    private BigDecimal balance;

    public WalletErrorResponse(String message, UUID walletId, BigDecimal balance) {
        this.message = message;
        this.walletId = walletId;
        this.balance = balance;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WalletErrorResponse [message=");
        builder.append(message);

        if (walletId != null) {
            builder.append(", walletId=");
            builder.append(walletId);
        }

        if (balance != null) {
            builder.append(", balance=");
            builder.append(balance);
        }
        builder.append("]");

        return builder.toString();
    }
}
