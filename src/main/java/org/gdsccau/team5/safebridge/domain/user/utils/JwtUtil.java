package org.gdsccau.team5.safebridge.domain.user.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

  @Value("${auth.secretKey}")
  private String secretKey;

  public String getAccessToken(final Long userId) {
    Claims claims = Jwts.claims().setSubject(String.valueOf(userId));

    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + 1000L * 60 * 60); // 토큰 만료 시간: 1시간 후

    return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(expiryDate)
        .signWith(SignatureAlgorithm.HS256, secretKey).compact();
  }
}
