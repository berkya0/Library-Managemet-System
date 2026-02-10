// LoanRequest.java
package com.berkaykomur.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoanRequest {
    @NotNull(message = "Kitap ID boş olamaz")
    private Long bookId;
    
    @NotNull(message = "Üye ID boş olamaz")
    private Long memberId;
}