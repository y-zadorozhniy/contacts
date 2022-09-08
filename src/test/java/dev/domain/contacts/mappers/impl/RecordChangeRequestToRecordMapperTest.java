package dev.domain.contacts.mappers.impl;

import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import dev.domain.contacts.domain.Record;
import dev.domain.contacts.domain.Tag;
import dev.domain.contacts.dto.request.RecordChangeRequest;
import dev.domain.contacts.mappers.Mapper;
import dev.domain.contacts.repository.TagRepository;

import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecordChangeRequestToRecordMapperTest {

    @Mock
    private TagRepository tagRepository;

    private Mapper<RecordChangeRequest, Record> recordMapper;

    @BeforeEach
    void setUp() {
        recordMapper = new RecordChangeRequestToRecordMapper(tagRepository);
    }

    @Test
    void mapTest_savedAndNewTag() {
        //arrange
        RecordChangeRequest request = RecordChangeRequest.builder()
                .fullName("Петров Павел Павлович")
                .tags(List.of("Тэг 1", "Тэг 3"))
                .contacts(List.of())
                .build();
        when(tagRepository.findAll())
                .thenReturn(buildSavedTags());
        //act
        Record actual = recordMapper.map(request);
        //assert
        Condition<Tag> tagWithId = new Condition<>(tag -> Objects.nonNull(tag.getId()),
                "one tag should be mapped from database");
        Condition<Tag> newTag = new Condition<>(tag -> Objects.isNull(tag.getId()),
                "one tag must be without id");
        assertThat(actual.getTags())
                .as("Should have both saved and new tags")
                .hasSize(2)
                .areExactly(1, tagWithId)
                .areExactly(1, newTag);
    }

    @Test
    void mapTest_throwIfEmptyFullName() {
        //arrange
        RecordChangeRequest request = RecordChangeRequest.builder()
                .build();
        //act & assert
        Assertions.assertThrows(IllegalArgumentException.class, () -> recordMapper.map(request),
                "full name is mandatory field");
    }

    private List<Tag> buildSavedTags() {
        return List.of(Tag.builder()
                        .id(1L)
                        .name("Тэг 1")
                        .build(),
                Tag.builder()
                        .id(2L)
                        .name("Тэг 2")
                        .build());
    }
}