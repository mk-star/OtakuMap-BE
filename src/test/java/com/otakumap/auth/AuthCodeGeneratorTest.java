package com.otakumap.auth;

import com.otakumap.domain.auth.service.AuthCodeGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

class AuthCodeGeneratorTest {

    @Test
    @DisplayName("6자리 난수를 생성한다.")
    void should_Generate_6Digit_AuthCode() {
        //given
        AuthCodeGenerator authCodeGenerator = new AuthCodeGenerator();

        //when
        String authCode = authCodeGenerator.generateCode();

        //then
        assertThat(authCode).isNotNull().hasSize(6);
    }

}
