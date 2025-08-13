package com.project.monika.service;

import com.project.monika.model.dto.OpenWalletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class IntegrationService {

    //imported properties
    @Value("${thirdparty.private.api.key}")
    private String apiKey;
    @Value("${thirdparty.public.api.secret}")
    private String secretKey;
    @Value("${third-party-baseUrl}")
    private String baseUrl;
    @Value("${third-party-username}")
    private String username;
    @Value("${third-party-password}")
    private String password;

    @Autowired
    private RestTemplate restTemplate;

    public static String sha512(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-512 algorithm not available", e);
        }
    }

    public HttpHeaders authenticate() {
        String url = baseUrl + "/waas/api/v1/authenticate";
        String timestamp = String.valueOf(System.currentTimeMillis());
        String signature = sha512(apiKey + timestamp + secretKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.set("x-api-key", apiKey);
//        headers.set("x-signature", signature);
//        headers.set("x-timestamp", timestamp);

        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        body.put("clientId", "waas");
        body.put("clientSecret",secretKey);
        log.info("Response :: {}", body);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);
        log.info("Response :: {}", entity);
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        log.info("Response :: {}", response);

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Authentication successful");
            return headers;
        } else {
            log.error("Authentication failed: {}", response.getBody());
            throw new RuntimeException("Third-party authentication failed");
        }
    }

    public Object openWallet(OpenWalletRequest requestDto) {
        String url = baseUrl + "/waas/api/v1/open_wallet";
        HttpHeaders headers = authenticate();
        HttpEntity<OpenWalletRequest> entity = new HttpEntity<>(requestDto, headers);
        ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, entity, Object.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            log.info("Wallet opened successfully: {}", response);
            return response.getBody();
        } else {
            log.error("Failed to open wallet: {}", response.getBody());
            throw new RuntimeException("Failed to open wallet");
        }
    }

    public Object debitAccount(String accountNo, BigDecimal amount, String narration, String transactionId) {
        String url = baseUrl + "/waas/api/v1/debit/transfer";

        Map<String, Object> payload = Map.of(
                "accountNo", accountNo,
                "narration", narration,
                "totalAmount", amount,
                "transactionId", transactionId,
                "merchant", Map.of(
                        "isFee", false,
                        "merchantFeeAccount", "",
                        "merchantFeeAmount", ""
                )
        );

        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, authenticate());
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, request, Object.class);
            return response.getStatusCode().is2xxSuccessful() ? response.getBody() : null;
        } catch (Exception e) {
            log.error("Debit API call failed", e);
            return null;
        }
    }
    public Object creditAccount(String accountNo, BigDecimal amount, String narration, String transactionId) {
        String url = baseUrl + "/api/v1/credit/transfer";

        Map<String, Object> payload = Map.of(
                "accountNo", accountNo,
                "narration", narration,
                "totalAmount", amount,
                "transactionId", transactionId,
                "merchant", Map.of(
                        "isFee", false,
                        "merchantFeeAccount", "",
                        "merchantFeeAmount", ""
                )
        );
        try {
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, authenticate());
            ResponseEntity<Object> response = restTemplate.exchange(url, HttpMethod.POST, request, Object.class);
            return response.getStatusCode().is2xxSuccessful() ? response.getBody() : null;
        } catch (Exception e) {
            log.error("Credit API call failed", e);
            return null;
        }
    }

}
