package dev.domain.contacts.dto;

import dev.domain.contacts.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserInfo {

    private Long id;
    private String username;
    private String email;
    private String role;

    public static UserInfo toDto(User source) {
        return UserInfo.builder()
                .id(source.getId())
                .email(source.getEmail())
                .username(source.getUsername())
                .role(source.getRole())
                .build();
    }
}
