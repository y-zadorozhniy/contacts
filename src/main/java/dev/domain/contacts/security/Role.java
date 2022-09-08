package dev.domain.contacts.security;

import lombok.Getter;

import java.util.Arrays;

public enum Role {

    EDITOR("Редактор"),
    MODERATOR("Модератор"),
    ADMIN("Администратор");

    @Getter
    private final String value;

    Role(String roleName) {
        this.value = roleName;
    }

    public static Role from(String source) {
        return Arrays.stream(values())
                .filter(role -> role.getValue().equals(source) || role.name().equals(source))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Неизвестная роль"));
    }
}
