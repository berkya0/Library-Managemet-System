package com.berkaykomur.controller.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.berkaykomur.controller.IRestBookController;
import com.berkaykomur.controller.RestBaseController;
import com.berkaykomur.dto.DtoBook;
import com.berkaykomur.dto.DtoBookIU;
import com.berkaykomur.model.RootEntity;
import com.berkaykomur.service.IBookService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rest/api/book")
public class RestBookImpl extends RestBaseController implements IRestBookController{
	@Autowired
	private IBookService bookService;
	@Override
	@GetMapping("/get/list")
	public RootEntity<List<DtoBook>> findAllBooks() {
		return ok(bookService.findAllBooks());
	}

	@Override
	@GetMapping("/get/{id}")
	public RootEntity<DtoBook> findBookById(@PathVariable(name = "id") Long id) {
		return ok(bookService.findBookById(id));
	}
	 @Override
	    @DeleteMapping("/delete/{id}")
	    @PreAuthorize("hasRole('ADMIN')")
	    public RootEntity<Void> deleteBook(@PathVariable(name = "id") Long id) {
	        bookService.deleteBook(id);
	        return ok(); 
	    }
	@PostMapping("/save")
	@PreAuthorize("hasRole('ADMIN')")
	public RootEntity<DtoBook> saveBook(@Valid @RequestBody DtoBookIU dtoBookIU) {
	    return ok(bookService.saveBook(dtoBookIU));
	}

}




