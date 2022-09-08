package dev.domain.contacts.dto;

import dev.domain.contacts.domain.Contact;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import dev.domain.contacts.dto.internal.ContactType;

@Data
@Builder
@AllArgsConstructor
public class ContactEditInfo {

    private ContactType type;
    private String value;
    private String comment;

    public static ContactEditInfo toDto(Contact source) {
        return ContactEditInfo.builder()
                .type(ContactType.from(source.getType()))
                .comment(source.getComment())
                .value(source.getValue())
                .build();
    }
}
