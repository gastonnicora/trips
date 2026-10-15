package com.gastonnicora.trips.security;

import com.gastonnicora.trips.entities.RefreshToken;
import com.gastonnicora.trips.repositories.RefreshTokenRepository;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private UserDetailsServiceImpl userDetailsService;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private FilterChain filterChain;

    private JwtAuthenticationFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        filter = new JwtAuthenticationFilter(jwtService, userDetailsService, refreshTokenRepository);
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldAuthenticateUserWhenJwtAndRefreshTokenAreValid() throws Exception {
        request.addHeader("Authorization", "Bearer token");
        when(jwtService.isValid("token")).thenReturn(true);
        when(jwtService.extractUsername("token")).thenReturn("user@test.com");
        when(jwtService.extractVersion("token")).thenReturn(2);

        RefreshToken refreshToken = mock(RefreshToken.class);
        when(refreshTokenRepository.findByToken("token")).thenReturn(Optional.of(refreshToken));
        when(refreshToken.isActive()).thenReturn(true);
        when(refreshToken.getVersion()).thenReturn(2);

        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername("user@test.com")).thenReturn(userDetails);

        filter.doFilter(request, response, filterChain);

        assertTrue(SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
        assertSame(userDetails, SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenAuthorizationHeaderIsMissing() throws Exception {
        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService, refreshTokenRepository);
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenAuthorizationHeaderIsNotBearer() throws Exception {
        request.addHeader("Authorization", "Basic abc");

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService, refreshTokenRepository);
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenJwtIsInvalid() throws Exception {
        request.addHeader("Authorization", "Bearer invalid-token");
        when(jwtService.isValid("invalid-token")).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenJwtParsingThrows() throws Exception {
        request.addHeader("Authorization", "Bearer token");
        when(jwtService.isValid("token")).thenThrow(new io.jsonwebtoken.MalformedJwtException("bad jwt"));

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenRefreshTokenDoesNotExist() throws Exception {
        request.addHeader("Authorization", "Bearer token");
        when(jwtService.isValid("token")).thenReturn(true);
        when(jwtService.extractUsername("token")).thenReturn("user@test.com");
        when(jwtService.extractVersion("token")).thenReturn(2);
        when(refreshTokenRepository.findByToken("token")).thenReturn(Optional.empty());

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenRefreshTokenIsInactive() throws Exception {
        request.addHeader("Authorization", "Bearer token");
        when(jwtService.isValid("token")).thenReturn(true);
        when(jwtService.extractUsername("token")).thenReturn("user@test.com");
        when(jwtService.extractVersion("token")).thenReturn(2);

        RefreshToken refreshToken = mock(RefreshToken.class);
        when(refreshTokenRepository.findByToken("token")).thenReturn(Optional.of(refreshToken));
        when(refreshToken.isActive()).thenReturn(false);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void shouldContinueWithoutAuthenticationWhenTokenVersionDoesNotMatch() throws Exception {
        request.addHeader("Authorization", "Bearer token");
        when(jwtService.isValid("token")).thenReturn(true);
        when(jwtService.extractUsername("token")).thenReturn("user@test.com");
        when(jwtService.extractVersion("token")).thenReturn(2);

        RefreshToken refreshToken = mock(RefreshToken.class);
        when(refreshTokenRepository.findByToken("token")).thenReturn(Optional.of(refreshToken));
        when(refreshToken.isActive()).thenReturn(true);
        when(refreshToken.getVersion()).thenReturn(1);

        filter.doFilter(request, response, filterChain);

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verify(filterChain).doFilter(request, response);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }
}
