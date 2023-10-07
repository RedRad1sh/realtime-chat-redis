package dev.nkucherenko.redischat.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.nkucherenko.redischat.dto.UserDto;
import dev.nkucherenko.redischat.entity.UserDetails;
import dev.nkucherenko.redischat.exception.InvalidJwtKeyException;
import dev.nkucherenko.redischat.exception.InvalidPasswordException;
import dev.nkucherenko.redischat.exception.UserAlreadyExistsException;
import dev.nkucherenko.redischat.exception.UserNotFoundException;
import dev.nkucherenko.redischat.repository.UserRepository;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.impl.DefaultJwsHeader;
import io.jsonwebtoken.impl.crypto.DefaultJwtSignatureValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtService {
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Value("${signatureKey}")
    private String key;

    public String createUserReturnJwt(UserDto dto) throws JsonProcessingException {
        if (userRepository.findByUserName(dto.getUserName()).isPresent()) {
            throw new UserAlreadyExistsException();
        }
        String cryptedPass = BCrypt.hashpw(dto.getPassword(), BCrypt.gensalt());
        UserDetails userDetails = new UserDetails(dto.getUserName(), cryptedPass)
                .setPfpUrl(dto.getPfpUrl());
        userRepository.save(userDetails);

        return generateJwt(userDetails);
    }

    public String buildJwtFromUserDetails(UserDto dto) throws JsonProcessingException {
        UserDetails userDetails = checkUserPassword(dto.getUserName(), dto.getPassword());

        return generateJwt(userDetails);
    }

    private String generateJwt(UserDetails userDetails) throws JsonProcessingException {
        return Jwts.builder().setIssuer("nkucherenko")
                .setSubject("redis-chat")
                .claim("user", objectMapper.writeValueAsString(userDetails))
                .setIssuedAt(Date.from(Instant.ofEpochSecond(1466796822L)))
                .setExpiration(Date.from(Instant.ofEpochSecond(4622470422L)))
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();
    }

    private UserDetails checkUserPassword(String username, String password) {
        UserDetails userDetails = userRepository.findByUserName(username)
                .orElseThrow(UserNotFoundException::new);

        if (!BCrypt.checkpw(password, userDetails.getPassword())) {
            throw new InvalidPasswordException();
        }
        return userDetails;
    }

    public UserDetails getUserInfoFromToken(String token) throws JsonProcessingException {
        // checkTokenValidity(token);

        Jwt<DefaultJwsHeader, DefaultClaims> jwt = Jwts.parser()
                .setSigningKey(key)
                .parse(token);

        UserDetails userDetails = objectMapper.readValue(jwt.getBody().get("user", String.class), UserDetails.class);

        UserDetails userDetailsSaved = userRepository.findByUserName(userDetails.getUserName())
                .orElseThrow(UserNotFoundException::new);

        if (!userDetailsSaved.getPassword().equals(userDetails.getPassword())) {
            throw new InvalidPasswordException();
        }
        return userDetails;
    }

//    private void checkTokenValidity(String token) {
//        String[] chunks = token.split("\\.");
//
//        String tokenWithoutSignature = chunks[0] + "." + chunks[1];
//        String signature = chunks[2];
//
//        SignatureAlgorithm sa = SignatureAlgorithm.HS256;
//        SecretKeySpec secretKeySpec = new SecretKeySpec(key, sa.getJcaName());
//
//        DefaultJwtSignatureValidator validator = new DefaultJwtSignatureValidator(sa, secretKeySpec);
//
//        if (!validator.isValid(tokenWithoutSignature, signature)) {
//            throw new InvalidJwtKeyException("Could not verify JWT token integrity!");
//        }
//    }
}
