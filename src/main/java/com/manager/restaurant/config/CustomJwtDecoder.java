package com.manager.restaurant.config;

import com.manager.restaurant.constant.JwtConstant;
import com.manager.restaurant.exception.BadException;
import com.manager.restaurant.exception.ErrorCode;
import com.manager.restaurant.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import java.text.ParseException;

@Component
public class CustomJwtDecoder implements JwtDecoder {
    @Value("${jwt.signerKey}")
    private String signerKey;
    @Autowired
    private AuthenticationService authenticationService;
    private NimbusJwtDecoder nimbusJwtDecoder;

    @Override
    public Jwt decode(String token) throws JwtException {
        try {
            authenticationService.verifyToken(token);
        } catch (RuntimeException | ParseException | JOSEException e) {
            throw new BadException(ErrorCode.UNAUTHENTICATED);
        }
        if (ObjectUtils.isEmpty(nimbusJwtDecoder)) {
            SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), JwtConstant.ALGORITHM);
            nimbusJwtDecoder = NimbusJwtDecoder
                    .withSecretKey(secretKeySpec)
                    .macAlgorithm(MacAlgorithm.HS512)
                    .build();
            System.out.println("sssssssssssssssssssssssssssss");
        }
        return nimbusJwtDecoder.decode(token);
    }
}

