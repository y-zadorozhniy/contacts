package dev.domain.contacts.service;

import dev.domain.contacts.domain.Tag;
import dev.domain.contacts.dto.response.ServerResponse;
import dev.domain.contacts.mappers.Mapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.domain.contacts.domain.Record;
import dev.domain.contacts.dto.RecordEditInfo;
import dev.domain.contacts.dto.RecordInfo;
import dev.domain.contacts.dto.internal.OperationStatus;
import dev.domain.contacts.dto.request.RecordChangeRequest;
import dev.domain.contacts.exceptions.NotFoundException;
import dev.domain.contacts.repository.RecordRepository;
import dev.domain.contacts.repository.TagRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecordServiceImpl implements RecordService {

    private final RecordRepository recordRepository;
    private final TagRepository tagRepository;
    private final Mapper<RecordChangeRequest, Record> recordChangeRequestToRecordMapper;

    @Value("${app.settings.records-per-page-count:10000}")
    private int recordsPerPageCount;

    @Override
    public List<RecordInfo> getRecordsWithOffset(int page) {
        int firstRecordIndex = page * recordsPerPageCount;
        return recordRepository.findAll(PageRequest.of(firstRecordIndex, recordsPerPageCount))
                .map(RecordInfo::toDto)
                .stream()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ServerResponse addRecord(RecordChangeRequest request) {
        Record newRecord = recordChangeRequestToRecordMapper.map(request);
        List<Tag> tags =
                newRecord.getTags().stream()
                        .map(tag -> tag.getId() != null ? tag : tagRepository.save(tag))
                        .collect(Collectors.toList());
        newRecord.setTags(tags);
        recordRepository.save(newRecord);
        return ServerResponse.builder()
                .status(OperationStatus.APPROVED.name())
                .message("Запись успешно создана")
                .build();
    }

    @Override
    @Transactional
    public ServerResponse removePermanently(Long recordId) {
        checkIfExists(recordId);
        recordRepository.deleteById(recordId);
        return ServerResponse.builder()
                .status(OperationStatus.APPROVED.name())
                .message("Запись успешно удалена")
                .build();
    }

    @Override
    public RecordEditInfo getRecordForEdit(Long recordId) {
        return RecordEditInfo.toDto(getRecordOrThrow(recordId));
    }

    @Override
    @Transactional
    public ServerResponse editRecord(Long recordId, RecordChangeRequest request) {
        checkIfExists(recordId);
        Record changedRecord = recordChangeRequestToRecordMapper.map(request);
        changedRecord.setId(recordId);
        Map<String, Tag> tags = tagRepository.findAll()
                .stream()
                .collect(Collectors.toMap(Tag::getName, Function.identity()));
        for (int i = 0; i < changedRecord.getTags().size(); i++) {
            Tag newTag = changedRecord.getTags().get(i);
            changedRecord.getTags()
                    .set(i, Optional.ofNullable(tags.get(newTag.getName()))
                            .orElse(tagRepository.save(newTag)));
        }
        recordRepository.save(changedRecord);
        return ServerResponse.builder()
                .status(OperationStatus.APPROVED.name())
                .message("Запись успешно обновлена")
                .build();
    }

    private void checkIfExists(Long recordId) {
        if (!recordRepository.existsById(recordId)) {
            throw new NotFoundException("Запись с указанным идентификатором отсутствует");
        }
    }

    private Record getRecordOrThrow(Long recordId) {
        return recordRepository.findById(recordId)
                .orElseThrow(() -> new NotFoundException("Запись с указанным идентификатором отсутствует"));
    }
}
