package smCapstone.homecam.domain.pet.exception;

import smCapstone.homecam.global.apipayload.BaseErrorCode;
import smCapstone.homecam.global.exception.GeneralException;

public class PetException extends GeneralException {
    public PetException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
