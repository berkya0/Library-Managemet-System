package com.berkaykomur.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//Member.java
@Entity
@Table(name = "member",
indexes = {
        @Index(name = "idx_fullName",columnList = "full_name"),
        @Index(name = "idx_email",columnList = "email"),
        @Index(name = "idx_phoneNumber",columnList = "phone_number")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {

    @Column(nullable = false)
    private String fullName;
    @Column(nullable = false,unique = true)
    private String email;
    @Column(nullable = false,unique = true)
    private String phoneNumber;

    @Temporal(TemporalType.TIMESTAMP)
     private Date membershipDate = new Date();
 
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Loan> loans;
}