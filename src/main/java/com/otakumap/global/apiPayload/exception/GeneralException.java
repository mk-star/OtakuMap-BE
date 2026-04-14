package com.otakumap.global.apiPayload.exception;

import com.otakumap.global.apiPayload.code.BaseErrorCode;
import com.otakumap.global.apiPayload.code.ErrorReasonDTO;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException {

    private final BaseErrorCode code;

    public GeneralException(BaseErrorCode errorCode) {
        super(errorCode.getReason().getMessage());
        this.code = errorCode;
    }

    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }

    public ErrorReasonDTO getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }
}
