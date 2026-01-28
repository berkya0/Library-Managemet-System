package com.berkaykomur.controller.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.berkaykomur.controller.IRestMemberController;
import com.berkaykomur.controller.RestBaseController;
import com.berkaykomur.dto.DtoMember;
import com.berkaykomur.dto.DtoMemberIU;
import com.berkaykomur.enums.Role;
import com.berkaykomur.exception.BaseException;
import com.berkaykomur.exception.ErrorMessage;
import com.berkaykomur.exception.MessagesType;
import com.berkaykomur.model.RootEntity;
import com.berkaykomur.service.IMemberService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/rest/api/member")
public class RestMemberControllerImpl extends RestBaseController implements IRestMemberController {
    @Autowired
    private IMemberService memberService;
    @Override
    @GetMapping("/get/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.isOwner(authentication, #id)")
    public RootEntity<DtoMember> findMemberById(@Valid @PathVariable(name = "id") Long id) {
        return ok(memberService.findMemberById(id));
    }
    @Override
    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userSecurityService.isOwner(authentication, #id)")
    public RootEntity<DtoMember> updateMemberById(
            @PathVariable(value ="id") Long id,
            @Valid @RequestBody DtoMemberIU dtoMemberIU) {
        return ok(memberService.updateMemberById(id, dtoMemberIU));
    }
    @Override
    @GetMapping("/get/list")
    @PreAuthorize("hasRole('ADMIN')")
    public RootEntity<List<DtoMember>> findAllMembers() {
        return ok(memberService.findAllMembers());
    }
    @GetMapping("/me")
    public RootEntity<DtoMember> getCurrentUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        return ok(memberService.findMemberByUsername(username));
    }
    @Override
    @PutMapping("/update-role/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public RootEntity<DtoMember> updateMemberRole(
            @PathVariable(value = "id") Long id,
            @RequestBody Map<String, String> request) {
        try {
            Role newRole = Role.valueOf(request.get("role").toUpperCase());
            return ok(memberService.updateMemberRole(id, newRole));
        } catch (IllegalArgumentException e) {
            throw new BaseException(new ErrorMessage(MessagesType.GENERAL_EXCEPTION, "Geçersiz rol: " + request.get("role")));
        }
    }
}


