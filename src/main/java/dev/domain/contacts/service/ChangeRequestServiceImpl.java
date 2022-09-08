package dev.domain.contacts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import dev.domain.contacts.domain.ChangeRequest;
import dev.domain.contacts.domain.Record;
import dev.domain.contacts.domain.Tag;
import dev.domain.contacts.domain.User;
import dev.domain.contacts.dto.ChangeRequestDetails;
import dev.domain.contacts.dto.ChangeRequestInfo;
import dev.domain.contacts.dto.internal.ChangeRequestType;
import dev.domain.contacts.dto.internal.OperationStatus;
import dev.domain.contacts.dto.request.RecordChangeRequest;
import dev.domain.contacts.dto.response.ServerResponse;
import dev.domain.contacts.exceptions.NotFoundException;
import dev.domain.contacts.exceptions.ServerErrorException;
import dev.domain.contacts.mappers.Mapper;
import dev.domain.contacts.repository.ChangeRequestRepository;
import dev.domain.contacts.repository.RecordRepository;
import dev.domain.contacts.repository.TagRepository;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static dev.domain.contacts.domain.Record.isContactsEquals;
import static dev.domain.contacts.domain.Record.isTagsEquals;

@Service
@RequiredArgsConstructor
public class ChangeRequestServiceImpl implements ChangeRequestService {

    private final ChangeRequestRepository changeRequestRepository;
    private final RecordRepository recordRepository;
    private final TagRepository tagRepository;
    private final ObjectMapper objectMapper;
    private final Mapper<RecordChangeRequest, Record> recordChangeRequestToRecordMapper;
    private final Mapper<ChangeRequest, ChangeRequestInfo> changeRequestToChangeRequestInfoMapper;
    private final Mapper<ChangeRequest, ChangeRequestDetails> changeRequestToChangeRequestDetailsMapper;

    @Override
    @SneakyThrows
    public ServerResponse createNewRecordRequest(RecordChangeRequest request, User user) {
        Record newRecord = recordChangeRequestToRecordMapper.map(request);
        ChangeRequest newRecordRequest = ChangeRequest.builder()
                .user(user)
                .newValue(objectMapper.writeValueAsString(newRecord))
                .type(ChangeRequestType.ADD.name())
                .status(OperationStatus.PENDING.name())
                .build();
        changeRequestRepository.save(newRecordRequest);
        return ServerResponse.builder()
                .message("Запрос на добавление записи успешно создан.")
                .status(OperationStatus.PENDING.name())
                .build();
    }

    @Override
    @SneakyThrows
    public ServerResponse createEditRecordRequest(Long recordId, RecordChangeRequest request, User user) {
        Record currentRecord = recordRepository.findById(recordId)
                .orElseThrow(() -> new NotFoundException("Запись с указанным идентификатором отсутствует"));
        Record changedRecord = recordChangeRequestToRecordMapper.map(request);
        ChangeRequest recordEditRequest = ChangeRequest.builder()
                .user(user)
                .record(currentRecord)
                .previousValue(objectMapper.writeValueAsString(currentRecord))
                .newValue(objectMapper.writeValueAsString(changedRecord))
                .type(ChangeRequestType.CHANGE.name())
                .status(OperationStatus.PENDING.name())
                .build();
        changeRequestRepository.save(recordEditRequest);
        return ServerResponse.builder()
                .message("Запрос на изменение записи успешно создан.")
                .status(OperationStatus.PENDING.name())
                .build();
    }

    @Override
    @SneakyThrows
    @Transactional
    public ServerResponse createRemoveRecordRequest(Long recordId, User user) {
        Record contact = recordRepository.findById(recordId)
                .orElseThrow(() -> new NotFoundException(String.format("Запись с id %d не найдена", recordId)));
        ChangeRequest removeRequest = ChangeRequest.builder()
                .type(ChangeRequestType.REMOVE.name())
                .record(contact)
                .user(user)
                .previousValue(objectMapper.writeValueAsString(contact))
                .status(OperationStatus.PENDING.name())
                .build();
        changeRequestRepository.save(removeRequest);
        return ServerResponse.builder()
                .message("Запрос на удаление принят")
                .status(OperationStatus.PENDING.name())
                .build();
    }

