package com.nhh203.userservice.security.jwt;

import com.nhh203.userservice.security.userprinciple.UserDetailService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Component
@Slf4j
public class JwtTokenFilter extends OncePerRequestFilter {
    UserDetailService userDetailService;
    JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = getJwt(request);

            if (token != null && jwtProvider.validateToken(token)) {
                String username = jwtProvider.getUserNameFromToken(token);
                UserDetails userDetails = userDetailService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities()
                );

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                String refreshToken = jwtProvider.createRefreshToken(authenticationToken);

                response.setHeader("Authorization", "Bearer " + token);
                response.setHeader("Refresh-Token", refreshToken);
            }


        } catch (Exception e) {
            log.error("Can't set user authentication -> Message: ", e);
        }

        // Ensure the filter chain continues
        filterChain.doFilter(request, response);
    }


    private String getJwt(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer")) {
            return authHeader.replace("Bearer", "");
        }
        return null;
    }

}
