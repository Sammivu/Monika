package com.project.monika.model.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@RequiredArgsConstructor
public class TransferRequest {
    private String sourceAccount;
    private String destinationAccount;
    private BigDecimal amount;

}
