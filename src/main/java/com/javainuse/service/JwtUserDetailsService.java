package com.javainuse.service;

import java.util.ArrayList;

import org.owasp.encoder.Encode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService  implements UserDetailsService{

	@Value("{$jwt.secret}")
	private String secret;
	
	@Value("${jwt.user}")
	private String user;
	
	@Value("${jwt.password}")
	private String password;
	
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		String cleanUser = Encode.forHtml(Encode.forCssString(username));
		if(secret.equals(cleanUser)) {
			return new User(user,password,
				new ArrayList<>());
		} else {
			throw new UsernameNotFoundException("User not found with " + cleanUser);
		}
		
	}
}
