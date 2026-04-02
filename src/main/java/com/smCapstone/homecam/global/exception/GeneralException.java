package com.smCapstone.homecam.global.exception;

import com.smCapstone.homecam.global.apipayload.BaseErrorCode;
import lombok.Getter;

@Getter
public class GeneralException extends RuntimeException{

    private final BaseErrorCode code;

    public GeneralException(BaseErrorCode code) {
        this.code = code;
    }
}
