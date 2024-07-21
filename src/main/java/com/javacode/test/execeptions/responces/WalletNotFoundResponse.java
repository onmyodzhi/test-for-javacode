package com.javacode.test.execeptions.responces;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
public class WalletNotFoundResponse {
    String message;
    UUID walletId;
}
