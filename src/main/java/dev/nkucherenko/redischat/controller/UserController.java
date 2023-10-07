package dev.nkucherenko.redischat.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import dev.nkucherenko.redischat.dto.RoomDto;
import dev.nkucherenko.redischat.dto.UserDto;
import dev.nkucherenko.redischat.entity.Room;
import dev.nkucherenko.redischat.entity.UserDetails;
import dev.nkucherenko.redischat.repository.UserRepository;
import dev.nkucherenko.redischat.service.RoomService;
import dev.nkucherenko.redischat.service.impl.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoomService roomService;

    @PostMapping("/token/auth")
    public ResponseEntity<Void> auth(@RequestBody UserDto userDto) throws JsonProcessingException {
        return ResponseEntity.ok().header("Authorization", jwtService.buildJwtFromUserDetails(userDto)).build();
    }

    @PostMapping("/token/register")
    public ResponseEntity<Void> register(@RequestBody UserDto userDto) throws JsonProcessingException {
        return ResponseEntity.ok().header("Authorization", jwtService.createUserReturnJwt(userDto)).build();
    }

    @GetMapping("/user/rooms")
    public ResponseEntity<Iterable<RoomDto>> getUserRooms(Authentication authentication) {
        return ResponseEntity.ok(roomService.getAllRoomsByIds(userRepository
                .findById(UUID.fromString(authentication.getDetails().toString()))
                .orElse(new UserDetails()).getRoomIds()));
    }

}
