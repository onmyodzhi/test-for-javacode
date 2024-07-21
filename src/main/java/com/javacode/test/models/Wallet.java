package com.javacode.test.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;
import java.util.UUID;


@Entity
@Table(name = "wallet")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Wallet {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    UUID id;

    @DecimalMin(value = "0.0", message = "Insufficient funds")
    @Column(name = "balance", nullable = false)
    BigDecimal balance;
}