    @Override
    public List<ChangeRequestInfo> getChangeRequestsByStatus(String status) {
        OperationStatus operationStatus = OperationStatus.valueOf(status);
        return changeRequestRepository.findByStatus(operationStatus.name()).stream()
                .map(changeRequestToChangeRequestInfoMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChangeRequestInfo> getChangeRequests() {
        return changeRequestRepository.findAll().stream()
                .map(changeRequestToChangeRequestInfoMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public List<ChangeRequestInfo> getChangeRequestsByUser(Long userId) {
        return changeRequestRepository.findByUserId(userId).stream()
                .map(changeRequestToChangeRequestInfoMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public ChangeRequestDetails getChangeRequestDetails(Long requestId) {
        ChangeRequest changeRequest = getChangeRequestOrThrow(requestId);
        return changeRequestToChangeRequestDetailsMapper.map(changeRequest);
    }

    @Override
    @Transactional
    public ServerResponse declineRecordRequest(Long requestId, String comment) {
        ChangeRequest changeRequest = getChangeRequestOrThrow(requestId);
        changeRequest.setStatus(OperationStatus.DECLINED.name());
        changeRequest.setComment(comment);
        return ServerResponse.builder()
                .status(OperationStatus.DECLINED.name())
                .message("Правка успешно отклонена.")
                .build();
    }

    @Override
    @SneakyThrows
    @Transactional
    public ServerResponse acceptRecordRequest(Long requestId) {
        ChangeRequest changeRequest = getChangeRequestOrThrow(requestId);
        if (changeRequest.getType().equals(ChangeRequestType.REMOVE.name())) {
            Record recordToRemove = changeRequest.getRecord();
            changeRequest.setRecord(null);
            recordRepository.delete(recordToRemove);
        } else if (changeRequest.getType().equals(ChangeRequestType.ADD.name())) {
            Record newRecord = objectMapper.readValue(changeRequest.getNewValue(), Record.class);
            newRecord.setTags(getAttachedTags(newRecord.getTags()));
            recordRepository.save(newRecord);
        } else if (changeRequest.getType().equals(ChangeRequestType.CHANGE.name())) {
            Record sourceRecord = objectMapper.readValue(changeRequest.getPreviousValue(), Record.class);
            Record changedRecord = objectMapper.readValue(changeRequest.getNewValue(), Record.class);
            Record mergedRecord = mergeRecord(changeRequest.getRecord(), sourceRecord, changedRecord);
            mergedRecord.setTags(getAttachedTags(mergedRecord.getTags()));
            recordRepository.save(mergedRecord);
        } else {
            throw new ServerErrorException("Неизвестный тип правки");
        }
        changeRequest.setStatus(OperationStatus.APPROVED.name());
        return ServerResponse.builder()
                .status(OperationStatus.APPROVED.name())
                .message("Правка успешно применена.")
                .build();
    }

    private List<Tag> getAttachedTags(List<Tag> recordTags) {
        List<Tag> tags = new ArrayList<>();
        for (Tag tag : recordTags) {
            if (tag.getId() != null) {
                tags.add(tagRepository.findById(tag.getId())
                        .orElseGet(() -> tagRepository.save(tag)));
            } else {
                tags.add(tagRepository.findTagByName(tag.getName())
                        .orElseGet(() -> tagRepository.save(tag)));
            }
        }
        return tags;
    }

    private static Record mergeRecord(Record currentRecord, Record sourceRecord, Record changedRecord) {
        if (!StringUtils.equals(sourceRecord.getFullName(), changedRecord.getFullName())) {
            currentRecord.setFullName(changedRecord.getFullName());
        }
        if (!StringUtils.equals(sourceRecord.getOrganization(), changedRecord.getOrganization())) {
            currentRecord.setOrganization(changedRecord.getOrganization());
        }
        if (!StringUtils.equals(sourceRecord.getOccupation(), changedRecord.getOccupation())) {
            currentRecord.setOccupation(changedRecord.getOccupation());
        }
        if (!StringUtils.equals(sourceRecord.getCountry(), changedRecord.getCountry())) {
            currentRecord.setCountry(changedRecord.getCountry());
        }
        if (!StringUtils.equals(sourceRecord.getCity(), changedRecord.getCity())) {
            currentRecord.setCity(changedRecord.getCity());
        }
        if (!isContactsEquals(sourceRecord.getContacts(), changedRecord.getContacts())) {
            currentRecord.setContacts(changedRecord.getContacts());
        }
        if (!isTagsEquals(sourceRecord.getTags(), changedRecord.getTags())) {
            currentRecord.setTags(changedRecord.getTags());
        }
        if (!StringUtils.equals(sourceRecord.getComment(), changedRecord.getComment())) {
            currentRecord.setComment(changedRecord.getComment());
        }
        return currentRecord;
    }

    private ChangeRequest getChangeRequestOrThrow(Long requestId) {
        return changeRequestRepository.findById(requestId)
                .orElseThrow(() ->
                        new NotFoundException(String.format("Правка с id %d не найдена", requestId)));
    }
}
