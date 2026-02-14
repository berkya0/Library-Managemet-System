package com.berkaykomur.service.impl;

import com.berkaykomur.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Component;

@Component("userSecurityService")
public class UserSecurityService {

    @Autowired
    private MemberRepository memberRepository;
    public boolean isOwner(Authentication authentication, Long memberId) {
        if (authentication == null || memberId == null) {
            return false;
        }
        String currentUsername = authentication.getName();
        return memberRepository.findById(memberId)
                .map(member -> member.getUser().getUsername().equals(currentUsername))
                .orElse(false);
    }
}
