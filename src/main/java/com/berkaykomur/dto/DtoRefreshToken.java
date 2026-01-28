package com.berkaykomur.dto;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoRefreshToken extends DtoBase {
    private String refreshToken;
    private Date expiredDate;
    private Long userId; // User referansı için
   
	private Date createTime;
}