package smCapstone.homecam.domain.device.exception;

import smCapstone.homecam.global.apipayload.BaseErrorCode;
import smCapstone.homecam.global.exception.GeneralException;

public class DispenserException extends GeneralException {
    public DispenserException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
