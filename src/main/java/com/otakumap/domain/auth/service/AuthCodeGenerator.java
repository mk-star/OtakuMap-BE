package com.otakumap.domain.auth.service;

import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * 랜덤 인증 번호 생성기
 */
@Service
public class AuthCodeGenerator {

    private static final int LEFT_LIMIT = 48; // number '0'
    private static final int RIGHT_LIMIT = 122; // alphabet 'z'
    private static final int TARGET_STRING_LENGTH = 6;
    private final Random random = new Random();

    /**
     * 6자리의 난수를 생성합니다.
     * @return 난수
     */
    public String generateCode() {
        return random.ints(LEFT_LIMIT, RIGHT_LIMIT + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 | i >= 97))
                .limit(TARGET_STRING_LENGTH)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

}
