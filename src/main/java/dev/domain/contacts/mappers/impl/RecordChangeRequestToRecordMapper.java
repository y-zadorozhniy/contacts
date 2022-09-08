package dev.domain.contacts.mappers.impl;

import dev.domain.contacts.domain.Tag;
import dev.domain.contacts.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import dev.domain.contacts.domain.Contact;
import dev.domain.contacts.domain.Record;
import dev.domain.contacts.dto.internal.ContactType;
import dev.domain.contacts.dto.request.ContactRequest;
import dev.domain.contacts.dto.request.RecordChangeRequest;
import dev.domain.contacts.repository.TagRepository;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RecordChangeRequestToRecordMapper implements Mapper<RecordChangeRequest, Record> {

    private final TagRepository tagRepository;

    @Override
    public Record map(RecordChangeRequest source) {
        if (StringUtils.isBlank(source.getFullName())) {
            throw new IllegalArgumentException("Не передано имя контакта");
        }
        Map<String, Tag> tags = tagRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Tag::getName, Function.identity()));
        List<Tag> recordTags = source.getTags()
                .stream()
                .filter(StringUtils::isNotBlank)
                .map(tag -> Optional.ofNullable(tags.get(tag))
                        .orElse(new Tag(null, tag)))
                .collect(Collectors.toList());
        List<Contact> contacts = source.getContacts()
                .stream()
                .map(RecordChangeRequestToRecordMapper::mapContact)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return Record.builder()
                .fullName(source.getFullName().trim())
                .organization(getNonEmptyOrNull(source.getCompany()))
                .occupation(getNonEmptyOrNull(source.getOccupation()))
                .country(getNonEmptyOrNull(source.getCountry()))
                .city(getNonEmptyOrNull(source.getCity()))
                .contacts(contacts)
                .tags(recordTags)
                .comment(getNonEmptyOrNull(source.getComment()))
                .build();
    }

    private static Contact mapContact(ContactRequest source) {
        if (StringUtils.isAnyBlank(source.getValue(), source.getType())) {
            return null;
        }
        return Contact.builder()
                .type(ContactType.from(source.getType()).name())
                .value(source.getValue())
                .comment(source.getComment())
                .build();
    }

    private static String getNonEmptyOrNull(String source) {
        return StringUtils.isBlank(source) ? null : source.trim();
    }
}
