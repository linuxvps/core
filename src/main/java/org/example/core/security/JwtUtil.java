package org.example.core.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}") // کلید مخفی JWT از application.properties
    private String SECRET_KEY;

    @Value("${jwt.expiration}") // مدت زمان انقضای Token (میلی‌ثانیه)
    private long EXPIRATION_TIME; // مثال: 86400000 (24 ساعت)

    // بازیابی تمام Claims از Token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    // استخراج نام کاربری از Token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // استخراج تاریخ انقضا از Token
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // اعتبارسنجی Token
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    // بررسی انقضای Token
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // تولید Token
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        // می‌توانید اطلاعات دیگری را به Claims اضافه کنید (مثلاً نقش‌ها)
        // claims.put("roles", userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        return createToken(claims, userDetails.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // دریافت کلید Secret (با تبدیل از رشته Base64)
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}