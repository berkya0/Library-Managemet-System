package com.berkaykomur.controller.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.berkaykomur.controller.IRestUserController;
import com.berkaykomur.controller.RestBaseController;
import com.berkaykomur.dto.DtoUser;
import com.berkaykomur.dto.DtoUserIU;
import com.berkaykomur.model.RootEntity;
import com.berkaykomur.service.IUserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rest/api/user")
public class RestUserControllerImpl extends RestBaseController implements IRestUserController{

	@Autowired
	private IUserService userService;
	@Override
	@PutMapping("/update")
	public RootEntity<DtoUser> updateUser(@Valid @RequestBody DtoUserIU dtoUserIU) {
		return ok(userService.updateUser(dtoUserIU));
	}
	@DeleteMapping("/delete/{id}")
	@PreAuthorize("hasRole('ADMIN') or @userSecurityService.isOwner(authentication, #id)")
	public RootEntity<Void> deleteUser(@PathVariable(value = "id") Long id) {
	    userService.deleteUserById(id);
	    return ok();
	}

}




