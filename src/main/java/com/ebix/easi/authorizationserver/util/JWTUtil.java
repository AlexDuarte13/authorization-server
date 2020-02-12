package com.ebix.easi.authorizationserver.util;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JWTUtil {
	
	private static String SECRET_KEY;
	
	public static String createJWT(Map<String, Object> payload, Date expireAt) {
		
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
	    
	    byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
	    Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
	    
	    JwtBuilder builder = Jwts.builder()
	            .setExpiration(expireAt)
	            .addClaims(payload)
	            .setHeader(getDefaultHeader())
	            .signWith(signatureAlgorithm, signingKey);
		
		return builder.compact();
	}
	
	public static Claims decodeJWT(String jwt) {
		return Jwts.parser()
				.setSigningKey(DatatypeConverter.parseBase64Binary(SECRET_KEY))
				.parseClaimsJws(jwt)
				.getBody();
	}
	
	public static Map<String, Object> getDefaultHeader() {
		Map<String, Object> header = new HashMap<>();
		header.put("alg", "HS512");
		header.put("typ", "JWT");
		return header;
	}
	
	@Value("${jwt.secretkey}")
	private void setSecretKey(String secretKey) {
		JWTUtil.SECRET_KEY = secretKey;
	}

}
