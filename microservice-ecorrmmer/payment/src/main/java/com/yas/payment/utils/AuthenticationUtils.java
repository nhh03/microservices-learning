package com.yas.payment.utils;

import com.yas.payment.exception.SignInRequiredException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

public class AuthenticationUtils {
	private AuthenticationUtils() {
		// Private constructor to prevent instantiation
	}

	public static String getCurrentUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
			throw new SignInRequiredException("SIGN_IN_REQUIRED");
		}
		JwtAuthenticationToken contextHolder = (JwtAuthenticationToken) authentication;
		return contextHolder.getToken().getSubject();
	}

	public static String getToken() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
			throw new SignInRequiredException("SIGN_IN_REQUIRED");
		}

		JwtAuthenticationToken contextHolder = (JwtAuthenticationToken) authentication;
		return contextHolder.getToken().getTokenValue();
	}


}
