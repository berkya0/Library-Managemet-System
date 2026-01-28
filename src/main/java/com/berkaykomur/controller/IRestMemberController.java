package com.berkaykomur.controller;

import java.util.List;
import java.util.Map;

import com.berkaykomur.dto.DtoMember;
import com.berkaykomur.dto.DtoMemberIU;
import com.berkaykomur.model.RootEntity;

public interface IRestMemberController {
	 RootEntity<DtoMember> findMemberById(Long id);
	 RootEntity<DtoMember> updateMemberById(Long id,DtoMemberIU dtoMemberIU);
	 RootEntity<List<DtoMember>> findAllMembers();
	 RootEntity<DtoMember> updateMemberRole(Long id,Map<String, String> request);

}
