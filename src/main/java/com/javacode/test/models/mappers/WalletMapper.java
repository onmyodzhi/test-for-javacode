package com.javacode.test.models.mappers;

import com.javacode.test.models.Wallet;
import com.javacode.test.models.dtos.WalletDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WalletMapper {
    WalletDto toDto(Wallet wallet);
    Wallet toEntity(WalletDto dto);
}
