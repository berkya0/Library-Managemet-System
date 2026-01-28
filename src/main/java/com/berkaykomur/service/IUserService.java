package com.berkaykomur.service;

import com.berkaykomur.dto.DtoUser;
import com.berkaykomur.dto.DtoUserIU;

public interface IUserService {
	
	 DtoUser updateUser(DtoUserIU dtoUserIU);
	 void deleteUserById(Long id);

}
