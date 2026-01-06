package com.example.demo.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtils {

    // 1. 签名密钥（随便写，但必须保密，不要太短）
    private static final String SECRET_KEY = "MySecretKey_YouCanChangeIt_To_Anything_Complex";

    // 2. 过期时间：这里设为 24 小时 (毫秒单位)
    private static final long EXPIRE_TIME = 24 * 60 * 60 * 1000L;

    /**
     * 生成 Token
     * @param claims 想要藏在 Token 里的数据（比如用户ID、用户名）
     * @return String 类型的 Token 字符串
     */
    public static String generateToken(Map<String, Object> claims) {
        return Jwts.builder()
                .setClaims(claims) // 放入数据
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRE_TIME)) // 设置过期时间
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY) // 签名算法
                .compact(); // 生成字符串
    }

    /**
     * 解析 Token（从 Token 里把数据取出来）
     */
    public static Claims parseToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
}