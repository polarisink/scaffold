package com.scaffold.security.util;

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
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Slf4j
public class JwtUtil {

    private static final int MINIMUM_KEY_LENGTH = 32;
    private final byte[] signingKey;

    public JwtUtil(String secret) {
        Assert.hasText(secret, "security.token.jwt-secret 不能为空");
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        Assert.isTrue(key.length >= MINIMUM_KEY_LENGTH,
                "security.token.jwt-secret 长度不能少于 32 字节");
        this.signingKey = key;
    }

    public String generateToken(PayloadDTO payloadDTO) {
        Assert.notNull(payloadDTO, "token payload不能为空");
        Assert.hasText(payloadDTO.getUsername(), "错误的token");
        Assert.notNull(payloadDTO.getUserId(), "错误的token");
        normalizePayload(payloadDTO);
        try {
            JWSHeader jwsHeader = new JWSHeader.Builder(JWSAlgorithm.HS256).type(JOSEObjectType.JWT).build();
            Payload payload = new Payload(JsonUtil.toJson(payloadDTO));
            JWSObject jwsObject = new JWSObject(jwsHeader, payload);
            JWSSigner jwsSigner = new MACSigner(signingKey);
            jwsObject.sign(jwsSigner);
            return jwsObject.serialize();
        } catch (Exception e) {
            log.error("generate token error", e);
            throw new BaseException("token生成失败，请稍后重试");
        }
    }

    public PayloadDTO resolveToken(String token) {
        try {
            Assert.hasText(token, "token不能为空");
            JWSObject jwsObject = JWSObject.parse(token);
            JWSVerifier jwsVerifier = new MACVerifier(signingKey);
            boolean verify = jwsObject.verify(jwsVerifier);
            if (!verify) {
                throw new BaseException("token校验失败");
            }
            PayloadDTO payloadDTO = JsonUtil.read(jwsObject.getPayload().toString(), PayloadDTO.class);
            validatePayload(payloadDTO);
            return payloadDTO;
        } catch (BaseException e) {
            throw e;
        } catch (Exception e) {
            log.error("resolve token error", e);
            throw new BaseException("token解析失败");
        }
    }

    public static String getRealToken(String authorizationToken) {
        if (!StringUtils.hasText(authorizationToken)) {
            return null;
        }
        String bearer = GlobalConstant.AUTHORIZATION_TOKEN_BEARER.trim();
        String normalized = authorizationToken.trim();
        if (normalized.length() <= bearer.length()) {
            return null;
        }
        if (!normalized.regionMatches(true, 0, bearer, 0, bearer.length())) {
            return null;
        }
        String token = normalized.substring(bearer.length()).trim();
        return StringUtils.hasText(token) ? token : null;
    }

    private static void normalizePayload(PayloadDTO payloadDTO) {
        long now = Instant.now().getEpochSecond();
        if (!StringUtils.hasText(payloadDTO.getSub()) && payloadDTO.getUserId() != null) {
            payloadDTO.setSub(String.valueOf(payloadDTO.getUserId()));
        }
        if (!StringUtils.hasText(payloadDTO.getJti())) {
            payloadDTO.setJti(payloadDTO.getUserId() + "-" + now);
        }
        if (payloadDTO.getIat() == null || payloadDTO.getIat() <= 0) {
            payloadDTO.setIat(now);
        }
        if (payloadDTO.getExp() == null || payloadDTO.getExp() <= payloadDTO.getIat()) {
            payloadDTO.setExp(payloadDTO.getIat() + 365L * 24 * 60 * 60);
        }
    }

    private static void validatePayload(PayloadDTO payloadDTO) {
        if (payloadDTO == null) {
            throw new BaseException("token内容为空");
        }
        if (payloadDTO.getUserId() == null || !StringUtils.hasText(payloadDTO.getUsername())) {
            throw new BaseException("token内容非法");
        }
        Long exp = payloadDTO.getExp();
        if (exp == null) {
            throw new BaseException("token缺少过期时间");
        }
        long now = Instant.now().getEpochSecond();
        if (exp <= now) {
            throw new BaseException("token已过期");
        }
    }
}
