package dev.domain.contacts.dto;

import dev.domain.contacts.domain.Record;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class RecordDetails {

    private Long id;
    private String fullName;

    public static RecordDetails toDto(Record source) {
        return RecordDetails.builder()
                .id(source.getId())
                .fullName(source.getFullName())
                .build();
    }
}
