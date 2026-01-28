package com.berkaykomur.service.impl;

import java.util.Date;
import java.util.UUID;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.berkaykomur.dto.AuthRequest;
import com.berkaykomur.dto.AuthResponse;
import com.berkaykomur.dto.DtoMember;
import com.berkaykomur.dto.DtoUser;
import com.berkaykomur.dto.RefreshTokenRequest;
import com.berkaykomur.dto.RegisterRequest;
import com.berkaykomur.enums.Role;
import com.berkaykomur.exception.BaseException;
import com.berkaykomur.exception.ErrorMessage;
import com.berkaykomur.exception.MessagesType;
import com.berkaykomur.jwt.JwtService;
import com.berkaykomur.model.Member;
import com.berkaykomur.model.RefreshToken;
import com.berkaykomur.model.User;
import com.berkaykomur.repository.MemberRepository;
import com.berkaykomur.repository.RefreshTokenRepository;
import com.berkaykomur.repository.UserRepository;
import com.berkaykomur.service.IAuthenticationService;

@Service
public class AuthenticationService implements IAuthenticationService {

    // Dependency Injection (Bağımlılıkların Enjekte Edilmesi)
    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;
    @Autowired private AuthenticationProvider authenticationProvider;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private MemberRepository memberRepository;

    // Yeni Kullanıcı Oluşturma (Register İşlemi İçin)
    private User createUser(RegisterRequest request, Role role) {
        // Aynı kullanıcı adıyla kayıt var mı kontrolü

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword())); // Şifre hash'leniyor
        user.setCreateTime(new Date());
        user.setRole(role);
        
        // Kullanıcı bilgilerini Member entity'sine kopyalama
        Member member = new Member();
        member.setEmail(request.getEmail());
        member.setFullName(request.getFullName());
        member.setPhoneNumber(request.getPhoneNumber());
        member.setUser(user);
        user.setMember(member);
        
        return user;
    }

    // Refresh Token Oluşturma
    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setCreateTime(new Date());
        refreshToken.setUser(user);
        refreshToken.setExpiredDate(new Date(System.currentTimeMillis() + JwtService.REFRESH_TOKEN_EXPIRATION));
        refreshToken.setRefreshToken(UUID.randomUUID().toString()); // Rastgele token üretimi
        return refreshToken;
    }

    // Refresh Token Geçerlilik Kontrolü
    private boolean isRefreshTokenValid(Date expireDate) {
        return new Date().before(expireDate);
    }

    // Token Yenileme İşlemi
    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        // Veritabanında token var mı kontrolü
        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(request.getRefreshToken())
            .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType.REFRESH_TOKEN_INVALID, "")));
        
        // Token süresi dolmuş mu kontrolü
        if (!isRefreshTokenValid(refreshToken.getExpiredDate())) {
            throw new BaseException(new ErrorMessage(MessagesType.REFRESH_TOKEN_IS_EXPIRED, ""));
        }
        
        // Yeni token'lar oluşturuluyor
        User user = refreshToken.getUser();
        String accessToken = jwtService.generateToken(user, user.getMember().getId());
        RefreshToken newRefreshToken = refreshTokenRepository.save(createRefreshToken(user));
        
        return new AuthResponse(
            accessToken, 
            newRefreshToken.getRefreshToken(), 
            user.getUsername(), 
            user.getRole(), 
            JwtService.ACCESS_TOKEN_EXPIRATION
        );
    }

    // Normal Kullanıcı Kayıt İşlemi
    @Override
    public DtoUser registerUser(RegisterRequest request) {
        User savedUser = userRepository.save(createUser(request, Role.USER));
        return convertToDtoUser(savedUser);
    }

    // Kullanıcı Giriş İşlemi (Authentication)
    @Override
    public AuthResponse authenticate(AuthRequest request) {
        try {
            // Spring Security ile kimlik doğrulama
            Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Kullanıcı bilgilerini al
            User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType.USERNAME_OR_PASSWORD_INVALID, "")));
            
            // Token'ları oluştur
            String accessToken = jwtService.generateToken(user, user.getMember().getId());
            RefreshToken refreshToken = refreshTokenRepository.save(createRefreshToken(user));
            
            return new AuthResponse(
                accessToken, 
                refreshToken.getRefreshToken(), 
                user.getUsername(), 
                user.getRole(), 
                JwtService.ACCESS_TOKEN_EXPIRATION
            );
            
        } catch (Exception e) {
            throw new BaseException(new ErrorMessage(MessagesType.USERNAME_OR_PASSWORD_INVALID, e.getMessage()));
        }
    }
    
    // Admin Kayıt İşlemi
    @Override
    public DtoUser registerAdmin(RegisterRequest request) {
        User savedUser = userRepository.save(createUser(request, Role.ADMIN));
        return convertToDtoUser(savedUser);
    }

    // User Entity -> DtoUser Dönüşümü
    private DtoUser convertToDtoUser(User user) {
        DtoUser dtoUser = new DtoUser();
        BeanUtils.copyProperties(user, dtoUser);
        return dtoUser;
    }

    // Member Entity -> DtoMember Dönüşümü
    private DtoMember convertToDtoMember(Member member) {
        DtoMember dtoMember = new DtoMember();
        DtoUser dtoUser = convertToDtoUser(member.getUser());
        BeanUtils.copyProperties(member, dtoMember);
        dtoMember.setUser(dtoUser);
        return dtoMember;
    }
}