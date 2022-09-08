package dev.domain.contacts.dto;

import dev.domain.contacts.domain.Record;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
public class RecordInfo {

    private Long id;
    private String fullName;
    private String organization;
    private String occupation;
    private String country;
    private String city;
    private List<ContactInfo> contacts;
    private String tags;
    private String comment;

    public static RecordInfo toDto(Record source) {
        String tags = source.getTags()
                .stream()
                .map(tag -> String.format("#%s", tag.getName()))
                .collect(Collectors.joining(" "));
        return RecordInfo.builder()
                .id(source.getId())
                .fullName(source.getFullName())
                .country(getValueOrEmpty(source.getCountry()))
                .city(getValueOrEmpty(source.getCity()))
                .organization(getValueOrEmpty(source.getOrganization()))
                .occupation(getValueOrEmpty(source.getOccupation()))
                .contacts(source.getContacts()
                        .stream()
                        .map(ContactInfo::toDto)
                        .collect(Collectors.toList()))
                .tags(getValueOrEmpty(tags))
                .comment(getValueOrEmpty(source.getComment()))
                .build();
    }

    private static String getValueOrEmpty(String value) {
        return StringUtils.isBlank(value) ? "-" : value;
    }
}
