package com.berkaykomur.controller;

import java.util.List;

import com.berkaykomur.dto.DtoLoan;
import com.berkaykomur.dto.LoanRequest;
import com.berkaykomur.model.RootEntity;

public interface IRestLoanController {
	
	 RootEntity<DtoLoan> loanBook(LoanRequest request);
	 RootEntity<List<DtoLoan>> getMyLoans();
     RootEntity<DtoLoan> returnBook(Long loanId);
	 RootEntity<List<DtoLoan>> getAllLoans();
	

}
