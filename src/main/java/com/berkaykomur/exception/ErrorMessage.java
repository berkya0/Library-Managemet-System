package com.berkaykomur.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorMessage {
	
	private MessagesType messagesType;
	private String ofStatic;
	
	public String prepearMessage() {
		
		StringBuilder builder=new StringBuilder();
		builder.append(messagesType.getErrorMessage());
		if(this.ofStatic!=null) {
			builder.append(" : "+ofStatic);
		}
		return builder.toString();
	}

}
