package com.berkaykomur.dto;

import java.util.Date;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoMember extends DtoBase {
    private String fullName;
    private String email;
    private String phoneNumber;
    private Date membershipDate;
    private DtoUser user;
    private List<DtoLoan> loans; // Yeni eklendi
}