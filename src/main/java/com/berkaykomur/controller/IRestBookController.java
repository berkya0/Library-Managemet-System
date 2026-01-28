package com.berkaykomur.controller;

import java.util.List;

import com.berkaykomur.dto.DtoBook;
import com.berkaykomur.dto.DtoBookIU;
import com.berkaykomur.model.RootEntity;

public interface IRestBookController {
	 RootEntity<DtoBook>saveBook(DtoBookIU dtoBookIU);
	 RootEntity<List<DtoBook>> findAllBooks();
	 RootEntity<DtoBook> findBookById(Long id);
	 RootEntity<Void> deleteBook(Long id);

}
