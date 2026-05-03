package com.gastonnicora.trips.utils;

import java.util.UUID;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.gastonnicora.trips.security.UserDetailsImpl;

public class SecurityUtils {
    public static UUID getCurrentUserUuid() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
        if (auth != null && user != null) {
            return user.getUuid();
        }
        return null;
    }
    public static String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder
                .getContext()
                .getAuthentication();
        return auth.getName();
    }

}
