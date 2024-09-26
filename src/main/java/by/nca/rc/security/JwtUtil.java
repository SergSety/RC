package by.nca.rc.security;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String jwtSecret;
    @Value("${jwt.expirationMs}")
    private Long jwtExpirationMs;

    public String generateToken(CustomerDetails customerDetails) {

        return generateTokenFromUsername(customerDetails.getUsername());
    }

    public String generateTokenFromUsername(String username) {

        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .setIssuer("Library")
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // My version
    public boolean validateToken(String authToken) {

        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);

        } catch (SignatureException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new SignatureException("Invalid JWT signature: " + e.getMessage());

        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
            throw new MalformedJwtException("Invalid JWT token: " + e.getMessage());

        } catch (ExpiredJwtException e) {
            log.error("WT token is expired: {}", e.getMessage());
            throw e;

        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
            throw new UnsupportedJwtException("JWT token is unsupported: " + e.getMessage());

        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
            throw new IllegalArgumentException("JWT claims string is empty: " + e.getMessage());
        }
        return true;
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }
}
