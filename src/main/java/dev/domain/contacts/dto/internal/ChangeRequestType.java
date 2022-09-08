package dev.domain.contacts.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.thymeleaf.util.StringUtils;
import dev.domain.contacts.exceptions.ServerErrorException;

import java.util.Arrays;

@AllArgsConstructor
public enum ChangeRequestType {

    ADD("Добавление"),
    CHANGE("Изменение"),
    REMOVE("Удаление");

    @Getter
    private String value;

    public static ChangeRequestType from(String type) {
        return Arrays.stream(values())
                .filter(t -> StringUtils.equals(t.name(), type))
                .findFirst()
                .orElseThrow(() -> new ServerErrorException("Unknown change request type"));
    }
}
