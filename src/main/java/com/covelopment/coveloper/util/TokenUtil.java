// TokenUtil.java
package com.covelopment.coveloper.util;

import com.covelopment.coveloper.security.JwtTokenProvider;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class TokenUtil {

    private final JwtTokenProvider jwtTokenProvider;

    public TokenUtil(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        return jwtTokenProvider.resolveToken(bearerToken);
    }

    public boolean validateToken(String token) {
        return jwtTokenProvider.validateToken(token);
    }

    public String getEmailFromToken(String token) {
        return jwtTokenProvider.getEmail(token);
    }

}
