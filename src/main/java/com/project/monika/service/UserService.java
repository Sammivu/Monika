package com.project.monika.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.monika.model.User;
import com.project.monika.model.dto.OpenWalletRequest;
import com.project.monika.model.dto.TransferRequest;
import com.project.monika.model.dto.TransferResponse;
import com.project.monika.model.dto.UserDTO;
import com.project.monika.model.enums.Role;
import com.project.monika.repository.UserRepository;
import com.project.monika.service.impl.UserServiceImpl;
import jakarta.persistence.EntityExistsException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService implements UserServiceImpl {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private  ModelMapper modelmapper;
    private final IntegrationService thirdPartyIntegrated;
    private final ObjectMapper objectMapper;

    @Override
    public UserDTO createUser(UserDTO userDTO) throws JsonProcessingException {
        Optional<User> existingUser = userRepository.findByEmail(userDTO.getEmail());
        if (existingUser.isPresent()){
            throw new EntityExistsException("Email already exist");
        }
        User newUser = new User();
        newUser.setFirstName(userDTO.getFirstName().trim());
        newUser.setLastName(userDTO.getLastName().trim());
        newUser.setEmail(userDTO.getEmail());
        newUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        newUser.setRoles(Role.USER);
        OpenWalletRequest walletRequest = getOpenWalletRequest(userDTO);
        Object walletResponse = thirdPartyIntegrated.openWallet(walletRequest);
        log.info("wallet response: {}", walletResponse);
        newUser.setWallet(objectMapper.writeValueAsString(walletResponse) );
        userRepository.save(newUser);

        return convertToDto(newUser);
    }
    private static OpenWalletRequest getOpenWalletRequest(UserDTO savedUser) {
        OpenWalletRequest walletRequest = new OpenWalletRequest();
        walletRequest.setEmail(savedUser.getEmail());
        walletRequest.setLastName(savedUser.getLastName());
        walletRequest.setOtherNames(savedUser.getFirstName());
        walletRequest.setBvn("24416208831");
        walletRequest.setNinUserId("abcdf-1235");
        walletRequest.setTransactionTrackingRef("DGN2024112212010000");
        walletRequest.setNationalIdentityNo("22316109918");
        walletRequest.setNextOfKinName("John Doe");
        walletRequest.setNextOfKinPhoneNo("09134567894");
        walletRequest.setAddress("7, Ikate Elegushi");
        walletRequest.setPlaceOfBirth("Lagos");
        walletRequest.setPhoneNo("09034478562");
        walletRequest.setGender(1);
        return walletRequest;
    }

    @Transactional
    @Override
    public TransferResponse localTransfer(TransferRequest request) {
        String transactionIdDebit = "Dbt" + System.currentTimeMillis();
        String transactionIdCredit = "Crd" + System.currentTimeMillis();

        Object debitResponse = thirdPartyIntegrated.debitAccount(request.getSourceAccount(), request.getAmount(), "Transfer to " + request.getDestinationAccount(), transactionIdDebit);
        boolean debitSuccess = debitResponse != null;
        if (!debitSuccess) {
            return TransferResponse.builder()
                    .debitSuccess(false)
                    .creditSuccess(false)
                    .debitTransactionId(transactionIdDebit)
                    .debitResponse(debitResponse)
                    .message("Debit failed — transfer aborted")
                    .build();
        }

        Object creditResponse = thirdPartyIntegrated.creditAccount(request.getSourceAccount(), request.getAmount(), "Transfer from " + request.getDestinationAccount(), transactionIdCredit);
        boolean creditSuccess = creditResponse != null;
        if (!creditSuccess) {
            log.warn("Credit failed, initiating rollback to return funds to source account...");

            String rollbackTransactionId = "R" + System.currentTimeMillis();
            Object rollbackResponse = thirdPartyIntegrated.creditAccount (request.getSourceAccount(), request.getAmount(), "Rollback of failed transfer to " + request.getDestinationAccount(), rollbackTransactionId);
            boolean rollbackSuccess = rollbackResponse != null;

            return TransferResponse.builder()
                    .debitSuccess(true)
                    .creditSuccess(false)
                    .rollbackSuccess(rollbackSuccess)
                    .debitTransactionId(transactionIdDebit)
                    .creditTransactionId(transactionIdCredit)
                    .rollbackTransactionId(rollbackTransactionId)
                    .debitResponse(debitResponse)
                    .creditResponse(creditResponse)
                    .rollbackResponse(rollbackResponse)
                    .message(rollbackSuccess ? "Credit failed but rollback succeeded — funds returned" :
                            "Credit failed and rollback also failed — manual intervention required")
                    .build();
        }
        return TransferResponse.builder()
                .debitSuccess(true)
                .creditSuccess(true)
                .debitTransactionId(transactionIdDebit)
                .creditTransactionId(transactionIdCredit)
                .debitResponse(debitResponse)
                .creditResponse(creditResponse)
                .message("Transfer completed successfully")
                .build();
    }

    private UserDTO convertToDto(User user){
        return modelmapper.map(user, UserDTO.class);
    }
}
