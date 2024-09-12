package com.project.api.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;
import java.util.UUID;

public class CurrentAuthContext {
    private static Map<String, Object> extractClaim() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();
        Map<String, Object> claims = ((Jwt) principal).getClaims();
        return claims;
    }

    public static String getUserEmail() {
        return (String) extractClaim().get("email");
    }

    public static UUID getUserId() {
        return UUID.fromString((String) extractClaim().get("sub"));
    }

    public static String getName() {
        return (String) extractClaim().get("given_name");
    }


}
