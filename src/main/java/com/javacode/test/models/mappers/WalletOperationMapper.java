package com.javacode.test.models.mappers;

import com.javacode.test.models.Wallet;
import com.javacode.test.models.dtos.WalletOperationDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface WalletOperationMapper {

    WalletOperationDto toDto(Wallet wallet);

    @Mappings({
           // @Mapping(source = "operationType", ignore = true),
            @Mapping(target = "balance", ignore = true)
    })
    Wallet toEntity(WalletOperationDto dto);
}
