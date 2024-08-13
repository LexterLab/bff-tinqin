package com.tinqinacademy.bff.core.jwt;

import com.tinqinacademy.authentication.api.operations.generateaccesstoken.GetUsernameFromTokenInput;
import com.tinqinacademy.authentication.api.operations.loaduserdetails.LoadUserDetailsInput;
import com.tinqinacademy.authentication.api.operations.validateacesstoken.ValidateAccessTokenInput;
import com.tinqinacademy.authentication.api.operations.validateacesstoken.ValidateAccessTokenOutput;
import com.tinqinacademy.authentication.restexport.AuthenticationClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationClient authenticationClient;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = getTokenFromRequest(request);

        ValidateAccessTokenInput input = ValidateAccessTokenInput
                .builder()
                .accessToken(token)
                .build();

        if (StringUtils.hasText(token) && authenticationClient.validateToken(input).getSuccess()) {


            GetUsernameFromTokenInput tokenInput = GetUsernameFromTokenInput.builder()
                    .token(token)
                    .build();

            String username = authenticationClient.getUsernameFromToken(tokenInput).getUsername();
            LoadUserDetailsInput userDetailsInput = LoadUserDetailsInput
                    .builder()
                    .username(username)
                    .build();
            UserDetails userDetails = authenticationClient.loadUserDetails(userDetailsInput).getUserDetails();

            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());

            usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        }

        filterChain.doFilter(request, response);
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }

        return null;
    }
}
