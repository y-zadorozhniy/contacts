package dev.domain.contacts.dto;

import dev.domain.contacts.domain.Contact;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import dev.domain.contacts.dto.internal.ContactType;

@Data
@Builder
@AllArgsConstructor
public class ContactInfo {

    private String type;
    private String icon;
    private String value;
    private String comment;

    public static ContactInfo toDto(Contact source) {
        ContactType type = ContactType.from(source.getType());
        return ContactInfo.builder()
                .type(type.getValue())
                .icon(type.getIcon())
                .comment(source.getComment())
                .value(source.getValue())
                .build();
    }
}
