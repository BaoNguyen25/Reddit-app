package com.example.Reddit.security;
import com.example.Reddit.exception.SpringRedditException;
import com.example.Reddit.model.CustomUserDetails;
import io.jsonwebtoken.*;;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;


import static io.jsonwebtoken.Jwts.parser;

@Service
public class JwtProvider {

    SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    String secretString = Encoders.BASE64.encode(key.getEncoded());

    public String generateToken(Authentication authentication) {
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        return Jwts.builder()
                .setSubject(principal.getUsername())
                .signWith(SignatureAlgorithm.HS256, secretString)
                .compact();
    }

    public boolean validateToken(String jwt) {
        try {
            parser().setSigningKey(secretString).parseClaimsJws(jwt);
            return true;
        } catch (MalformedJwtException | ExpiredJwtException | UnsupportedJwtException | IllegalArgumentException e) {
            throw new SpringRedditException("Exception occurred while validate token");
        }
    }

    public String getUsernameFromJWT(String token) {
        Claims claims = parser()
                .setSigningKey(secretString)
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }
}