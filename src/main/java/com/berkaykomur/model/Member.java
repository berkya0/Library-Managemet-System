package com.berkaykomur.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Member.java
@Entity
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {
 private String fullName;
 private String email;
 private String phoneNumber;
 
 @Temporal(TemporalType.TIMESTAMP)
 private Date membershipDate = new Date();
 
 @OneToOne
 @JoinColumn(name = "user_id", referencedColumnName = "id")
 private User user;

 // Loan ile ilişki (CascadeType.ALL ve orphanRemoval ekleniyor)
 @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
 private List<Loan> loans;
}