package com.project.monika.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.monika.model.dto.TransferRequest;
import com.project.monika.model.dto.TransferResponse;
import com.project.monika.model.dto.UserDTO;
import jakarta.transaction.Transactional;

public interface UserServiceImpl {

    UserDTO createUser(UserDTO userDTO) throws JsonProcessingException;
    TransferResponse localTransfer(TransferRequest transferRequest);
}
