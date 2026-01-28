package com.berkaykomur.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Loan.java
@Entity
@Table(name = "loan")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Loan extends BaseEntity {
 
 @ManyToOne
 @JoinColumn(name = "book_id", nullable = false)  // Foreign key açıkça belirtiliyor
 private Book book;
 
 @ManyToOne
 @JoinColumn(name = "member_id", nullable = false)  // Foreign key açıkça belirtiliyor
 private Member member;
 
 private LocalDate loanDate;
 private LocalDate returnDate;
 private LocalDate dueDate;
 
 public void setLoanDetails() {
     this.loanDate = LocalDate.now();
     this.dueDate = LocalDate.now().plusDays(14);
 }
}