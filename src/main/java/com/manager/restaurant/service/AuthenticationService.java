    package com.manager.restaurant.service;

    import com.manager.restaurant.dto.request.AuthenticationRequest;
    import com.manager.restaurant.dto.request.IntrospectRequest;
    import com.manager.restaurant.dto.response.AuthenticationResponse;
    import com.manager.restaurant.dto.response.IntrospectResponse;
    import com.manager.restaurant.entity.Account;
    import com.manager.restaurant.entity.InvalidatedToken;
    import com.manager.restaurant.exception.BadException;
    import com.manager.restaurant.exception.ErrorCode;
    import com.manager.restaurant.repository.AccountRepository;
    import com.manager.restaurant.repository.InvalidatedTokenRepository;
    import com.manager.restaurant.until.JwtUtil;
    import com.manager.restaurant.until.SecurityUtils;
    import com.nimbusds.jose.JOSEException;
    import com.nimbusds.jwt.SignedJWT;
    import lombok.AccessLevel;
    import lombok.RequiredArgsConstructor;
    import lombok.experimental.FieldDefaults;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.crypto.password.PasswordEncoder;
    import org.springframework.stereotype.Service;

    import java.text.ParseException;
    import java.time.LocalDateTime;
    import java.time.ZoneId;
    import java.util.Objects;

    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @Service
    @Slf4j
    public class AuthenticationService {
        AccountRepository accountRepository;
        InvalidatedTokenRepository invalidatedTokenRepository;
        JwtUtil jwtUtil;


        public AuthenticationResponse authenticate(AuthenticationRequest request) {
            Account user = accountRepository.findByUsername(request.getUsername()).orElseThrow(
                    () -> new BadException(ErrorCode.USER_NOT_EXISTED));
            PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                throw new BadException(ErrorCode.UNAUTHENTICATED);
            }

            var token = jwtUtil.generateToken(user.getUsername()
                    , user.getRole());
            return AuthenticationResponse.builder()
                    .token(token)
                    .build();
        }

        public SignedJWT verifyToken(String token) throws ParseException, JOSEException {
            SignedJWT signedJWT = jwtUtil.parseToken(token);
            jwtUtil.validateTokenExpiration(signedJWT);
            System.out.println("111111111111111111");
            checkTokenInvalidation(signedJWT.getJWTClaimsSet().getJWTID());
            System.out.println("33333333333333333");
            return signedJWT;
        }

        private void checkTokenInvalidation(String jwtId) {
            System.out.println("22222222222222222222");
            if (invalidatedTokenRepository.existsById(jwtId)) {
                System.out.println("44444444444444444");
                throw new BadException(ErrorCode.UNAUTHENTICATED);
            }
        }


        public IntrospectResponse introspect(IntrospectRequest request) {
            var token =request.getToken();
            boolean isValid = true;
            try {
                verifyToken(token);
            }catch (BadException | ParseException | JOSEException e) {
                isValid = false;
            }
            return IntrospectResponse.builder()
                    .valid(isValid)
                    .build();
        }


        public void logout() {
            try {
                invalidateToken(Objects.requireNonNull(SecurityUtils.getCurrentJWTToken()));
            } catch (ParseException e) {
                log.error(e.getMessage());
                throw new BadException(ErrorCode.UNCATEGORIZED_EXCEPTION);
            }
        }

        public void invalidateToken(SignedJWT signToken) throws ParseException{
            String jwt = signToken.getJWTClaimsSet().getJWTID();
            LocalDateTime expiryTime = signToken.getJWTClaimsSet().getExpirationTime().toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            InvalidatedToken invalidatedToken = new InvalidatedToken(jwt, expiryTime);
            invalidatedTokenRepository.save(invalidatedToken);
        }
    }
