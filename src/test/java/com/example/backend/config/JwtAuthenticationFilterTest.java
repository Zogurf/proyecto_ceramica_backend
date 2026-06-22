package com.example.backend.config;

import com.example.backend.services.JwtService;
import com.example.backend.services.UserDetailsServiceImpl;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class JwtAuthenticationFilterTest {

    @Test
    void continuesFilterChainWhenTokenIsExpired() throws Exception {
        JwtService jwtService = mock(JwtService.class);
        UserDetailsServiceImpl userDetailsService = mock(UserDetailsServiceImpl.class);
        FilterChain filterChain = mock(FilterChain.class);
        JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/products");
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.addHeader("Authorization", "Bearer expired-token");
        when(jwtService.extractUsername(anyString()))
                .thenThrow(mock(ExpiredJwtException.class));

        filter.doFilter(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
    }
}
