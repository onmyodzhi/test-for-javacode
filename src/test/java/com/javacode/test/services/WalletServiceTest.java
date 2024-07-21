package com.javacode.test.services;

import com.javacode.test.execeptions.InsufficientFundsException;
import com.javacode.test.execeptions.WalletNotFoundException;
import com.javacode.test.execeptions.responces.WalletErrorResponse;
import com.javacode.test.execeptions.responces.WalletNotFoundResponse;
import com.javacode.test.models.Wallet;
import com.javacode.test.models.dtos.WalletDto;
import com.javacode.test.models.dtos.WalletOperationDto;
import com.javacode.test.models.mappers.WalletMapper;
import com.javacode.test.models.mappers.WalletOperationMapper;
import com.javacode.test.repositories.WalletRepository;
import com.javacode.test.states.OperationType;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.hibernate.metamodel.mapping.OwnedValuedModelPart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class WalletServiceTest {

    @Mock
    WalletRepository walletRepository;

    @Mock
    WalletOperationMapper walletOperationMapper;

    @Mock
    WalletMapper walletMapper;

    @InjectMocks
    WalletService underTest;


    @Test
    void getWalletInfo() {
        UUID walletId = UUID.randomUUID();

        Wallet wallet = new Wallet(walletId, BigDecimal.TEN);

        when(walletRepository.findById(walletId))
                .thenReturn(Optional.of(wallet));

        WalletDto expected = new WalletDto(walletId, BigDecimal.TEN);

        when(walletMapper.toDto(wallet))
                .thenReturn(expected);

        WalletDto actual = assertDoesNotThrow(() -> underTest.getWalletInfo(walletId));

        assertEquals(expected, actual);
    }

    @Test
    void getWalletInfoWithWalletNotFoundInRepository() {
        UUID walletId = UUID.randomUUID();

        when(walletRepository.findById(walletId))
                .thenReturn(Optional.empty());

        WalletNotFoundException actualException = assertThrows(WalletNotFoundException.class, () -> underTest.getWalletInfo(walletId));

        assertEquals(actualException.getPayload(), new WalletNotFoundResponse("Wallet with id: " + walletId, walletId));
        assertEquals(actualException.getHttpStatus(), HttpStatus.NOT_FOUND);
    }

    @Test
    void getWalletInfoWithWalletFoundInRepositoryButMapperReturnsNull() {
        UUID walletId = UUID.randomUUID();

        Wallet wallet = new Wallet(walletId, BigDecimal.TEN);

        when(walletRepository.findById(walletId))
                .thenReturn(Optional.of(wallet));

        when(walletMapper.toDto(wallet))
                .thenReturn(null);

        WalletNotFoundException actualException = assertThrows(WalletNotFoundException.class, () -> underTest.getWalletInfo(walletId));

        assertEquals(actualException.getPayload(), new WalletNotFoundResponse("Wallet with id: " + walletId, walletId));
        assertEquals(actualException.getHttpStatus(), HttpStatus.NOT_FOUND);
    }

    @ParameterizedTest
    @EnumSource(OperationType.class)
    void operation(OperationType operationType) {
        UUID walletId = UUID.randomUUID();

        Wallet wallet = new Wallet(walletId, BigDecimal.TEN);
        when(walletRepository.findById(walletId))
        .thenReturn(Optional.of(wallet));

        WalletDto expected = new WalletDto(walletId, BigDecimal.valueOf(20));

        when(walletRepository.save(wallet))
                .thenReturn(wallet);

        when(walletMapper.toDto(wallet))
                .thenReturn(expected);

        WalletDto actual = assertDoesNotThrow(() -> underTest.operation(new WalletOperationDto(
                walletId,
                operationType,
                BigDecimal.TEN
        )));

        assertEquals(expected, actual);

        ArgumentCaptor<Wallet> walletCaptor = ArgumentCaptor.forClass(Wallet.class);
        verify(walletRepository, times(1))
                .save(walletCaptor.capture());

        Wallet actualWallet = walletCaptor.getValue();

        assertEquals(walletId, actualWallet.getId());

        BigDecimal expectedBalance = switch (operationType) {
            case DEPOSIT -> BigDecimal.valueOf(20);
            case WITHDRAW -> BigDecimal.valueOf(0);
        };
        assertEquals(expectedBalance, actualWallet.getBalance());

        verifyNoMoreInteractions(walletRepository, walletOperationMapper, walletMapper);
    }

    @ParameterizedTest
    @EnumSource(OperationType.class)
    void operationWithNotFoundException(OperationType operationType) {
        UUID walletId = UUID.randomUUID();

        when(walletRepository.findById(walletId))
                .thenReturn(Optional.empty());

        WalletNotFoundException actualException = assertThrows(WalletNotFoundException.class, () -> underTest.operation(new WalletOperationDto(
                walletId,
                operationType,
                BigDecimal.TEN
        )));

        assertEquals(actualException.getPayload(), new WalletNotFoundResponse("Wallet with id: " + walletId, walletId));
        assertEquals(actualException.getHttpStatus(), HttpStatus.NOT_FOUND);

        verifyNoMoreInteractions(walletRepository);
        verifyNoInteractions(walletOperationMapper, walletMapper);
    }

    @Test
    void operationWithInsufficientFunds() {
        UUID walletId = UUID.randomUUID();

        Wallet wallet = new Wallet(walletId, BigDecimal.ONE);
        when(walletRepository.findById(walletId))
                .thenReturn(Optional.of(wallet));


        InsufficientFundsException actualException = assertThrows(InsufficientFundsException.class, () -> underTest.operation(new WalletOperationDto(
                walletId,
                OperationType.WITHDRAW,
                BigDecimal.TEN
        )));

        assertEquals(new WalletErrorResponse("Insufficient funds", walletId, BigDecimal.ONE), actualException.getPayload());
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, actualException.getHttpStatus());
        assertEquals(BigDecimal.ONE, actualException.getBalance());

        verify(walletRepository, never()).save(any(Wallet.class));

        assertEquals(BigDecimal.ONE, wallet.getBalance());
        assertEquals(walletId, wallet.getId());

        verifyNoMoreInteractions(walletRepository, walletOperationMapper, walletMapper);
    }
}