package com.otunba.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.*;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class JwtTokenServiceTest {
    @Mock
    private JwtEncoder jwtEncoder;
    @Mock
    private JwtDecoder jwtDecoder;
    @InjectMocks
    private JwtTokenService jwtTokenService;
    @Mock
    private Authentication authentication;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    public void test_that_token_expires_at_exact_time_and_correct_user_details_is_present(){
        String username = "testUser";
        when(authentication.getName()).thenReturn(username);
        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(60 * 60 * 24);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(username)
                .build();
        Jwt jwt = Jwt.withTokenValue("tokenValue")
                .issuer("self")
                .issuedAt(now)
                .expiresAt(expiresAt)
                .subject(username)
                .header("alg", "HS256")
                .build();
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);
        var token = jwtTokenService.generateToken(authentication);
        assertNotNull(token);
        assertEquals("tokenValue", token);
        verify(authentication, times(1)).getName();
        verify(jwtEncoder, times(1)).encode(any(JwtEncoderParameters.class));

    }

}