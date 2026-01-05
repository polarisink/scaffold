package com.scaffold.security.util;

import cn.hutool.crypto.SecureUtil;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.scaffold.base.constant.GlobalConstant;
import com.scaffold.base.constant.ResultCodeEnum;
import com.scaffold.base.exception.BaseException;
import com.scaffold.base.util.JsonUtil;
import com.scaffold.security.vo.PayloadDTO;
import lombok.extern.slf4j.Slf4j;

/**
 * JWT工具类
 */
@Slf4j
public class JwtUtil {

    public static String JWT_SECRET_KEY = GlobalConstant.SECRET;

    public static String generateToken(PayloadDTO p) {
        //校验基本信息
        ResultCodeEnum.TOKEN_UNAUTHORIZED.isFalse(p.getUsername() == null || p.getUsername().isBlank());
        try {
            //创建JWS头，设置签名算法和类型
            JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256).
                    type(JOSEObjectType.JWT)
                    .build();
            //将负载信息封装到Payload中
            Payload payload = new Payload(JsonUtil.toJson(p));
            //创建JWS对象
            JWSObject jwsObject = new JWSObject(jwsHeader, payload);
            //创建HMAC签名器
            JWSSigner jwsSigner = new MACSigner(SecureUtil.md5(JWT_SECRET_KEY));
            //签名
            jwsObject.sign(jwsSigner);
            return jwsObject.serialize();
        } catch (Exception e) {
            log.error("generate token error:{}", e.getMessage());
            throw new BaseException("token获取失败,请稍后重试！");
        }
    }


    /**
     * 解析token
     *
     * @param token 令牌
     * @return {@link PayloadDTO}
     */
    public static PayloadDTO resolveToken(String token) {
        try {
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("不合法的token");
            }
            //从token中解析JWS对象
            JWSObject jwsObject = JWSObject.parse(token);
            //创建HMAC验证器
            JWSVerifier jwsVerifier = new MACVerifier(SecureUtil.md5(JWT_SECRET_KEY));
            boolean verify = jwsObject.verify(jwsVerifier);
            if (!verify) {
                throw new BaseException("校验token失败");
            }
            String payload = jwsObject.getPayload().toString();
            return JsonUtil.read(payload, PayloadDTO.class);
        } catch (Exception e) {
            log.error("analysisToken error:{}", e.getMessage());
            throw new BaseException("analysisToken error");
        }
    }

    /**
     * 获取真实token
     *
     * @param authorizationToken header中的原始token
     * @return 真实token
     */
    public static String getRealToken(String authorizationToken) {
        // 如果前端设置了令牌前缀，则裁剪掉前缀
        if (authorizationToken == null || authorizationToken.isBlank()) {
            return null;
        }
        if (authorizationToken.length() < 100) {
            return null;
        }
        if (authorizationToken.startsWith(GlobalConstant.AUTHORIZATION_TOKEN_BEARER.toLowerCase().trim())) {
            authorizationToken = authorizationToken.replaceFirst(GlobalConstant.AUTHORIZATION_TOKEN_BEARER.toLowerCase(), "");
        }
        if (authorizationToken.startsWith(GlobalConstant.AUTHORIZATION_TOKEN_BEARER.trim())) {
            authorizationToken = authorizationToken.replaceFirst(GlobalConstant.AUTHORIZATION_TOKEN_BEARER, "");
        }
        return authorizationToken;
    }

}