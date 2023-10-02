package dev.nkucherenko.redischat.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Accessors(chain = true)
@RedisHash("User")
public class UserDetails {
    private UUID id;
    private String pfpUrl;
    @Indexed
    private String userName;
    private String password;
    private Set<GrantedAuthority> authorities = Set.of();

    public UserDetails() {
    }

    public UserDetails(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }
}
