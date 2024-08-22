package com.covelopment.coveloper.sercurity;

import com.covelopment.coveloper.security.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class JwtTokenProviderTest {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    public void testCreateToken() {
        String email = "test@example.com";
        String token = jwtTokenProvider.createToken(email);
        assertNotNull(token);
        assertTrue(jwtTokenProvider.validateToken(token));
        assertEquals(email, jwtTokenProvider.getEmail(token));
    }

    @Test
    public void testResolveToken() {
        String email = "test@example.com";
        String token = jwtTokenProvider.createToken(email);
        HttpServletRequest request = new MockHttpServletRequest();
        ((MockHttpServletRequest) request).addHeader("Authorization", "Bearer " + token);

        String resolvedToken = jwtTokenProvider.resolveToken(request);
        assertEquals(token, resolvedToken);
    }
}
