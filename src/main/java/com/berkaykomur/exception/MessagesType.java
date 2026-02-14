package com.berkaykomur.exception;

import lombok.Getter;

@Getter
public enum MessagesType {
	TOKEN_IS_INVALID("1001","Token bulunamadı"),
	NO_RECORD_EXIST ("1004","Kayıt bulunamadı"),
	USERNAME_NOT_FOUND("1006","Username bulunamadı"),
	USERNAME_OR_PASSWORD_INVALID("1007","Kullanıcı adı veya şifre geçersiz"),
	TOKEN_IS_EXPIRED("1008","Tokenin süresi dolmuş"),
	REFRESH_TOKEN_INVALID("1010","Böyle bir refresh token bulunamadı"),
	REFRESH_TOKEN_IS_EXPIRED("1009","Refresh tokenin süresi dolmuş"),
	GENERAL_EXCEPTION("1011","Genel bir hata oluştu"),
	UNAUTHORIZED_ACTIO("1012","Bunu yapmaya yetkin yok!"),
	ALREADY_LOANED("1013","Bu kitap başkası tarafından zaten ödünç alınmış"),
    USERNAME_ALREADY_TAKEN("1025","Kullanıcı adı alınmış");
	
	private String code;
	private String errorMessage;
	
	MessagesType(String code,String errorMessage){
		this.code=code;
		this.errorMessage=errorMessage;
	}

	

}
