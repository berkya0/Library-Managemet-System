package com.berkaykomur.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.berkaykomur.model.Loan;

@Repository
public interface LoanRepository extends JpaRepository<Loan, Long> {
	  List<Loan> findByMemberId(Long memberId); // Member ID'ye göre ödünç kayıtlarını getirir
	  void deleteByBookId(Long bookId);

}
