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


    @Autowired private BCryptPasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;
    @Autowired private AuthenticationProvider authenticationProvider;
    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private UserRepository userRepository;
    private User createUser(RegisterRequest request, Role role) {

        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BaseException(new ErrorMessage((MessagesType.USERNAME_ALREADY_TAKEN),request.getUsername()));
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setCreateTime(new Date());
        user.setRole(role);

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
        refreshToken.setExpiredDate(new Date(System.currentTimeMillis() + jwtService.refreshTokenExpiration));
        refreshToken.setRefreshToken(UUID.randomUUID().toString()); // Rastgele token üretimi
        return refreshToken;
    }
    private boolean isRefreshTokenValid(Date expireDate) {
        return new Date().before(expireDate);
    }


    @Override
    public AuthResponse refreshToken(RefreshTokenRequest request) {

        RefreshToken refreshToken = refreshTokenRepository.findByRefreshToken(request.getRefreshToken())
            .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType.REFRESH_TOKEN_INVALID, "")));

        if (!isRefreshTokenValid(refreshToken.getExpiredDate())) {
            throw new BaseException(new ErrorMessage(MessagesType.REFRESH_TOKEN_IS_EXPIRED, ""));
        }
        User user = refreshToken.getUser();
        String accessToken = jwtService.generateToken(user, user.getMember().getId());
        RefreshToken newRefreshToken = refreshTokenRepository.save(createRefreshToken(user));
        
        return new AuthResponse(
            accessToken, 
            newRefreshToken.getRefreshToken(), 
            user.getUsername(), 
            user.getRole(), 
            jwtService.accsessTokenExpiration
        );
    }


    @Override
    public DtoUser registerUser(RegisterRequest request) {
        User savedUser = userRepository.save(createUser(request, Role.USER));
        return convertToDtoUser(savedUser);
    }

    @Override
    public AuthResponse authenticate(AuthRequest request) {
        try {

            Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType.USERNAME_OR_PASSWORD_INVALID, "")));

            String accessToken = jwtService.generateToken(user, user.getMember().getId());
            RefreshToken refreshToken = refreshTokenRepository.save(createRefreshToken(user));
            
            return new AuthResponse(
                accessToken, 
                refreshToken.getRefreshToken(),
                user.getUsername(), 
                user.getRole(),
                    jwtService.accsessTokenExpiration
            );
        } catch (Exception e) {
            throw new BaseException(new ErrorMessage(MessagesType.USERNAME_OR_PASSWORD_INVALID, e.getMessage()));
        }
    }

    @Override
    public DtoUser registerAdmin(RegisterRequest request) {
        User savedUser = userRepository.save(createUser(request, Role.ADMIN));
        return convertToDtoUser(savedUser);
    }

    private DtoUser convertToDtoUser(User user) {
        DtoUser dtoUser = new DtoUser();
        BeanUtils.copyProperties(user, dtoUser);
        return dtoUser;
    }
}