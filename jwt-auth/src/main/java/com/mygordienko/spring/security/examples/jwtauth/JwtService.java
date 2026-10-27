package com.mygordienko.spring.security.examples.jwtauth;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class JwtService {
    private static final String SECRET_KEY = "2485q0355q9548q495";
    private static final Long EXPIRATION_TIME = 360_000L;
    private static final String ISSUER = "com.mygordienko.spring.security.examples";

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(SECRET_KEY);
    }

    public String generateToken(String username) {
        var now = Instant.now();
        return JWT.create()
                .withIssuer(ISSUER)
                .withSubject(username)
                .withIssuedAt(now)
                .withExpiresAt(now.plusMillis(EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SECRET_KEY));
    }
    public String extractUsername(String token) {
        DecodedJWT decodedJWT = decodeJwt(token);
        return decodedJWT != null ? decodedJWT.getSubject() : null;
    }

    private DecodedJWT decodeJwt(String token) {
        try {
            JWTVerifier verifier = JWT.require(getAlgorithm())
                    .withIssuer(ISSUER)
                    .build();
            // NOTE: this also checks expiration date
            return verifier.verify(token);
        } catch (JWTVerificationException e) {
            return null;
        }
    }
}
