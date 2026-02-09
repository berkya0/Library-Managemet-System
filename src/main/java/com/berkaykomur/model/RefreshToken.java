package com.berkaykomur.model;
import java.util.Date;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "refresh_token")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String refreshToken;
    @Column(nullable = false)
    private Date expiredDate;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false) // Foreign key açıkça belirtildi
    private User user;
    @Column(nullable = false)
	private Date createTime;
}