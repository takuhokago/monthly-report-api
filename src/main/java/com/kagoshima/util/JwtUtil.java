package com.kagoshima.util;

import java.security.Key;
import java.util.Date;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import com.kagoshima.entity.Employee;

@Component
public class JwtUtil {

	private static final String SECRET_KEY = "uLCw4U4l9FCXYn3TS3kI1pNLnZxcy3vC9PQjdfvGyYw="; // 32文字以上

	private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 8; // 一旦8時間

	private Key getSigningKey() {
	    byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
	    return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(Employee employee) {
		return Jwts.builder().setSubject(employee.getCode()) // 社員番号を subject に
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
				.signWith(getSigningKey(), SignatureAlgorithm.HS256).compact();
	}

	public String extractUsername(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody().getSubject();
	}

	public boolean isTokenValid(String token, String username) {
		final String extractedUsername = extractUsername(token);
		return (extractedUsername.equals(username)) && !isTokenExpired(token);
	}

	private boolean isTokenExpired(String token) {
		return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody()
				.getExpiration().before(new Date());
	}

	public boolean validateToken(String token, UserDetails userDetails) {
		final String username = extractUsername(token);
		return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
	}

}
