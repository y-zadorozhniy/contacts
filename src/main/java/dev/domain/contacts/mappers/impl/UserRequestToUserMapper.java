package dev.domain.contacts.mappers.impl;

import dev.domain.contacts.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import dev.domain.contacts.domain.User;
import dev.domain.contacts.dto.request.UserRequest;
import dev.domain.contacts.security.Role;

@Component
@RequiredArgsConstructor
public class UserRequestToUserMapper implements Mapper<UserRequest, User> {

    private final PasswordEncoder passwordEncoder;

    @Override
    public User map(UserRequest source) {
        return User.builder()
                .username(source.getUsername())
                .email(source.getEmail())
                .role(Role.from(source.getRole()).name())
                .password(passwordEncoder.encode(source.getPassword()))
                .build();
    }
}
