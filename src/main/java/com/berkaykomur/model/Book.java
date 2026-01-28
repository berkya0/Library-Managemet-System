package com.berkaykomur.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "book")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book extends BaseEntity{

	private String title;
	
	private String author;
	
	@Enumerated(EnumType.STRING)
	private Category category;
	
	private String isbnNo;
	
	private boolean available;
	
	
}
