package com.daengdaeng_eodiga.project.global.Security.config;

import com.daengdaeng_eodiga.project.global.enums.Jwtexception;
import com.daengdaeng_eodiga.project.oauth.OauthProvider;

import io.jsonwebtoken.*;

import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;

import java.time.*;
import java.util.Date;

@Slf4j
@Component
public class JWTUtil {

    private final SecretKey secretKey;
    private final String text_key;
    public JWTUtil(@Value("${spring.jwt.secret}")String secret) {
        this.secretKey =  Keys.hmacShaKeyFor(secret.getBytes());
        this.text_key=secret;
    }
    @Getter
    @Value("${jwt.token-expiration.access}")
    private int accessTokenExpiration;

    @Getter
    @Value("${jwt.token-expiration.refresh}")
    private int refreshTokenExpiration;

    public String getEmail(String token) {
        String email = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("email", String.class);
        log.info("jwt - getEmail : " + email);
        return email;
    }

    public OauthProvider getProvider (String token) {
        String provider = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("provider", String.class);
        log.info("jwt - getProvider : " + provider);
        return OauthProvider.valueOf(provider);
    }
    /**
     * JWT의 만료 시간을 계산하여 반환하는 메소드입니다.
     * @author 김승환
     * 주어진 토큰의 만료 시간을 계산하고, 현재 시간과 비교하여 남은 시간을 초 단위로 반환합니다.
     * 만약 토큰이 유효하지 않거나 예외가 발생할 경우 0을 반환합니다.
     *
     * @param token 유효성을 검사할 JWT 토큰
     * @return 토큰의 만료까지 남은 시간(초 단위). 유효하지 않은 경우 0을 반환합니다.
     */

    public long getExpiration(String token) {
        try {
            Date expiration =Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token).getPayload().getExpiration();

            LocalDateTime targetTime = expiration.toInstant()
                    .atZone(ZoneId.of("CST6CDT"))
                    .toLocalDateTime();

            LocalDateTime now = LocalDateTime.now(ZoneId.of("CST6CDT"));

            return Duration.between(now, targetTime).getSeconds();
        } catch (Exception e) {
            return 0;
        }
    }
    /**
     * 주어진 JWT 토큰의 유효성을 검사하는 메소드입니다.
     * @author 김승환
     * 토큰의 유효성 검사 결과를 {@link Jwtexception} 타입으로 반환합니다.
     * 토큰이 만료되었거나 형식이 잘못되었거나 기타 다른 오류가 발생하면 해당 오류에 맞는 값을 반환합니다.
     *
     * @param token 검사할 JWT 토큰
     * @return 토큰 유효성 검사 결과를 나타내는 {@link Jwtexception} 값
     */
    public Jwtexception isJwtValid(String token) {
        try {
            SecretKey key = Keys.hmacShaKeyFor(text_key.getBytes());

            Claims claims = Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Jwtexception.normal;

        } catch (ExpiredJwtException e) {
            return Jwtexception.expired;
        } catch (MalformedJwtException e) {
            return Jwtexception.mismatch;
        }
        catch (UnsupportedJwtException e) {
            return Jwtexception.unsupportedJwt;
        } catch (IllegalArgumentException e) {
            return Jwtexception.invalidArgument;
        } catch (JwtException e) {
            return Jwtexception.otherError;
        }
    }

    /**
     * 주어진 이메일과 제공자를 기반으로 JWT를 생성하는 메소드입니다.
     * @author 김승환
     * 지정된 만료 시간(ms)을 기준으로 JWT 토큰을 생성하여 반환합니다.
     *
     * @param email 사용자 이메일
     * @param provider 인증 제공자
     * @param expiredMs JWT 토큰의 만료 시간(밀리초 단위)
     * @return 생성된 JWT 토큰 문자열
     */
    public String createJwt(String email, String provider, int expiredMs) {
        log.info("jwt - createJwt email: " + email);
        long now = new Date().getTime();
        return Jwts.builder()
                .claim("email", email)
                .claim("provider", provider)
                .issuedAt(new Date(now))
                .expiration(new Date(now+expiredMs))
                .signWith(secretKey)
                .compact();
    }
    /**
     * 주어진 이메일과 제공자를 기반으로 리프레시 토큰을 생성하는 메소드입니다.
     * @author 김승환
     * 지정된 만료 시간(ms)을 기준으로 리프레시 토큰을 생성하여 반환합니다.
     *
     * @param email 사용자 이메일
     * @param provider 인증 제공자
     * @param expiredMs 리프레시 토큰의 만료 시간(밀리초 단위)
     * @return 생성된 리프레시 토큰 문자열
     */
    public String createRefreshToken(String email, String provider, int expiredMs) {
        long now = new Date().getTime();
        return Jwts.builder()
                .claim("email", email)
                .claim("provider", provider)
                .issuedAt(new Date(now))
                .expiration(new Date(now+expiredMs))
                .signWith(secretKey)
                .compact();
    }
    /**
     * 쿠키를 생성하는 메소드입니다.
     * @author 김승환
     * 주어진 키와 값, 만료 시간을 기반으로 쿠키를 생성하여 반환합니다.
     *
     * @param key 쿠키의 이름
     * @param value 쿠키의 값
     * @param expiredMs 쿠키의 만료 시간(밀리초 단위)
     * @return 생성된 {@link ResponseCookie}
     */
    public ResponseCookie createCookie(String key, String value, int expiredMs) {
        return ResponseCookie.from(key, value)
                .path("/")
                .sameSite("Lax")
                .httpOnly(true)
                .secure(true)
                .maxAge(expiredMs)
                .domain(".daengdaeng-where.link")
                .build();
    }
}