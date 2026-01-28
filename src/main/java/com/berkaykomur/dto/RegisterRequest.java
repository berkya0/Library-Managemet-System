package com.berkaykomur.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {
	//kullanıcı bilgileri
		@NotBlank
		private String username;
		@NotBlank
		private String password;
		
		//member bilgileri
		@NotBlank
		private String fullName;
		
		@NotBlank
		@Email
		private String email; 
		
		@NotBlank(message = "Telefon numarası boş olamaz")
	    @Size(min = 11 ,max = 11,message = "Tel no 11 haneli olmalıdır")
		private String phoneNumber;

}
