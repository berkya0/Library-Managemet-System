package com.berkaykomur.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class BaseException extends RuntimeException {
	
	public BaseException(ErrorMessage errorMessage) {
		super(errorMessage.prepearMessage());
		
	}

}
