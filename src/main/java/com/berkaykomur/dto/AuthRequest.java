package com.berkaykomur.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRequest {

	//kullanıcı bilgileri
	@NotBlank
	private String username;
	@NotBlank
	private String password;
	
	
	
}
