package smCapstone.homecam.global.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import smCapstone.homecam.global.apipayload.GeneralErrorCode;
import smCapstone.homecam.global.exception.GeneralException;

public class SecurityUtil {

    public static Long getCurrentMemberId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
        }

        Object principal = authentication.getPrincipal();

        if (!(principal instanceof Long)) {
            throw new GeneralException(GeneralErrorCode.UNAUTHORIZED);
        }

        return (Long) principal;
    }
}
