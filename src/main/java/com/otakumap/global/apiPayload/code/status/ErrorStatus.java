package com.otakumap.global.apiPayload.code.status;

import com.otakumap.global.apiPayload.code.BaseErrorCode;
import com.otakumap.global.apiPayload.code.ErrorReasonDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorStatus implements BaseErrorCode {

    // 일반적인 응답
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST,"COMMON400","잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED,"COMMON401","인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON404", "잘못된 입력 값입니다"),

    // 인증 관련 에러
    PASSWORD_NOT_EQUAL(HttpStatus.BAD_REQUEST, "AUTH4001", "비밀번호가 일치하지 않습니다."),
    EMAIL_CODE_EXPIRED(HttpStatus.BAD_REQUEST, "AUTH4002", "인증 코드가 만료되었습니다. 다시 요청해주세요."),
    CODE_NOT_EQUAL(HttpStatus.BAD_REQUEST, "AUTH4003", "인증 코드가 올바르지 않습니다."),
    EMAIL_SEND_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH5001", "메일 발송 중 오류가 발생했습니다."),
    EMAIL_WRITE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "AUTH5002", "메일 작성 중 문제가 발생했습니다."),
    EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "AUTH4004", "이미 사용 중인 이메일입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "AUTH4005", "유효하지 않은 토큰입니다."),
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "AUTH4006", "토큰이 만료되었습니다."),
    TOKEN_LOGGED_OUT(HttpStatus.UNAUTHORIZED, "AUTH4007", "이 토큰은 로그아웃되어 더 이상 유효하지 않습니다."),
    UNSUPPORTED_PROVIDER(HttpStatus.BAD_REQUEST, "AUTH4008", "지원하지 않는 provider입니다."),

    // 멤버 관련 에러
    USER_NOT_FOUND(HttpStatus.BAD_REQUEST, "USER4001", "사용자가 없습니다."),
    NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER4002", "이미 사용 중인 닉네임입니다."),
    USERID_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "USER4003", "이미 사용 중인 아이디입니다."),

    // 명소 관련 에러
    PLACE_NOT_FOUND(HttpStatus.BAD_REQUEST, "PLACE4001", "존재하지 않는 명소입니다."),
    INVALID_PLACE_ANIMATION(HttpStatus.BAD_REQUEST, "PLACE4002", "해당 장소에 유효하지 않은 명소-애니메이션입니다."),

    // 명소 좋아요 관련 에러
    PLACE_LIKE_NOT_FOUND(HttpStatus.BAD_REQUEST, "PLACE4003", "저장되지 않은 명소입니다."),
    PLACE_LIKE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "PLACE4004", "이미 좋아요를 누른 명소입니다."),

    // 명소 후기 관련 에러
    PLACE_REVIEW_NOT_FOUND(HttpStatus.BAD_REQUEST, "PLACE4005", "존재하지 않는 명소 후기입니다."),
    MULTIPLE_WRITTEN_PLACE_REVIEWS_FOUND(HttpStatus.BAD_REQUEST, "PLACE4006", "고유한 명소 후기가 조회되지 않습니다."),

    // 이벤트 좋아요 관련 에러
    EVENT_LIKE_NOT_FOUND(HttpStatus.BAD_REQUEST, "EVENT4001", "저장되지 않은 이벤트입니다."),
    EVENT_LIKE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "EVENT4002", "이미 저장된 이벤트입니다."),

    // 이벤트 상세 정보 관련 에러
    EVENT_NOT_FOUND(HttpStatus.BAD_REQUEST, "EVENT4002", "존재하지 않는 이벤트입니다."),

    // 이벤트 후기 관련 에러
    EVENT_REVIEW_NOT_FOUND(HttpStatus.BAD_REQUEST, "EVENT4003", "존재하지 않는 이벤트 후기입니다."),
    MULTIPLE_WRITTEN_EVENT_REVIEWS_FOUND(HttpStatus.BAD_REQUEST, "EVENT4009", "고유한 이벤트 후기가 조회되지 않습니다."),

    // 이벤트 카테고리별 검색 관련 에러
    EVENT_CONDITION_NOT_FOUND(HttpStatus.BAD_REQUEST, "EVENT4004", "이벤트 검색 조건이 존재하지 않습니다."),
    EVENT_GENRE_NOT_FOUND(HttpStatus.BAD_REQUEST, "EVENT4005", "존재하지 않는 애니메이션 장르입니다."),
    EVENT_TYPE_NOT_FOUND(HttpStatus.BAD_REQUEST, "EVENT4006", "존재하지 않는 이벤트 종류입니다."),
    EVENT_STATUS_NOT_FOUND(HttpStatus.BAD_REQUEST, "EVENT4007", "존재하지 않는 이벤트 상태입니다."),

    // 이벤트 검색 관련 에러
    EVENT_SEARCH_NOT_FOUND(HttpStatus.NOT_FOUND, "EVENT4008", "검색된 이벤트가 없습니다."),

    // 후기 검색 관련 에러
    REVIEW_SEARCH_NOT_FOUND(HttpStatus.NOT_FOUND, "SEARCH4001", "검색된 후기가 없습니다."),

    // 애니메이션 관련 에러
    ANIMATION_NOT_FOUND(HttpStatus.NOT_FOUND, "ANIMATION4001", "존재하지 않는 애니메이션입니다"),
    PLACE_ANIMATION_NOT_FOUND(HttpStatus.NOT_FOUND, "ANIMATION4002", "존재하지 않는 애니메이션입니다"),
    ANIMATION_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "ANIMATION4003", "이미 존재하는 애니메이션입니다."),
    ANIMATION_NAME_IS_EMPTY(HttpStatus.BAD_REQUEST, "ANIMATION4004", "애니메이션 이름이 비어있습니다."),
    ANIMATION_NAME_LENGTH(HttpStatus.BAD_REQUEST, "ANIMATION4005", "애니메이션 이름은 2자 이상 50자 이하여야 합니다."),
    ANIMATION_NAME_SPECIAL_CHARACTER(HttpStatus.BAD_REQUEST, "ANIMATION4006", "애니메이션 이름은 한글, 영문, 숫자 및 일부 특수문자(./-)만 포함할 수 있습니다."),

    // 루트 관련 에러
    ROUTE_NOT_FOUND(HttpStatus.NOT_FOUND, "ROUTE4001", "존재하지 않은 루트입니다."),

    // 루트 애니메이션 관련 에러
    ROUTE_ANIMATION_NOT_FOUND(HttpStatus.NOT_FOUND, "ROUTE_ANIMATION4001", "관련된 애니메이션이 존재하지 않습니다."),

    // 루트 좋아요 관련 에러
    ROUTE_LIKE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "ROUTE4002", "이미 좋아요를 누른 루트입니다."),
    ROUTE_LIKE_NOT_FOUND(HttpStatus.BAD_REQUEST, "ROUTE4003", "저장되지 않은 루트입니다."),

    // 루트 아이템 관련 에러
    ROUTE_ITEM_NOT_FOUND(HttpStatus.NOT_FOUND, "ROUTE_ITEM4001", "존재하지 않는 루트 아이템입니다."),

    // 알림 관련 에러
    INVALID_NOTIFICATION_TYPE(HttpStatus.BAD_REQUEST, "NOTIFICATION4001", "유효하지 않은 알림 타입입니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.NOT_FOUND, "NOTIFICATION4002", "존재하지 않는 알림입니다."),
    NOTIFICATION_ALREADY_READ(HttpStatus.BAD_REQUEST, "NOTIFICATION4003", "이미 읽은 알림입니다."),
    NOTIFICATION_ACCESS_DENIED(HttpStatus.UNAUTHORIZED, "NOTIFICATION4004", "알림에 접근할 수 없습니다."),

    // 정렬 관련 에러
    INVALID_SORT_TYPE(HttpStatus.BAD_REQUEST, "SORT4001", "유효하지 않은 정렬 기준입니다."),

    // 여행 후기 관련 에러
    INVALID_REVIEW_TYPE(HttpStatus.BAD_REQUEST, "REVIEW4001", "유효하지 않은 후기 타입입니다."),
    INVALID_REVIEW_ID(HttpStatus.BAD_REQUEST, "REVIEW4002", "이벤트 후기와 장소 후기에 모두 존재하지 않는 후기 id 입니다."),
    REVIEW_NOT_FOUND(HttpStatus.BAD_REQUEST, "REVIEW4003", "존재하지 않는 후기입니다."),
    PAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "REVIEW4004", "페이지를 찾을 수 없습니다."),

    // 이미지 관련 에러
    INVALID_FOLDER(HttpStatus.BAD_REQUEST, "IMAGE4001", "유효하지 않은 폴더입니다."),
    IMAGE_NOT_FOUND(HttpStatus.NOT_FOUND, "IMAGE4002", "존재하지 않는 이미지입니다."),

    // 검색 관련 에러
    INVALID_SEARCH_KEYWORD(HttpStatus.BAD_REQUEST, "SEARCH4001", "유효하지 않은 검색어입니다."),

    // 한 줄 리뷰 관련 에러
    EVENT_SHORT_REVIEW_NOT_FOUND(HttpStatus.BAD_REQUEST, "EVENT4008", "존재하지 않는 이벤트 한 줄 리뷰입니다."),
    PLACE_SHORT_REVIEW_NOT_FOUND(HttpStatus.BAD_REQUEST, "PLACE4006", "존재하지 않는 이벤트 한 줄 리뷰입니다."),

    // 결제 관련 에러
    PAYMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PAYMENT4001", "결제 정보를 찾을 수 없습니다."),
    PAYMENT_STATUS_INVALID(HttpStatus.BAD_REQUEST, "PAYMENT4002", "결제 상태가 유효하지 않습니다."),
    PAYMENT_AMOUNT_MISMATCH(HttpStatus.BAD_REQUEST, "PAYMENT4003", "결제 금액이 일치하지 않습니다."),
    PAYMENT_DUPLICATE(HttpStatus.BAD_REQUEST, "PAYMENT4004", "이미 처리된 결제입니다."),

    // 내부 결제 관련 에러
    PURCHASE_FREE_CONTENT(HttpStatus.BAD_REQUEST, "PURCHASE4001", "무료이므로 결제가 불가능합니다."),
    PURCHASE_INSUFFICIENT_POINTS(HttpStatus.FORBIDDEN, "PURCHASE4002", "포인트가 모자라 결제가 불가능합니다."),
    PURCHASE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "PURCHASE4003", "이미 결제된 글입니다."),
    PURCHASE_SELF_CONTENT(HttpStatus.BAD_REQUEST, "PURCHASE4004", "본인의 글은 결제할 수 없습니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;

    @Override
    public ErrorReasonDTO getReason() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .build();
    }

    @Override
    public ErrorReasonDTO getReasonHttpStatus() {
        return ErrorReasonDTO.builder()
                .message(message)
                .code(code)
                .isSuccess(false)
                .httpStatus(httpStatus)
                .build()
                ;
    }
}
