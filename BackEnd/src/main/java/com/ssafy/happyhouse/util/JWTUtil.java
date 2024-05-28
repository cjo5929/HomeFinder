package com.ssafy.happyhouse.util;

import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class JWTUtil {
    @Value("${jwt.salt}")
    private String salt;
    @Value("${jwt.access-token.expiretime}")
    private long accessTokenExpireTime;
    @Value("${jwt.refresh-token.expiretime}")
    private long refreshTokenExpireTime;

    public String createRefreshToken(String userId, String userRole) {
        return create(userId, userRole,"refreshToken", refreshTokenExpireTime);
    }

    public String createAccessToken(String userId, String userRole) {
        return create(userId, userRole, "accessToken", accessTokenExpireTime);
    }

    private String create(String userId, String userRole, String subject, long expireTime) {
        Claims claims = Jwts.claims()
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expireTime));
        claims.put("userId", userId);
        claims.put("userRole", "ROLE_" + userRole.toUpperCase());
        String jwt = Jwts.builder()
                .setHeaderParam("typ", "jwt").setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, this.generateKey())
                .compact();
        return jwt;
    }

    private byte[] generateKey() {
        byte[] key = null;
        try {
            key = salt.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return key;
    }

    public boolean checkToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(this.generateKey()).parseClaimsJws(token);
            log.debug("claims: {}", claims);
            return true;
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
    }

    private Claims parseClaims(String token) {
        Jws<Claims> claims = null;
        try {
            claims = Jwts.parser().setSigningKey(this.generateKey()).parseClaimsJws(token);
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new IllegalArgumentException("Invalid JWT token");
        }
        return claims.getBody();
    }

    public String getUserId(String authorization) {
        Claims claims = parseClaims(authorization);
        log.info("id = {}",claims.get("userId", String.class));
        return claims.get("userId", String.class);
    }

    public String getUserRole(String authorization) {
        Claims claims = parseClaims(authorization);
        log.info("id = {}",claims.get("userRole", String.class));
        return claims.get("userRole", String.class);
    }
}