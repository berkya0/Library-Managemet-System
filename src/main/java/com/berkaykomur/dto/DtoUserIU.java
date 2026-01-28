package com.berkaykomur.dto;

import java.util.Date;

import com.berkaykomur.enums.Role;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class DtoUserIU {
	
	private String username;
	private String password;
	private Role role;
	private DtoMember member;
	private Date createTime;
	

}
