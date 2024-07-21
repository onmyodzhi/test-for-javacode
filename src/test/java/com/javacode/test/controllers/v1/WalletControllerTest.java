package com.javacode.test.controllers.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.javacode.test.execeptions.InsufficientFundsException;
import com.javacode.test.execeptions.WalletNotFoundException;
import com.javacode.test.models.dtos.WalletDto;
import com.javacode.test.models.dtos.WalletOperationDto;
import com.javacode.test.services.WalletService;
import com.javacode.test.states.OperationType;
import jakarta.validation.ConstraintViolationException;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(WalletController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WalletControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper mapper;

    @MockBean
    WalletService service;


    WalletDto walletDto;
    WalletOperationDto walletOperationDto;
    UUID walletUUID;

    @BeforeEach
    void setUp() {
        walletUUID = UUID.randomUUID();
        walletDto = new WalletDto(walletUUID, BigDecimal.valueOf(100));
        walletOperationDto = new WalletOperationDto(
                walletUUID,
                OperationType.DEPOSIT,
                BigDecimal.valueOf(100)
        );
    }

    @Test
    void testPerformWalletTransaction() throws Exception {
        when(service.operation(any(WalletOperationDto.class)))
                .thenReturn(walletDto);

        mvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(walletOperationDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(walletDto)));
    }

    @Test
    void testPerformWalletTransactionInsufficientFunds() throws Exception {
        walletOperationDto = new WalletOperationDto(
                walletUUID,
                OperationType.WITHDRAW,
                BigDecimal.valueOf(50)
        );
        when(service.operation(any(WalletOperationDto.class)))
                .thenThrow(new InsufficientFundsException(BigDecimal.valueOf(50), walletUUID));

        mvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(walletOperationDto)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(content().json("""
                        {
                        "message":"Insufficient funds",
                        "walletId":"%s",
                        "balance":50
                        }
                        """.formatted(walletUUID)));
    }

    @Test
    void testPerformWalletTransactionValidationError() throws Exception {
        walletOperationDto.setAmount(BigDecimal.valueOf(-1));

        when(service.operation(any(WalletOperationDto.class)))
                .thenThrow(new ConstraintViolationException("Validation error" + BigDecimal.valueOf(-1), null));

        mvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(walletOperationDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetWallet() throws Exception {
        when(service.getWalletInfo(walletUUID))
                .thenReturn(walletDto);

        mvc.perform(get("/api/v1/wallets/" + walletUUID))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(walletDto)));
    }

    @Test
    void testGetWalletNotFound() throws Exception {
        when(service.getWalletInfo(walletUUID)).thenThrow(new WalletNotFoundException(walletUUID));

        mvc.perform(get("/api/v1/wallets/" + walletUUID))
                .andExpect(status().isNotFound())
                .andExpect(content().json("{\"message\":\"Wallet with id: " + walletUUID + "\",\"walletId\":\"" + walletUUID + "\"}"));
    }
}