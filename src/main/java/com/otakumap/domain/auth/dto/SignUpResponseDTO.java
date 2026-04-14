package com.otakumap.domain.auth.dto;

import java.time.LocalDateTime;

public record SignUpResponseDTO(
        Long id,
        LocalDateTime createdAt
) {
}
