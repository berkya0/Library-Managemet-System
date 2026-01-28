package com.berkaykomur.service;

import java.util.List;

import com.berkaykomur.dto.DtoBook;
import com.berkaykomur.dto.DtoBookIU;

public interface IBookService {

	 DtoBook saveBook(DtoBookIU dtoBookIU);
	 List<DtoBook> findAllBooks();
	 DtoBook findBookById(Long id);
	 void deleteBook(Long id);

}
