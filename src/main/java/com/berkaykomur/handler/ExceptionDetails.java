package com.berkaykomur.handler;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionDetails<T> {

	private Date errorTime;
	
	private String hostName;
	
	private String path;
	
	private T error;
}
