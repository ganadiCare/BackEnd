package smCapstone.homecam.domain.report.exception;

import smCapstone.homecam.global.apipayload.BaseErrorCode;
import smCapstone.homecam.global.exception.GeneralException;

public class ReportException extends GeneralException {
    public ReportException(BaseErrorCode errorCode) {
        super(errorCode);
    }
}
