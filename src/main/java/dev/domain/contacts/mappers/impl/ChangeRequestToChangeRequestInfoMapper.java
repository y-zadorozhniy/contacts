package dev.domain.contacts.mappers.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.domain.contacts.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import dev.domain.contacts.domain.ChangeRequest;
import dev.domain.contacts.domain.Record;
import dev.domain.contacts.dto.ChangeRequestInfo;
import dev.domain.contacts.dto.internal.ChangeRequestType;
import dev.domain.contacts.dto.internal.OperationStatus;
import dev.domain.contacts.exceptions.ServerErrorException;

import java.time.format.DateTimeFormatter;

@Component
@RequiredArgsConstructor
public class ChangeRequestToChangeRequestInfoMapper implements Mapper<ChangeRequest, ChangeRequestInfo> {

    private final ObjectMapper objectMapper;

    @Override
    @SneakyThrows
    public ChangeRequestInfo map(ChangeRequest source) {
        Record contact;
        ChangeRequestType requestType = ChangeRequestType.from(source.getType());
        switch (requestType) {
            case CHANGE:
            case REMOVE:
                contact = objectMapper.readValue(source.getPreviousValue(), Record.class);
                break;
            case ADD:
                contact = objectMapper.readValue(source.getNewValue(), Record.class);
                break;
            default:
                throw new ServerErrorException("Неизвестный тип правок контакта");
        }
        return ChangeRequestInfo.builder()
                .id(source.getId())
                .recordId(contact.getId() == null ? "-" : contact.getId().toString())
                .contactName(contact.getFullName())
                .createdAt(source.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .userName(source.getUser().getUsername())
                .changeType(requestType.getValue())
                .status(OperationStatus.valueOf(source.getStatus()).getValue())
                .comment(source.getComment())
                .build();
    }
}
