package com.otakumap.domain.auth.enums;

public enum AuthEmailType {
    SIGNUP("오타쿠맵 이메일 인증 안내",
            "<p>회원가입을 위한 이메일 인증을 진행합니다.</p>" +
                    "<p>아래 발급된 인증번호를 입력하여 인증을 완료해주세요.</p>"),

    FIND_PASSWORD("오타쿠맵 비밀번호 재설정 안내",
            "<p>비밀번호 찾기 요청을 받았습니다.</p>" +
                    "<p>아래 발급된 인증번호를 입력하여 비밀번호를 재설정해주세요.</p>");

    private final String subject;
    private final String bodyContent;

    AuthEmailType(String subject, String bodyContent) {
        this.subject = subject;
        this.bodyContent = bodyContent;
    }

    public String getSubject() {
        return subject;
    }

    public String getBodyContent() {
        return bodyContent;
    }

}