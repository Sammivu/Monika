package com.project.monika.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OpenWalletRequest {
    private String bvn;
    private String dateOfBirth;
    private int gender;
    private String lastName;
    private String otherNames;
    private String phoneNo;
    private String transactionTrackingRef;
    private String placeOfBirth;
    private String address;
    private String nationalIdentityNo;
    private String ninUserId;
    private String nextOfKinPhoneNo;
    private String nextOfKinName;
    private String email;
}
