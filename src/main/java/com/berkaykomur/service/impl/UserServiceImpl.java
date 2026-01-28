package com.berkaykomur.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.berkaykomur.dto.DtoUser;
import com.berkaykomur.dto.DtoUserIU;
import com.berkaykomur.exception.BaseException;
import com.berkaykomur.exception.ErrorMessage;
import com.berkaykomur.exception.MessagesType;
import com.berkaykomur.model.User;
import com.berkaykomur.repository.MemberRepository;
import com.berkaykomur.repository.UserRepository;
import com.berkaykomur.service.IUserService;



@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public DtoUser updateUser(DtoUserIU dtoUserIU) {
        User user = userRepository.findByUsername(dtoUserIU.getUsername())
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType. NO_RECORD_EXIST, dtoUserIU.getUsername())));

        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = isAdmin();

        if (!currentUsername.equals(user.getUsername()) && !isAdmin) {
            throw new BaseException(new ErrorMessage(MessagesType.UNAUTHORIZED_ACTIO,""));
        }

        if (dtoUserIU.getPassword() != null && !dtoUserIU.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(dtoUserIU.getPassword()));
        }
        if (isAdmin && dtoUserIU.getRole() != null) {
            user.setRole(dtoUserIU.getRole());
        }
        return convertToDto(userRepository.save(user));
    }
    @Override
    @Transactional
    public void deleteUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BaseException(new ErrorMessage(MessagesType.NO_RECORD_EXIST, id.toString())));

        // Yetki kontrolü
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = isAdmin();

        if (!currentUsername.equals(user.getUsername()) && !isAdmin) {
            throw new BaseException(new ErrorMessage(MessagesType.UNAUTHORIZED_ACTIO, "Bu işlem için yetkiniz yok"));
        }

        // Aktif ödünç alma kontrolü
        if (user.getMember() != null) {
            boolean hasActiveLoans = user.getMember().getLoans().stream()
                    .anyMatch(loan -> loan.getReturnDate() == null);

            if (hasActiveLoans) {
                throw new BaseException(new ErrorMessage(MessagesType.GENERAL_EXCEPTION,
                        "Ödünç alınmış kitapları olan kullanıcı silinemez. Önce kitapları iade etmelisiniz."));
            }

            // İlişkili member'ı sil
            memberRepository.delete(user.getMember());
        }

        // Kullanıcıyı sil (member silindikten sonra)
        userRepository.delete(user);
    }
    private DtoUser convertToDto(User user) {
        DtoUser dto = new DtoUser();
        BeanUtils.copyProperties(user, dto);
        return dto;
    }
    private boolean isAdmin() {
        return SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}