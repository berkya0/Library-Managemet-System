package com.berkaykomur.controller;

import com.berkaykomur.dto.DtoUser;
import com.berkaykomur.dto.DtoUserIU;
import com.berkaykomur.model.RootEntity;

public interface IRestUserController {
	 RootEntity<DtoUser> updateUser (DtoUserIU dtoUserIU);
	 RootEntity<Void> deleteUser(Long id);

}
