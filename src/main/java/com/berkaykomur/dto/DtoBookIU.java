package com.berkaykomur.dto;

import com.berkaykomur.model.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoBookIU {
    @NotBlank
    @Size(max = 200, message = "Kitap adı 200 karakterden uzun olamaz")
    private String title;
    
    @NotBlank
    @Size(max = 100, message = "Yazar adı 100 karakterden uzun olamaz")
    private String author;
    
    @NotNull
    private Category category;

    @NotBlank
   @Size(min=13,max=13,message = "ISBN 13 haneli olmalıdır")
    private String isbnNo;
    
    private boolean available;
}