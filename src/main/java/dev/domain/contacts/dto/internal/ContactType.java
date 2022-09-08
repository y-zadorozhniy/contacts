package dev.domain.contacts.dto.internal;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

public enum ContactType {

    PHONE("Телефон", "phone_type.png"),
    EMAIL("Email", "email_type.png"),
    VK("ВКонтакте", "vk_type.png"),
    INSTAGRAM("Instagram", "instagram_type.png"),
    ODNOKLASSNIKI("Одноклассники", "ok_type.png"),
    FACEBOOK("Facebook", "fb_type.png"),
    LINKEDIN("LinkedIn", "linkedin_type.png"),
    OTHER("Другое", "other_type.png");

    @Getter
    private final String value;

    @Getter
    private final String icon;

    ContactType(String type, String icon) {
        this.value = type;
        this.icon = icon;
    }

    public static ContactType from(String type) {
        return Arrays.stream(values())
                .filter(contactType -> Objects.equals(contactType.value, type) ||
                        Objects.equals(contactType.name(), type))
                .findFirst()
                .orElseThrow();
    }
}