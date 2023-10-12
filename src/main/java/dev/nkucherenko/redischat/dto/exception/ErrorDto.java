package dev.nkucherenko.redischat.dto.exception;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class ErrorDto {
    private ErrorCode code;
    private String description;
    private LocalDateTime timestamp = LocalDateTime.now();
}
