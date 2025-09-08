package com.otakumap.global.security.jwt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtDTO {
    @NotBlank(message = "Access Token 입력은 필수입니다.")
    @Schema(description = "accessToken")
    private String accessToken;

    @NotBlank(message = "Refresh Token 입력은 필수입니다.")
    @Schema(description = "refreshToken")
    private String refreshToken;
}
