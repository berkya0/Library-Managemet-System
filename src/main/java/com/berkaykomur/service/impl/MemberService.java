package com.berkaykomur.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.berkaykomur.dto.DtoMember;
import com.berkaykomur.dto.DtoMemberIU;
import com.berkaykomur.dto.DtoUser;
import com.berkaykomur.enums.Role;
import com.berkaykomur.exception.BaseException;
import com.berkaykomur.exception.ErrorMessage;
import com.berkaykomur.exception.MessagesType;
import com.berkaykomur.model.Member;
import com.berkaykomur.model.User;
import com.berkaykomur.repository.MemberRepository;
import com.berkaykomur.repository.UserRepository;
import com.berkaykomur.service.IMemberService;

// ... imports ...

@Service
public class MemberService implements IMemberService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private UserRepository userRepository; // Yeni eklendi
    @Override
    public DtoMember findMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType. NO_RECORD_EXIST,id.toString())));
        return convertToDto(member);
    }

    @Override
    public DtoMember updateMemberById(Long id, DtoMemberIU dtoMemberIU) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType. NO_RECORD_EXIST,id.toString())));

        checkAuthorization(member);

        member.setFullName(dtoMemberIU.getFullName());
        member.setEmail(dtoMemberIU.getEmail());
        member.setPhoneNumber(dtoMemberIU.getPhoneNumber());

        return convertToDto(memberRepository.save(member));
    }

    @Override
    public List<DtoMember> findAllMembers() {
        return memberRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    @Override
    public DtoMember findMemberByUsername(String username) {
        // Önce User'ı bulup onun Member'ını alıyoruz
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType. NO_RECORD_EXIST,username)));

        if (user.getMember() == null) {
            throw new BaseException(new ErrorMessage(MessagesType.NO_RECORD_EXIST, "Kullanıcıya ait member bilgisi bulunamadı"));
        }

        return convertToDto(user.getMember());
    }

    private DtoMember convertToDto(Member member) {
        DtoMember dto = new DtoMember();
        BeanUtils.copyProperties(member, dto);

        if (member.getUser() != null) {
            DtoUser userDto = new DtoUser();
            BeanUtils.copyProperties(member.getUser(), userDto);
            dto.setUser(userDto);
        }

        return dto;
    }

    private void checkAuthorization(Member member) {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isOwner = member.getUser().getUsername().equals(currentUsername);
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isOwner && !isAdmin) {
            throw new BaseException(new ErrorMessage(MessagesType.UNAUTHORIZED_ACTIO,"Bu işlem için yetkiniz yok"));
        }
    }
    @Override
    public DtoMember updateMemberRole(Long id, Role newRole) {
        // Kullanıcıyı bul
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType.NO_RECORD_EXIST, id.toString())));

        // Rolü güncelle
        user.setRole(newRole);
        userRepository.save(user);

        // Member bilgilerini döndür
        if (user.getMember() == null) {
            throw new BaseException(new ErrorMessage(MessagesType.NO_RECORD_EXIST, "Kullanıcıya ait member bilgisi bulunamadı"));
        }
        return convertToDto(user.getMember());
    }
    @Override
    public Long findMemberIdByUsername(String username) {
        return memberRepository.findMemberIdByUsername(username)
                .orElseThrow(() -> new BaseException(
                        new ErrorMessage(MessagesType.NO_RECORD_EXIST, "Üye bulunamadı: " + username)));
    }

}