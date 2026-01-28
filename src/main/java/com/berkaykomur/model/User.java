package com.berkaykomur.model;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.berkaykomur.enums.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//User.java
@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity implements UserDetails {
 private String username;
 private String password;
 
 @Enumerated(EnumType.STRING)
 private Role role;
 
 @DateTimeFormat(iso = ISO.DATE_TIME)
	private Date createTime;
 
 @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
 private Member member;  // orphanRemoval eklendi
 
 @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
 private List<RefreshToken> refreshTokens; // RefreshToken ilişkisi eklendi
 
 @Override
 public Collection<? extends GrantedAuthority> getAuthorities() {
     return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
 }
}
