package com.berkaykomur.controller;

import com.berkaykomur.dto.AuthRequest;
import com.berkaykomur.dto.AuthResponse;
import com.berkaykomur.dto.DtoUser;
import com.berkaykomur.dto.RefreshTokenRequest;
import com.berkaykomur.dto.RegisterRequest;
import com.berkaykomur.model.RootEntity;

public interface IRestAuthenticationController {
	 RootEntity<DtoUser> registerUser(RegisterRequest request);
	 RootEntity<DtoUser> registerAdmin(RegisterRequest request);
	 RootEntity<AuthResponse> authentica(AuthRequest input);
	 RootEntity<AuthResponse> refreshToken(RefreshTokenRequest input);
	
}
