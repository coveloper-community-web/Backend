package com.covelopment.coveloper.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;  // JWT 서명에 사용할 비밀 키

    private SecretKey key;  // SecretKey 객체

    private final UserDetailsService userDetailsService;  // 사용자 세부 정보를 가져오기 위한 서비스

    public JwtTokenProvider(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    public void init() {
        // Base64로 인코딩된 비밀 키를 디코딩하여 SecretKey 객체 생성
        key = Keys.hmacShaKeyFor(Base64.getDecoder().decode(SECRET_KEY));
    }

    public String createToken(String email) {
        // JWT 토큰 생성
        Claims claims = Jwts.claims().setSubject(email);  // 사용자 이메일을 클레임으로 설정
        Date now = new Date();  // 현재 시간
        Date expiryDate = new Date(now.getTime() + 1000 * 60 * 60); // 1시간 후 만료 설정

        return Jwts.builder()
                .setClaims(claims)  // 클레임 설정
                .setIssuedAt(now)  // 발급 시간 설정
                .setExpiration(expiryDate)  // 만료 시간 설정
                .signWith(key, SignatureAlgorithm.HS256)  // HMAC SHA-256 알고리즘으로 서명
                .compact();  // JWT 문자열 반환
    }
    public boolean validateToken(String token) {
        // JWT 토큰 유효성 검사
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)  // 서명 검증을 위한 키 설정
                    .build()
                    .parseClaimsJws(token);  // 토큰 파싱
            return true;  // 유효한 경우 true 반환
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            System.err.println("Token is expired: " + e.getMessage());
        } catch (io.jsonwebtoken.UnsupportedJwtException e) {
            System.err.println("Token is unsupported: " + e.getMessage());
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            System.err.println("Token is malformed: " + e.getMessage());
        } catch (io.jsonwebtoken.SignatureException e) {
            System.err.println("Signature validation failed: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Token validation failed: " + e.getMessage());
        }
        return false;  // 유효하지 않은 경우 false 반환
    }


    public String getEmail(String token) {
        // JWT 토큰에서 이메일 추출
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)  // 서명 검증을 위한 키 설정
                .build()
                .parseClaimsJws(token)
                .getBody();  // 클레임 추출
        return claims.getSubject();  // 클레임에서 이메일 반환
    }

    public String resolveToken(HttpServletRequest request) {
        // Authorization 헤더에서 Bearer 토큰 추출
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            // "Bearer " 제거 후 공백 제거
            return bearerToken.substring(7).trim();  // 트림하여 공백 제거
        }
        return null;  // 토큰이 없는 경우 null 반환
    }


    public Authentication getAuthentication(String token) {
        // JWT 토큰으로부터 Authentication 객체 생성
        String email = getEmail(token);  // 이메일 추출
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);  // 사용자 세부 정보 로드
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());  // Authentication 객체 반환
    }
}
