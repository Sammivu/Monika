package com.project.monika.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransferResponse {
    private boolean debitSuccess;
    private boolean creditSuccess;
    private boolean rollbackSuccess;
    private String debitTransactionId;
    private String creditTransactionId;
    private String rollbackTransactionId;
    private Object debitResponse;
    private Object creditResponse;
    private Object rollbackResponse;
    private String message;
}