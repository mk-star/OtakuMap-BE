package com.otakumap.global.security.jwt.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO (
    @NotBlank(message = "아이디 입력은 필수입니다.")
    String userId,

    @NotBlank(message = "비밀번호 입력은 필수입니다.")
    String password
) {
}