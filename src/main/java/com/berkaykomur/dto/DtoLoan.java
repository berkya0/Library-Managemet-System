package com.berkaykomur.dto;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DtoLoan extends DtoBase {
    private DtoBook book;
    private DtoMember member;
    private LocalDate loanDate;
    private LocalDate returnDate;
    private LocalDate dueDate;
    private boolean active;
}