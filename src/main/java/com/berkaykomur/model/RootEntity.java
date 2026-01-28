package com.berkaykomur.model;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RootEntity<T> {
	private Integer status;
	private T payload;
	private String errorMessage;
	public static <T>RootEntity<T> ok(T payload){
		RootEntity<T> rootEntity=new RootEntity<>();
		rootEntity.setStatus(200);
		rootEntity.setErrorMessage(null);
		rootEntity.setPayload(payload);
		return rootEntity;
	}
	public static <T>RootEntity<T> error(String errorMessage){
		RootEntity<T> rootEntity=new RootEntity<>();
		rootEntity.setStatus(500);
		rootEntity.setErrorMessage(errorMessage);
		rootEntity.setPayload(null);
		return rootEntity;
	}
}
