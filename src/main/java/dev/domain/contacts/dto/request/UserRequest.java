package dev.domain.contacts.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {

    private String username;
    private String email;
    private String role;
    private String password;
    private String passwordRepeat;
}
