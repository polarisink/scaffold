package com.scaffold.security.util;

import cn.hutool.crypto.SecureUtil;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.scaffold.base.constant.GlobalConstant;
import com.scaffold.base.exception.BaseException;
import com.scaffold.base.util.JsonUtil;
import com.scaffold.security.vo.PayloadDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.Assert;

@Slf4j
public class JwtUtil {

    public static String JWT_SECRET_KEY = GlobalConstant.SECRET;

    public static String generateToken(PayloadDTO p) {
        Assert.hasText(p.getUsername(), "错误的token");
        try {
            JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();
            Payload payload = new Payload(JsonUtil.toJson(p));
            JWSObject jwsObject = new JWSObject(jwsHeader, payload);
            JWSSigner jwsSigner = new MACSigner(SecureUtil.md5(JWT_SECRET_KEY));
            jwsObject.sign(jwsSigner);
            return jwsObject.serialize();
        } catch (Exception e) {
            log.error("generate token error:{}", e.getMessage());
            throw new BaseException("token获取失败,请稍后重试！");
        }
    }

    public static PayloadDTO resolveToken(String token) {
        try {
            if (token == null || token.isBlank()) {
                throw new IllegalArgumentException("不合法的token");
            }
            JWSObject jwsObject = JWSObject.parse(token);
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

    public static String getRealToken(String authorizationToken) {
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
