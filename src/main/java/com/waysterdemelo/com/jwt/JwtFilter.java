package com.waysterdemelo.com.jwt;

import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomerUserDetailsService  customerUserDetailsService;

    Claims claims = null;
    private String username = null;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

       if (request.getServletPath().matches("/user/login|/user/forgotpassword|/user/signup")){
            filterChain.doFilter(request, response);
        }else {
            String authorization = request.getHeader("Authorization");
            String token = null;

            if (authorization != null && authorization.startsWith("Bearer ")){
                token = token.substring(7);
                username = jwtUtil.extractUserName(token);
                claims = jwtUtil.extractAllClaims(token);
            }


       }
    }
}
