package com.otakumap.domain.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.ResponseCookie;

import java.time.LocalDateTime;

public class AuthResponseDTO {
    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LoginResultDTO {
        Long id;
        String accessToken;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CheckIdResultDTO {
        boolean isDuplicated;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CheckEmailResultDTO {
        boolean isDuplicated;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class VerifyCodeResultDTO {
        boolean isVerified;
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class FindIdResultDTO {
        String userId;
    }
}
