package com.revelvol.JWT.service;

import com.revelvol.JWT.model.User;
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
import java.util.Set;
import java.util.function.Function;

@Service
public class JwtService {
    // use hex as best practice and easier implementation

    // use secret key from environment variable
    @Value("${jwt.secret}")
    private String SECRET_KEY;


    public String extractUsername(String jwt) {
        return extractClaims(jwt, Claims::getSubject);
        //look at that :: get subject, jadi ini shorthand methjod to overide apply di claim resolver dibawah
    }


    // a generate token method with extra claims
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails // take user detail from spring framework
    ) {

        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // generate token without extra claims
    public String generateToken(
            UserDetails userDetails
    ) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // generate Expired Token for testing
    public String generateExpiredToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        long currentTimeMillis = System.currentTimeMillis();
        long issuedAtMillis = currentTimeMillis - (60 * 1000); // 60 seconds ago
        long expirationMillis = currentTimeMillis - (30 * 1000); // 30 seconds ago

        Date issuedAt = new Date(issuedAtMillis);
        Date expiration = new Date(expirationMillis);

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(issuedAt)
                .setExpiration(expiration)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateExpiredToken(UserDetails userDetails) {
        return generateExpiredToken(new HashMap<>(), userDetails);
    }

    // method to validate token
    public boolean isTokenValid(String jwt, User userDetails) {
        final String username = extractUsername(jwt);
        return (username.equals(userDetails.getEmail()) && isTokenExpired(jwt));

    }

    private boolean isTokenExpired(String jwt) {
        return extractExpiration(jwt).after(new Date(System.currentTimeMillis()));
    }

    private Date extractExpiration(String jwt) {
        return extractClaims(jwt, Claims::getExpiration);
    }

    // extract single claims every pass
    // in case wondering <> is a generic type, jadi bisa work with all data type
    // function also the same , jadi ini take claims and return T
    public <T> T extractClaims(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        //kayaknya ini just some kind of java thing buat execute function
        return claimsResolver.apply(claims);
    }

    // extract all claims from the token
    private Claims extractAllClaims(String token) {

        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    //get the signin key
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        //hjmacshakey are the algorithm to implement the jsot
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // get the additional signed roles
    public Set<String> extractUserRoles(String token) {

        return extractClaims(token, claims -> {
            //get the "roles" claims from the JWT
            return claims.get("roles", Set.class);
        });
    }

    // get the additional signed userId

    public Integer extractUserId(String token) {
        return extractClaims(token, claims -> {
            // get "userId" claims from the JWT
            return claims.get("userId", Integer.class);
        });
    }
}
