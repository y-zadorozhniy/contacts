package dev.domain.contacts.mappers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.domain.contacts.domain.Tag;
import dev.domain.contacts.dto.ChangeRequestDetails;
import dev.domain.contacts.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import dev.domain.contacts.domain.ChangeRequest;
import dev.domain.contacts.domain.Contact;
import dev.domain.contacts.domain.Record;
import dev.domain.contacts.dto.ChangeRequestItem;
import dev.domain.contacts.dto.internal.ChangeRequestType;
import dev.domain.contacts.dto.internal.ContactType;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChangeRequestToChangeRequestDetailsMapper implements Mapper<ChangeRequest, ChangeRequestDetails> {

    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public ChangeRequestDetails map(ChangeRequest source) {
        ChangeRequestType requestType = ChangeRequestType.from(source.getType());
        Record previousValue = StringUtils.isBlank(source.getPreviousValue())
                ? new Record()
                : objectMapper.readValue(source.getPreviousValue(), Record.class);
        Record newValue = StringUtils.isBlank(source.getNewValue())
                ? new Record()
                : objectMapper.readValue(source.getNewValue(), Record.class);
        String previousContacts = mapContacts(previousValue.getContacts());
        String newContacts = mapContacts(newValue.getContacts());
        String previousTags = mapTags(previousValue.getTags());
        String newTags = mapTags(newValue.getTags());
        List<ChangeRequestItem> changes = new ArrayList<>();
        changes.add(new ChangeRequestItem("Полное имя", mapValue(previousValue.getFullName()),
                mapValue(newValue.getFullName())));
        changes.add(new ChangeRequestItem("Компания", mapValue(previousValue.getOrganization()),
                mapValue(newValue.getOrganization())));
        changes.add(new ChangeRequestItem("Должность", mapValue(previousValue.getOccupation()),
                mapValue(newValue.getOccupation())));
        changes.add(new ChangeRequestItem("Страна", mapValue(previousValue.getCountry()),
                mapValue(newValue.getCountry())));
        changes.add(new ChangeRequestItem("Город", mapValue(previousValue.getCity()), mapValue(newValue.getCity())));
        changes.add(new ChangeRequestItem("Контакты", previousContacts, newContacts));
        changes.add(new ChangeRequestItem("Тэги", previousTags, newTags));
        changes.add(new ChangeRequestItem("Комментарий", mapValue(previousValue.getComment()),
                mapValue(newValue.getComment())));
        return ChangeRequestDetails.builder()
                .id(source.getId())
                .type(requestType)
                .changes(changes)
                .build();
    }

    private static String mapValue(String value) {
        if(StringUtils.isBlank(value)) {
            return "-";
        }
        return value;
    }

    private static String mapContacts(List<Contact> contacts) {
        if(contacts == null || contacts.isEmpty()) {
            return "-";
        }
        return contacts.stream()
                .map(contact -> String.format("%s %s %s", ContactType.from(contact.getType()).getValue(),
                        contact.getValue(),
                        contact.getComment()))
                .collect(Collectors.joining(" | "));
    }

    private static String mapTags(List<Tag> tags) {
        if(tags == null || tags.isEmpty()) {
            return "-";
        }
        return tags.stream()
                .map(tag -> String.format("#%s", tag.getName()))
                .collect(Collectors.joining(" | "));
    }
}
