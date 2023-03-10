package com.application.fusamate.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.application.fusamate.service.impl.EmployeeServiceImpl;
import com.application.fusamate.utils.TokenManager;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.ExpiredJwtException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final EmployeeServiceImpl userDetailsService;
    private final TokenManager tokenManager;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {


            
        String tokenHeader = request.getHeader("Authorization");
        String username = null;
        String token = null;
   System.out.println("tokenHeader");
   System.out.println(tokenHeader);
        // System.out.println(tokenHeader.startsWith("Bearer ")

        
        // );
        System.out.println(tokenHeader);
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            token = tokenHeader.substring(7, tokenHeader.length());

            try {
                username = tokenManager.getUsernameFromToken(token);
            } catch (MalformedJwtException e) {
                //
                System.out.println("MalformedJwtException");
                // response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
            } catch (ExpiredJwtException e) {
                System.out.println("JWT Token has expired");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                RequestDispatcher rd = request.getRequestDispatcher("/error");
                rd.forward(request, response);
            }
        } else {
            System.out.println(request.getRequestURI());

            
     if (!request.getRequestURI().equals("/api/login") &&
                    !request.getRequestURI().equals("/api/change-password")
                    &&
                    !request.getRequestURI().equals("/api/reset-password")) {

                        System.out.println("bi gọienennann");
                        System.out.println(request.getRequestURI().contains("/public"));
                        if (!request.getRequestURI().contains("/public")) {
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            RequestDispatcher rd = request.getRequestDispatcher("/error");
                            rd.forward(request, response);  
                        }
             
            }

        }
        if (null != username && SecurityContextHolder.getContext().getAuthentication() == null) {

            System.out.println("SecurityContextHolder");
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (tokenManager.validateJwtToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null,
                        userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
       
        filterChain.doFilter(request, response);
    }
}
