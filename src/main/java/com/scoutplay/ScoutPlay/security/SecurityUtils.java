package com.scoutplay.ScoutPlay.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static String currentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            return null;
        }
        return authentication.getPrincipal().toString();
    }

    public static boolean hasUserType(String userType) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        for (GrantedAuthority authority : authentication.getAuthorities()) {
            if (authority.getAuthority().equals("ROLE_" + userType)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isOwner(String resourceOwnerId) {
        return resourceOwnerId != null && resourceOwnerId.equals(currentUserId());
    }
}