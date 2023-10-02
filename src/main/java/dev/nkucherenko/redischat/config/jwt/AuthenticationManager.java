package dev.nkucherenko.redischat.config.jwt;

import dev.nkucherenko.redischat.entity.UserDetails;
import dev.nkucherenko.redischat.exception.InvalidJwtKeyException;
import dev.nkucherenko.redischat.repository.UserRepository;
import dev.nkucherenko.redischat.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Lazy
@Component
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String jwtToken = authentication.getCredentials().toString();
        return Mono.just(tokenValidate(jwtToken))
                .map(this::getAuthorities);
    }

    private UsernamePasswordAuthenticationToken getAuthorities(UserDetails userDetails) {
        return new UsernamePasswordAuthenticationToken(
                userDetails.getUserName(), null,
                userDetails.getAuthorities().stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                        .collect(Collectors.toList()));
    }

    private UserDetails tokenValidate(String token) {
        try {
            UserDetails userDetails = jwtService.getUserInfoFromToken(token);
            userRepository.findByUserName(userDetails.getUserName());
            return userDetails;
        } catch (Exception e) {
            throw new InvalidJwtKeyException(e);
        }
    }
}
