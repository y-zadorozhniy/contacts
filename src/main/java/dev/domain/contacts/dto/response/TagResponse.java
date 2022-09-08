package dev.domain.contacts.dto.response;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import dev.domain.contacts.domain.Tag;

@Data
@Builder(toBuilder = true)
@RequiredArgsConstructor
public class TagResponse {

    private final long id;
    private final String name;

    public static TagResponse toDto(Tag tag) {
        return TagResponse.builder()
                .id(tag.getId())
                .name(tag.getName())
                .build();
    }
}
