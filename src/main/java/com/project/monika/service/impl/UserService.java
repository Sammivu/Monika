package com.project.monika.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.monika.model.dto.BankDto;
import com.project.monika.model.dto.TransferRequest;
import com.project.monika.model.dto.TransferResponse;
import com.project.monika.model.dto.UserDTO;

import java.util.List;

public interface UserService {

    UserDTO createUser(UserDTO userDTO) throws JsonProcessingException;
    TransferResponse localTransfer(TransferRequest transferRequest);

    List<Object> getAllBanks();
}
