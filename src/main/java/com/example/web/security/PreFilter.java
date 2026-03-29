package com.example.web.security;

import java.io.IOException;

import org.eclipse.angus.mail.handlers.handler_base;
import org.hibernate.grammars.hql.HqlParser.SecondContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.support.StringMultipartFileEditor;

import com.example.web.service.inter.CustomUserDetailService;
import com.example.web.service.inter.JwtService;
import com.example.web.service.inter.UserService;
import com.example.web.util.TokenEnum;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class PreFilter extends OncePerRequestFilter{

    private final CustomUserDetailService customUserDetailService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
                log.info("---------------------Pre filter-----------------------");

                final String authorization = request.getHeader("Authorization");
                log.info("Authorization: {}", authorization);

                if(authorization == null || StringUtils.isBlank(authorization) || !authorization.startsWith("Bearer ")) {
                    filterChain.doFilter(request, response);
                    return;
                }
                
                final String token = authorization.substring("Bearer ".length());
                log.info("Token: {}", token);

                final String userName = jwtService.extractUsername(token, TokenEnum.ACCESS_TOKEN);

                if(StringUtils.isNotEmpty(userName) && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = customUserDetailService.userDetailsService().loadUserByUsername(userName);
                    if (jwtService.isValid(token,TokenEnum.ACCESS_TOKEN, userDetails)) {
                        SecurityContext context = SecurityContextHolder.createEmptyContext();
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        context.setAuthentication(authenticationToken);
                        SecurityContextHolder.setContext(context);
                    }
                }

                log.info("User: {}", userName);

                filterChain.doFilter(request, response);
    }
}
