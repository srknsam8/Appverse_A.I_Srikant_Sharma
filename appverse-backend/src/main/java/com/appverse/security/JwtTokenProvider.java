package com.appverse.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component // Tells Spring Boot to keep this machine running in the background
public class JwtTokenProvider {

    // 1. Generate a massive, mathematically unbreakable secret key for your app
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS512);

    // 2. Set the ID Card to expire in 7 days (Time in milliseconds)
    private final long jwtExpirationInMs = 604800000;

    //MACHINE FUNCTION 1: Print the ID Card
    public String generateToken(String email) {
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .setSubject(email) // We tie the ID card strictly to their email
                .setIssuedAt(new Date())
                .setExpiration(expireDate)
                .signWith(key) // We stamp it with our secret key so it can't be forged
                .compact();
    }

    //MACHINE FUNCTION 2: Read the ID Card 
    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
                
        return claims.getSubject(); // Hands back the email printed on the card
    }

    //MACHINE FUNCTION 3: Verify the ID Card is Real
    public boolean validateToken(String token) {
        try {
            // If the parser successfully reads it without crashing, it is a valid token!
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            // If it is expired, forged, or broken, we reject it
            return false;
        }
    }
}