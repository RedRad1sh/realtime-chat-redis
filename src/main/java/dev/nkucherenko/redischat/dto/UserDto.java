package dev.nkucherenko.redischat.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@NoArgsConstructor
public class UserDto {
    @NonNull
    private String userName;
    @NonNull
    private String password;
    private String pfpUrl;
}
