package com.berkaykomur.dto;

import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.berkaykomur.enums.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoUser extends DtoBase {
    private String username;
    private Role role;
    private List<DtoRefreshToken> refreshTokens; // Yeni eklendi
	private Date createTime;
}