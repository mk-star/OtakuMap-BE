package com.otakumap.global.security.jwt.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefreshTokenRequestDTO {
    @NotNull(message = "유저 ID 입력은 필수입니다.")
    @Schema(description = "userId")
    private Long id;
}
