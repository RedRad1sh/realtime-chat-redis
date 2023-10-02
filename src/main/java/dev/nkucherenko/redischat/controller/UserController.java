package dev.nkucherenko.redischat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.nkucherenko.redischat.dto.UserDto;
import dev.nkucherenko.redischat.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final JwtService jwtService;

    @PostMapping("/token/auth")
    public ResponseEntity<Void> auth(@RequestBody UserDto userDto) throws JsonProcessingException {
        return ResponseEntity.ok().header("Authorization", jwtService.buildJwtFromUserDetails(userDto)).build();
    }

    @PostMapping("/token/register")
    public ResponseEntity<Void> register(@RequestBody UserDto userDto) throws JsonProcessingException {
        return ResponseEntity.ok().header("Authorization", jwtService.createUserReturnJwt(userDto)).build();
    }

}
