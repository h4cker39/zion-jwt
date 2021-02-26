package com.javainuse.config;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.javainuse.service.JwtUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;

/**
 * This class will check if the type comming is
 * indeed a valid JWTToken not authenticate just
 * validate its type.
 * @author Luis Giordano
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter{

	@Autowired 
	private JwtUserDetailsService jwtUserDetailsService;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
	final String requestTokenHeader = 
			request.getHeader("authorization");
	
	String username = null;
	String jwtToken  =  null;
	
	// JWT Token is in the form "Bearer token". Remove Bearer word and get
	// only the Token
	// From a security standpoint this is not a proper validation
	// P.S. from Luis Giordano AppSec Guy.
	
	if(requestTokenHeader != null && requestTokenHeader.startsWith("Bearer")) {
		jwtToken = requestTokenHeader.substring(7);
		
		try {
			username = jwtTokenUtil.getUsernameFromToken(jwtToken);
		}catch(IllegalArgumentException e) {
			System.out.println("Illegal token or unable to catch token");
		}catch (ExpiredJwtException e) {
			System.out.println("Expired JWT Token");
		} 
	}	else {
		logger.warn("JWT DOES NOT BEGIN WITH BEARER STRING");
	}
	// Once we get the token validate it.
	if(username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
		UserDetails userDetails =
				this.jwtUserDetailsService.loadUserByUsername(username);
		
		//Verify is tokenn  is valid then SPring security to set authentication
		if(jwtTokenUtil.validateToken(jwtToken, userDetails)) {
			UsernamePasswordAuthenticationToken
				usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
						userDetails,null,userDetails.getAuthorities());		
			usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
			// After setting the Authentication in the context, we specify
			// that the current user is authenticated. So it passes the
			// Spring Security Configurations successfully.
			SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);		
		} 
	}
	filterChain.doFilter(request,response);	
  }
}
