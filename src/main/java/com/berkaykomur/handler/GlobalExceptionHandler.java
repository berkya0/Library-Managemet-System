package com.berkaykomur.handler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.berkaykomur.exception.BaseException;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(value = {BaseException.class})
	public ResponseEntity<ApiError<?>> handleBaseException(BaseException ex ,WebRequest request) {
		
		return ResponseEntity.badRequest().body(createApiError(ex.getMessage(), request));
	}
	
	@ExceptionHandler(value = {MethodArgumentNotValidException.class})
	public ResponseEntity<ApiError<Map<String, List<String>>>> handleValidException(MethodArgumentNotValidException e,WebRequest request) {
		Map<String, List<String>>map=new HashMap<>();
		for (ObjectError objectError : e.getBindingResult().getAllErrors()) {
			String fieldName=((FieldError)objectError).getField();
			if(map.containsKey(fieldName)) {
				map.put(fieldName, addValue(map.get(fieldName), objectError.getDefaultMessage()));
			} else {
				map.put(fieldName, addValue(new ArrayList<>(), objectError.getDefaultMessage()));
			}	
		}
		return ResponseEntity.badRequest().body(createApiError(map, request));
	}
	
	public List<String> addValue(List<String> list,String newValue){
		list.add(newValue);
		return list;
	}
	
	public <T>ApiError<T> createApiError(T errorMessage,WebRequest request){
		
		ApiError<T> apiError=new ApiError<>();
		apiError.setStatus(HttpStatus.UNAUTHORIZED.value());
		ExceptionDetails<T> exception =new ExceptionDetails<>();
		exception.setError(errorMessage);
		exception.setErrorTime(new Date());
		exception.setHostName(getHostName());
		exception.setPath(request.getDescription(false).substring(4));
		apiError.setExceptionDetails(exception);
		
		return apiError;
	}
	
	public String getHostName() {
		try {
			return InetAddress.getLocalHost().getHostName();
			
		}catch (UnknownHostException e) {
			e.getMessage();
		}
		return "";
	}

}
