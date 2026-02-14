package com.berkaykomur.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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
@Service
public class MemberService implements IMemberService {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private UserRepository userRepository;

    @Override
    public DtoMember findMemberById(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType. NO_RECORD_EXIST,id.toString())));
        return convertToDto(member);
    }

    @Override
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.isOwner(authentication, #id)")
    public DtoMember updateMemberById(Long id, DtoMemberIU dtoMemberIU) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType. NO_RECORD_EXIST,id.toString())));

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

    @Override
    public DtoMember updateMemberRole(Long id, Role newRole) {
  
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType.NO_RECORD_EXIST, id.toString())));

        user.setRole(newRole);
        userRepository.save(user);

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