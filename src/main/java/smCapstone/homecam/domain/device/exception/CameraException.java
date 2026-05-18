package smCapstone.homecam.domain.device.exception;

import smCapstone.homecam.global.apipayload.BaseErrorCode;
import smCapstone.homecam.global.exception.GeneralException;

public class CameraException extends GeneralException {
    public CameraException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
