package com.berkaykomur.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.berkaykomur.controller.IRestAuthenticationController;
import com.berkaykomur.controller.RestBaseController;
import com.berkaykomur.dto.AuthRequest;
import com.berkaykomur.dto.AuthResponse;
import com.berkaykomur.dto.DtoUser;
import com.berkaykomur.dto.RefreshTokenRequest;
import com.berkaykomur.dto.RegisterRequest;
import com.berkaykomur.model.RootEntity;
import com.berkaykomur.service.IAuthenticationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rest/api")
public class RestAuthenticationController extends RestBaseController implements IRestAuthenticationController {

	@Autowired
	private IAuthenticationService authenticationService;

	@Override
	@PostMapping("/user/register")
	public RootEntity<DtoUser> registerUser(@Valid @RequestBody RegisterRequest request) {
		return ok(authenticationService.registerUser(request));
	}

	@PostMapping("/admin/register")
	@Override
	public RootEntity<DtoUser> registerAdmin(@Valid @RequestBody RegisterRequest request) {
		return ok(authenticationService.registerAdmin(request));
	}

	@Override
	@PostMapping("/authenticate")
	public RootEntity<AuthResponse> authentica(@Valid @RequestBody AuthRequest input) {
		return ok(authenticationService.authenticate(input));
	}

	@Override
	@PostMapping("/refresh-token")
	public RootEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest input) {
		return ok(authenticationService.refreshToken(input));
	}

}
