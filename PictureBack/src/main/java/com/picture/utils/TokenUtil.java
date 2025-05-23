package com.picture.utils;

import com.picture.domain.User;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class TokenUtil {
    // token的有效时间
    @Value("${spring.jwt.ttl}")
    private long ttl;

    // JWT的签名秘钥，用于加密和验证
    @Value("${spring.jwt.signature}")
    private String signature;

    /**
     * 创建 JWT token
     *
     * @param userName 用户名
     * @param id       用户ID
     * @return 加密后的 JWT 字符串
     */
    public String createToken(String userName,Integer id){
        JwtBuilder jwtBuilder= Jwts.builder();

        // 生成Token
        String jwtToken=jwtBuilder
                // 设置Header参数
                .setHeaderParam("typ","JWT")
                .setHeaderParam("alg","HS256")
                // 设置Payload内容
                .claim("userId",id.toString())
                .claim("userName",userName)
                .setSubject("Memory")
                .setExpiration(new Date(System.currentTimeMillis() + ttl))   // 设置过期时间
                .setId(UUID.randomUUID().toString())    // 设置唯一ID
                // 设置签名算法和秘钥
                .signWith(SignatureAlgorithm.HS256,signature)
                .compact(); // 压缩为字符串
        return jwtToken;
    }

    /**
     * 解析 JWT token，提取出用户信息
     *
     * @param token JWT 字符串
     * @return 解析后的 User 对象，如果失败返回 null
     */
    public User jwtParser(String token) {
        Claims claims = null;   // 存放解析出的载荷数据
        User user= new User();  // 用户对象

        // 判断token是否为空或格式错误
        if(token ==null||token.length()==0) return null;

        // token不一定通过验证，所以需要包裹try-catch捕获jwt提供的JwtException
        try {
            // 解析Token，获得claims数据，即payload
            claims = Jwts.parser()
                    // HS256是对称加密体系，加密解密使用同一个key
                    .setSigningKey(signature)   // 设置签名秘钥
                    .parseClaimsJws(token)  // 解析token
                    .getBody();     // 获取Payload数据
        } catch (ExpiredJwtException e) {
            // Token过期
            throw new RuntimeException("JWT过期");
        } catch (UnsupportedJwtException e) {
            // Token格式不支持
            throw new RuntimeException("不支持的JWT");
        } catch (MalformedJwtException e) {
            // Token格式错误
            throw new RuntimeException("JWT格式错误");
        } catch (SignatureException e) {
            // 签名校验失败
            throw new RuntimeException("签名异常");
        } catch (IllegalArgumentException e) {
            // 参数不合法（空、非法等）
            throw new RuntimeException("非法请求");
        } catch (Exception e) {
            // 其它异常
            throw new RuntimeException("解析异常");
        }
        finally {
            // 如果成功获取 claims，则封装为 User 对象，这提取出了用户的信息
            if(claims!=null) {
                int userId = Integer.parseInt((String) claims.get("userId"));
                String userName = (String) claims.get("userName");
                user.setUserId(userId);
                user.setUserName(userName);
            }
            else{
                return null;
            }
            return user;
        }
    }
}
