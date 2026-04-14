package com.otakumap.auth;

import com.otakumap.domain.auth.enums.AuthEmailType;
import com.otakumap.domain.auth.service.AuthMailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
class AuthEmailServiceTest {

    @Autowired
    private AuthMailService mailService;

    @MockitoBean
    private JavaMailSender javaMailSender;

    @DisplayName("비동기로 인증 이메일 보낼 때 실패 시 3번 재시도한다")
    @Test
    void sendAsyncAuthenticationMail() {
        // given
        String email = "test@naver.com";
        String authenticationCode = "authCode";

        willThrow(new MailSendException("1차 실패"))
                .willThrow(new MailSendException("2차 실패"))
                .willThrow(new MailSendException("3차 실패"))
                .given(javaMailSender)
                .send(any(SimpleMailMessage.class));

        // when
        mailService.sendEmail(email, authenticationCode, AuthEmailType.SIGNUP);

        // then
        then(javaMailSender).should(timeout(5000).times(3))
                .send(any(SimpleMailMessage.class));
    }

    @Test
    void asyncUnexpectedExceptionTest() throws Exception {
        // given
        String email = "test@naver.com";
        String authenticationCode = "authCode";

        willThrow(new RuntimeException("예상치 못한 예외"))
                .given(javaMailSender)
                .send(any(SimpleMailMessage.class));

        // when
        mailService.sendEmail(email, authenticationCode, AuthEmailType.SIGNUP);

        // then
        verify(javaMailSender, timeout(2000).times(1))
                .send(any(SimpleMailMessage.class));
    }
}