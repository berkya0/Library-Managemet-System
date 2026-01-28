package com.berkaykomur.dto;

import com.berkaykomur.model.Category;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoBook {
	
	private Long id;
	
	private String title;
	
	private String author;
	
	private Category category;
	
	private String isbnNo;
	
	private boolean available;
	

}
