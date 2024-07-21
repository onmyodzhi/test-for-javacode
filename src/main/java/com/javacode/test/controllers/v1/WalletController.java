package com.javacode.test.controllers.v1;

import com.javacode.test.models.dtos.WalletDto;
import com.javacode.test.models.dtos.WalletOperationDto;
import com.javacode.test.services.WalletService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WalletController {

    final WalletService walletService;

    @PostMapping("/api/v1/wallet")
    public WalletDto performWalletTransaction(@Validated @RequestBody WalletOperationDto walletOperationDto){
        return walletService.operation(walletOperationDto);
    }

    @GetMapping("/api/v1/wallets/{WALLET_UUID}")
    public WalletDto getWallet(@PathVariable("WALLET_UUID") UUID walletUUID){
        return walletService.getWalletInfo(walletUUID);
    }
}
