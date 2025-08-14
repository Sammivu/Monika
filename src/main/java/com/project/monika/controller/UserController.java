package com.project.monika.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.monika.model.dto.BankDto;
import com.project.monika.model.dto.TransferRequest;
import com.project.monika.model.dto.TransferResponse;
import com.project.monika.model.dto.UserDTO;
import com.project.monika.service.impl.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class UserController {

    private final UserService userService;

    @PostMapping("/api/auth/create")
    public ResponseEntity<UserDTO>createUser(@RequestBody UserDTO userDTO) throws JsonProcessingException {
        UserDTO newUser = userService.createUser(userDTO);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    @PostMapping("/account/local-transfer")
    public ResponseEntity<TransferResponse> transferFunds(TransferRequest transferRequest) {

        TransferResponse response = userService.localTransfer(transferRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/get-all-banks")
    public ResponseEntity<List<Object>> banks(){
        List<Object> bankDtoList = userService.getAllBanks();
        return ResponseEntity.ok(bankDtoList);
    }
//    @PostMapping("/open")
//    public ResponseEntity<String> openWallet(@RequestBody OpenWalletRequest request) {
//        return ResponseEntity.ok(walletService.openWallet(request));
//    }
}
