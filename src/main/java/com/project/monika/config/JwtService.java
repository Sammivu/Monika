package com.project.monika.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
// This secret is what we used to confirm if the token that was genereted  is the same key that is used to confirm
public class JwtService {
    private static String SECRET_KEY;
    @Value("${jwt.secret.key}")
    public void setSecretKey(String secretKey){
        SECRET_KEY = secretKey;
    }

    //Extract all claims
    //This code helps us to compare the user token
    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .setSigningKey(getSignInkey())
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }

    // this is used to decode our token
    private Key getSignInkey() {
        byte[] keyBytes= Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    //extracting a single claims
    public <T> T extractClaims (String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    //Extracting username into the claims
    public String extractUsername (String token){
        return extractClaims(token, Claims::getSubject);
    }

    //Method to generate token and set a time for expiration
    public String generateToken(Map<String, Object> extractClaims, UserDetails userDetails){

        return Jwts
                .builder()
                .setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInkey(), SignatureAlgorithm.HS256)
                .compact();
    }

    //Generates a token for every user
    public  String generateToken (UserDetails userDetails){
        return (generateToken(new HashMap<>(), userDetails));

    }

    //Checking if token is valid
    public  Boolean isTokenValid(String token, UserDetails userDetails){
        final String username = extractUsername(token);

        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private  Date extractExpiration(String token){
        return extractClaims(token, Claims::getExpiration);
    }

}