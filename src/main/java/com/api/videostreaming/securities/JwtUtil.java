package com.api.videostreaming.securities;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.api.videostreaming.utilities.Constants;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;



@Component
public class JwtUtil {

    private final long jwtExpirationTimeInSec;
    private final SecretKey key;

    public JwtUtil(@Value("${jwtExpirationTimeInSec}") long jwtExpirationTimeInSec, @Value("${jwtSecretKey}") String jwtSecretKey) {
        this.jwtExpirationTimeInSec = jwtExpirationTimeInSec;
        this.key = getSecretKey(jwtSecretKey);
    }

    // generating seceret key
    private SecretKey getSecretKey(String jwtSecretKey) {
        try {
            // Obtain the bytes from the key
            byte[] keyBytes = jwtSecretKey.getBytes(StandardCharsets.UTF_8);
            // Use a cryptographic hash function to get a fixed size key
            MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
            byte[] hashedKey = sha512.digest(keyBytes);
            // Return the key sized for HMAC-SHA512
            return new SecretKeySpec(hashedKey, "HmacSHA512");
        } catch (Exception e) {
            throw new RuntimeException("Error while hashing key", e);
        }
    }

    // retrieve username from jwt token
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    // retrieve expiration date from jwt token
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    // for retrieveing any information from token we will need the secret key
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // check if the token has expired
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    // generate auth token for user
    public String generateToken(String userName, String tokenType) {
        long expirationTimeInSec = jwtExpirationTimeInSec;
        if (tokenType.equals(Constants.TYPE_REFRESH_TOKEN)) {
            // refresh token time will be double
            expirationTimeInSec = 2 * expirationTimeInSec;
        }
        Map<String, Object> claims = new HashMap<>();
        claims.put(Constants.TOKEN_TYPE, tokenType);
        return doGenerateToken(claims, userName, expirationTimeInSec);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject, long customJwtExpirationTime) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + customJwtExpirationTime * 1000))
                .signWith(key, SignatureAlgorithm.HS512)
                .compact();
    }

    // validate token
    public Boolean validateToken(String token, String userName) {
        final String tokenUsername = getUsernameFromToken(token);
        return (tokenUsername.equals(userName) && !isTokenExpired(token));
    }

    // retrieve a specific custom claim from the token
    public String getCustomClaimFromToken(String token, String claimKey) {
        final Claims claims = getAllClaimsFromToken(token);
        return claims.get(claimKey).toString();
    }

    // retrieve all custom claims from the token
    public Map<String, Object> getAllCustomClaimsFromToken(String token) {
        final Claims claims = getAllClaimsFromToken(token);
        return new HashMap<>(claims);
    }
}
