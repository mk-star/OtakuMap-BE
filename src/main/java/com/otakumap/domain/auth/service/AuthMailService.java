package com.otakumap.domain.auth.service;

import com.otakumap.domain.auth.enums.AuthEmailType;
import com.otakumap.global.apiPayload.code.status.ErrorStatus;
import com.otakumap.global.apiPayload.exception.handler.AuthHandler;
import com.otakumap.global.util.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetrySynchronizationManager;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthMailService {

    private static final String AUTH_CODE_KEY_PREFIX = "authcode:";
    private static final long AUTH_CODE_EXPIRE_TIME = 30 * 60 * 1000L;
    private static final int RETRY_MAX_ATTEMPTS = 3;

    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil;

    @Value("${spring.mail.username}")
    private String senderEmail;

    /**
     * 인증 이메일 MimeMessage 객체를 생성합니다.
     * @param email 수신사 이메일
     * @param code 인증 코드
     * @param emailType 회원가입/비밀번호 찾기 구분
     * @return SimpleMailMessage
     */
    private SimpleMailMessage createEmailForm(String email, String code, AuthEmailType emailType) {
        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(senderEmail);
        message.setTo(email);
        message.setSubject(emailType.getSubject());


        String body = String.format(
                "%s<br><p>인증번호: <strong>%s</strong></p>",
                emailType.getBodyContent(),
                code
        );

        message.setText(body);
        return message;
    }

    /**
     * 인증 코드를 포함한이메일을 전송합니다.
     * @param email 수신자 이메일
     * @param emailType 회원가입/비밀번호 찾기 구분
     * 메일 예외 발생 시, 1초마다 총 3회 재시도
     */
    @Async("mailExecutor")
    @Retryable(
            retryFor = MailSendException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000)
    )
    public void sendEmail(String email, String code, AuthEmailType emailType) {
            // 메일 발송
            RetryContext context = RetrySynchronizationManager.getContext();
            int retryCount = context != null ? context.getRetryCount() + 1 : 1;
            log.info("[메일 전송 시도] email={}, 시도 횟수={}/{}", email, retryCount, RETRY_MAX_ATTEMPTS);

            SimpleMailMessage  message = createEmailForm(email, code, emailType);
            javaMailSender.send(message);

            log.info("[메일 전송 완료] email={}", email);

            // Redis에 인증 코드를 저장
            String redisKey = AUTH_CODE_KEY_PREFIX + email + ":" + emailType.name().toLowerCase();
            redisUtil.set(redisKey, code, AUTH_CODE_EXPIRE_TIME, TimeUnit.MILLISECONDS);
    }

    // 메일 서버 에러
    @Recover
    public void recover(MailException ex, String email, String code, AuthEmailType emailType) {
        log.error("[메일 전송 최종 실패] email={}, type={}, message={}",
                email, emailType, ex.getMessage(), ex);
    }

    // 우리 서버 에러
    @Recover
    public void recover(Exception ex, String email, String code, AuthEmailType emailType) throws Exception {
        log.error("[System-Error] 예상치 못한 시스템 예외 발생: {}", ex.getClass().getSimpleName());
        throw ex;
    }

    /**
     * 인증 번호를 인증합니다.
     * Redis에 저장된 인증 번호와 유저가 입력한 인증 번호의 일치 여부
     * @param email 수신자 이메일
     * @param code 입력한 코드
     * @param emailType 회원가입/비밀번호 찾기 구분
     */
    public void verifyAuthCode(String email, String code, AuthEmailType emailType) {
        String redisKey = AUTH_CODE_KEY_PREFIX + email + ":" + emailType.name().toLowerCase();
        String expectedCode = redisUtil.get(redisKey);

        if (expectedCode == null) {
            throw new AuthHandler(ErrorStatus.CODE_EXPIRED);
        }
        if (!expectedCode.equals(code)) {
            throw new AuthHandler(ErrorStatus.CODE_NOT_EQUAL);
        }
    }

}
