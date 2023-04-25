package com.staticanalyzer.staticanalyzer.config.jwt;

import java.security.Key;

import javax.crypto.spec.SecretKeySpec;

import io.jsonwebtoken.SignatureAlgorithm;

import org.springframework.stereotype.Component;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.core.convert.converter.Converter;

/**
 * 将H256密钥自动转为{@code java.security.Key}的形式
 * 
 * @author iu_oi
 * @since 0.0.2
 */
@Component
@ConfigurationPropertiesBinding
public class JwtKeyConverter implements Converter<String, Key> {

    @Override
    public Key convert(String secret) {
        return new SecretKeySpec(secret.getBytes(), SignatureAlgorithm.HS256.getJcaName());
    }
}
