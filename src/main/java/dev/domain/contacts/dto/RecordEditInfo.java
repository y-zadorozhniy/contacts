package dev.domain.contacts.dto;

import dev.domain.contacts.domain.Record;
import dev.domain.contacts.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
public class RecordEditInfo {

    private Long id;
    private String fullName;
    private String organization;
    private String occupation;
    private String country;
    private String city;
    private List<ContactEditInfo> contacts;
    private String tags;
    private String comment;

    public static RecordEditInfo toDto(Record source) {
        String tags = source.getTags()
                .stream()
                .map(Tag::getName)
                .collect(Collectors.joining("\n"));
        return RecordEditInfo.builder()
                .id(source.getId())
                .fullName(source.getFullName())
                .country(Optional.ofNullable(source.getCountry()).orElse(""))
                .city(Optional.ofNullable(source.getCity()).orElse(""))
                .organization(Optional.ofNullable(source.getOrganization()).orElse(""))
                .occupation(Optional.ofNullable(source.getOccupation()).orElse(""))
                .contacts(source.getContacts()
                        .stream()
                        .map(ContactEditInfo::toDto)
                        .collect(Collectors.toList()))
                .tags(tags)
                .comment(Optional.ofNullable(source.getComment()).orElse(""))
                .build();
    }
}
