package com.IBTim4.CertificatesApp.auth;

import com.IBTim4.CertificatesApp.certificate.controller.CertificateController;
import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService authService;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if (request.getRequestURL().toString().contains("/api/")) {
            System.out.println("####" + request.getMethod() + ":" + request.getRequestURL());
            System.out.println("#### Authorization: " + request.getHeader("Authorization"));

            logger.info("Getting " + request.getMethod() + " request on: " + request.getRequestURL());
            logger.info("Authorization header: " + request.getHeader("Authorization"));

            String requestTokenHeader = request.getHeader("Authorization");
            String username = null;
            String jwtToken = null;

            if (requestTokenHeader != null && requestTokenHeader.contains("Bearer")) {
                jwtToken = requestTokenHeader.substring(requestTokenHeader.indexOf("Bearer ") + 7).replaceAll("\"", "");
                System.out.println(">>>>>JWT TOKEN: " + jwtToken);

                try {
                    username = jwtTokenUtil.getUsernameFromToken(jwtToken);
                    UserDetails userDetails = this.authService.loadUserByUsername(username);

                    if (jwtTokenUtil.validateToken(jwtToken, userDetails)) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        System.out.println("Username: " + userDetails.getUsername() + ", role: " + userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                    }
                } catch (IllegalArgumentException e) {
                    logger.error("Unable to get JWT Token.", e);
                    System.out.println("Unable to get JWT Token.");
                } catch (ExpiredJwtException e) {
                    logger.error("JWT Token has expired.", e);
                    System.out.println("JWT Token has expired.");
                } catch (io.jsonwebtoken.MalformedJwtException e) {
                    logger.error("Bad JWT Token.", e);
                    System.out.println("Bad JWT Token.");
                }
            } else {
                logger.warn("JWT Token does not exist.");
            }
        }
        filterChain.doFilter(request, response);

    }

}
