package com.javacode.test.models.dtos;

import com.javacode.test.states.OperationType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class WalletOperationDto {

    @NotNull
    UUID id;

    @NotNull
    OperationType operationType;

    @Positive
    BigDecimal amount;
}
