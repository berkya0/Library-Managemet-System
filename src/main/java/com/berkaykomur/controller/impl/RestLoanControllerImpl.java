// RestLoanControllerImpl.java
package com.berkaykomur.controller.impl;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.berkaykomur.controller.IRestLoanController;
import com.berkaykomur.controller.RestBaseController;
import com.berkaykomur.dto.DtoLoan;
import com.berkaykomur.dto.LoanRequest;
import com.berkaykomur.exception.BaseException;
import com.berkaykomur.exception.ErrorMessage;
import com.berkaykomur.exception.MessagesType;
import com.berkaykomur.model.RootEntity;
import com.berkaykomur.service.ILoanService;
import com.berkaykomur.service.IMemberService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rest/api/loan")
public class RestLoanControllerImpl extends RestBaseController implements IRestLoanController {
    @Autowired
    private ILoanService loanService;
    @Autowired
    private IMemberService memberService;
    @Override
    @PostMapping("/borrow")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public RootEntity<DtoLoan> loanBook(@Valid @RequestBody LoanRequest request) {
        // Mevcut kullanıcının memberId'si ile request'teki memberId'yi karşılaştır
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();     
        return ok(loanService.loanBook(request));
    }
    @Override
    @GetMapping("/my-loans")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public RootEntity<List<DtoLoan>> getMyLoans() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Örnek: MemberService üzerinden username'e göre memberId bulunabilir
        Long memberId = memberService.findMemberIdByUsername(username);
        
        return ok(loanService.getLoansByMemberId(memberId));
    }
    @Override
    @PostMapping("/return/{loanId}")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public RootEntity<DtoLoan> returnBook(@PathVariable Long loanId) {
        // İade işlemini yapan kullanıcının bu ödünç kaydına ait olup olmadığını kontrol et
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        Long memberId = memberService.findMemberIdByUsername(username);
        
        // Ödünç kaydının bu kullanıcıya ait olduğunu doğrula
        if (!loanService.isLoanBelongsToMember(loanId, memberId)) {
            throw new BaseException(
                new ErrorMessage(MessagesType.UNAUTHORIZED_ACTIO, 
                "Bu işlemi yapmaya yetkiniz yok"));
        }
        
        return ok(loanService.returnBook(loanId));
    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public RootEntity<List<DtoLoan>> getAllLoans() {
        return ok(loanService.getAllLoans());
    }
}