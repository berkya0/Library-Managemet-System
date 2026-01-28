package com.berkaykomur.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoLoanIU {
    @NotNull
    private Long bookId; // DtoBook yerine direkt ID
    
    @NotNull
    private Long memberId; // DtoMember yerine direkt ID
    
    @NotNull
    private LocalDate loanDate;
    
    private LocalDate returnDate; // Null olabilir
}