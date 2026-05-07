package smCapstone.homecam.domain.member.exception;

import smCapstone.homecam.global.exception.GeneralException;

public class MemberException extends GeneralException {
    public MemberException(MemberErrorCode errorCode) {
        super(errorCode);
    }
}
