package com.javacode.test.services;

import com.javacode.test.execeptions.InsufficientFundsException;
import com.javacode.test.execeptions.WalletNotFoundException;
import com.javacode.test.models.Wallet;
import com.javacode.test.models.dtos.WalletDto;
import com.javacode.test.models.dtos.WalletOperationDto;
import com.javacode.test.models.mappers.WalletMapper;
import com.javacode.test.models.mappers.WalletOperationMapper;
import com.javacode.test.repositories.WalletRepository;
import com.javacode.test.states.OperationType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WalletService {

    final WalletRepository walletRepository;
    final WalletOperationMapper walletOperationMapper;
    final WalletMapper walletMapper;
    final ReadWriteLock lock = new ReentrantReadWriteLock();

    public WalletDto getWalletInfo(UUID walletId) {
        return walletRepository.findById(walletId)
                .map(walletMapper::toDto)
                .orElseThrow(() -> new WalletNotFoundException(walletId));
    }

    @Transactional
    public WalletDto operation(WalletOperationDto walletOperationDto) {
        BigDecimal amount = walletOperationDto.getAmount();
        OperationType operationType = walletOperationDto.getOperationType();
        UUID walletId = walletOperationDto.getId();

        lock.writeLock().lock();

        try {
            return switch (operationType) {
                case DEPOSIT -> adjustWalletBalance(walletId, amount);
                case WITHDRAW -> adjustWalletBalance(walletId, amount.negate());
            };
        } finally {
            lock.writeLock().unlock();
        }
    }

    private WalletDto adjustWalletBalance(UUID walletId, BigDecimal amount) {
        Wallet existingWallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException(walletId));

        BigDecimal adjustedAmount = existingWallet.getBalance().add(amount);

        if (adjustedAmount.compareTo(BigDecimal.ZERO) < 0) {
            throw new InsufficientFundsException(existingWallet.getBalance(), walletId);
        }

        existingWallet.setBalance(adjustedAmount);

        existingWallet = walletRepository.save(existingWallet);

        return walletMapper.toDto(existingWallet);
    }
}
