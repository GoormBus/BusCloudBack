package goorm.bus.global.jwt;

import goorm.bus.global.exception.CustomException;
import goorm.bus.global.exception.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private SecretKey secretKey;
    private final long expiration = 1000 * 60 * 10; // 10분

    public JwtUtil(@Value("${jwt.secret}") String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),
                Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String createToken(String id) {
        long now = System.currentTimeMillis();

        return Jwts.builder()
                .claim("id", id) // id
                .issuedAt(new Date(now)) // iat
                .expiration(new Date(now + expiration)) // exp
                .signWith(secretKey)
                .compact();
    }

    public Boolean isValidToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
        } catch (SignatureException e) {
            return false;
        }
        return true;
    }

    public Boolean isExpired(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
                    .getPayload().getExpiration();
        } catch (ExpiredJwtException e) {
            return true;
        }
        return false;
    }

    public String getId(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token)
                .getPayload().get("id", String.class);
    }

    public boolean verify(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseClaimsJws(token);
        }catch (SecurityException | MalformedJwtException e) {
            throw new CustomException(ErrorCode.JWT_ERROR_TOKEN);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.JWT_EXPIRE_TOKEN);
        } catch (UnsupportedJwtException e) {
            throw new CustomException(ErrorCode.JWT_ERROR_TOKEN);
        } catch (IllegalArgumentException e) {
            throw new CustomException(ErrorCode.JWT_ERROR_TOKEN);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.JWT_ERROR_TOKEN);
        }

        return true;
    }
}
