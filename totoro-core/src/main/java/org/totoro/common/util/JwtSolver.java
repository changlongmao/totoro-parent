package org.totoro.common.util;

import com.fasterxml.jackson.core.type.TypeReference;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import org.totoro.common.javabean.dto.JwtClaimDTO;
import org.totoro.common.json.JsonUtils;
import org.totoro.common.properties.JwtProperties;

import javax.annotation.Resource;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>jwt工具类</p>
 * <pre>
 *     jwt的claim里一般包含以下几种数据:
 *         1. iss -- token的发行者
 *         2. sub -- 该JWT所面向的用户
 *         3. aud -- 接收该JWT的一方
 *         4. exp -- token的失效时间
 *         5. nbf -- 在此时间段之前,不会被处理
 *         6. iat -- jwt发布时间
 *         7. jti -- jwt唯一标识,防止重复使用
 * </pre>
 *
 * @author ChangLF 2023-8-15
 */
@Component
public class JwtSolver {

    @Resource
    private JwtProperties jwtProperties;

    /**
     * 解析token
     */
    public JwtClaimDTO parseToken(String token) {
        return JsonUtils.toObject(JsonUtils.toJson(getClaimFromToken(token)), JwtClaimDTO.class);
    }

    /**
     * 生成token
     */
    public String generateToken(JwtClaimDTO jwtClaimDTO) {
        Date createdDate = new Date();
        Date expirationDate = new Date(createdDate.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .setClaims(JsonUtils.toObject(JsonUtils.toJson(jwtClaimDTO), new TypeReference<Map<String, Object>>() {}))
                .setIssuedAt(createdDate)
                .setHeader(new HashMap<String, Object>(){{put("typ", "JWT");}})
                .setExpiration(expirationDate)
                .setNotBefore(createdDate)
                .signWith(SignatureAlgorithm.HS256, generalKey())
                .compact();
    }

    /**
     *  验证token是否失效
     *  true:过期   false:没过期
     */
    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException expiredJwtException) {
            return true;
        }
    }

    /**
     * 获取jwt的payload部分
     */
    public Claims getClaimFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(generalKey())
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 获取jwt发布时间
     */
    public Date getIssuedDateFromToken(String token) {
        return getClaimFromToken(token).getIssuedAt();
    }

    /**
     * 获取jwt失效时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token).getExpiration();
    }

    /**
     * 获取SecretKey
     */
    public SecretKey generalKey() {
        return new SecretKeySpec(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8), 0,
                jwtProperties.getSecret().length(), "AES");
    }

}
