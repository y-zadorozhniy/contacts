package dev.domain.contacts.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.domain.contacts.domain.ChangeRequest;
import dev.domain.contacts.domain.Contact;
import dev.domain.contacts.domain.Record;
import dev.domain.contacts.domain.Tag;
import dev.domain.contacts.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import dev.domain.contacts.domain.*;
import dev.domain.contacts.dto.ChangeRequestInfo;
import dev.domain.contacts.dto.internal.ChangeRequestType;
import dev.domain.contacts.dto.internal.OperationStatus;
import dev.domain.contacts.dto.response.ServerResponse;
import dev.domain.contacts.exceptions.NotFoundException;
import dev.domain.contacts.mappers.impl.ChangeRequestToChangeRequestDetailsMapper;
import dev.domain.contacts.mappers.impl.ChangeRequestToChangeRequestInfoMapper;
import dev.domain.contacts.mappers.impl.RecordChangeRequestToRecordMapper;
import dev.domain.contacts.repository.ChangeRequestRepository;
import dev.domain.contacts.repository.RecordRepository;
import dev.domain.contacts.repository.TagRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChangeRequestServiceImplTest {

    private static final String CHANGE_REQUEST_NEW_VALUE =
            "{\"id\":null,\"fullName\":\"Крюков Денис\",\"organization\":\"Дом Творчества Переделкино\"," +
                    "\"occupation\":\"начальник библиотеки\",\"country\":\"Россия\",\"city\":\"Новая Москва\"," +
                    "\"comment\":null,\"contacts\":[{\"id\":null,\"value\":\"+79167777777\",\"comment\":\"\"," +
                    "\"type\":\"PHONE\"}],\"tags\":[{\"id\":null,\"name\":\"Переделкино\"},{\"id\":null," +
                    "\"name\":\"ДомТворчества\"},{\"id\":null,\"name\":\"Библиотека\"}]}";
    private static final String SAVED_RECORD_VALUE =
            "{\"id\":\"1\",\"fullName\":\"Крюков Денис\",\"organization\":\"Дом Творчества Переделкино\"," +
                    "\"occupation\":\"начальник библиотеки\",\"country\":\"Россия\",\"city\":\"Новая Москва\"," +
                    "\"comment\":null,\"contacts\":[{\"id\":\"1\",\"value\":\"+79167777777\",\"comment\":\"\"," +
                    "\"type\":\"PHONE\"}],\"tags\":[{\"id\":\"1\",\"name\":\"Переделкино\"},{\"id\":\"2\"," +
                    "\"name\":\"ДомТворчества\"},{\"id\":\"3\",\"name\":\"Библиотека\"}]}";
    private static final String CHANGE_REQUEST_CHANGED_VALUE =
            "{\"id\":\"1\",\"fullName\":\"Крюков Петр\",\"organization\":\"Дом Творчества Бутово\"," +
                    "\"occupation\":\"директор библиотеки\",\"country\":\"Молдавия\",\"city\":\"Старая Москва\"," +
                    "\"comment\":null,\"contacts\":[{\"id\":\"1\",\"value\":\"+79167777777\",\"comment\":\"\"," +
                    "\"type\":\"PHONE\"},{\"id\":null,\"value\":\"krykov.petr@mail.ru\",\"comment\":\"рабочий\"," +
                    "\"type\":\"EMAIL\"}],\"tags\":[{\"id\":\"1\",\"name\":\"Переделкино\"},{\"id\":null," +
                    "\"name\":\"Новый тег\"},{\"id\":\"3\",\"name\":\"Библиотека\"}]}";

    @Mock
    private ChangeRequestRepository changeRequestRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private RecordRepository recordRepository;

    private ChangeRequestService changeRequestService;

    @BeforeEach
    void setUp() {
        ObjectMapper objectMapper = new ObjectMapper();
        changeRequestService = new ChangeRequestServiceImpl(changeRequestRepository,
                recordRepository,
                tagRepository,
                objectMapper,
                new RecordChangeRequestToRecordMapper(tagRepository),
                new ChangeRequestToChangeRequestInfoMapper(objectMapper),
                new ChangeRequestToChangeRequestDetailsMapper(objectMapper));
    }

    @Test
    void declineRequestTest() {
        //arrange
        long changeRequestId = 1L;
        String comment = "Test comment";
        when(changeRequestRepository.findById(changeRequestId))
                .thenReturn(Optional.of(ChangeRequest.builder()
                        .build()));
        //act
        ServerResponse actual = changeRequestService.declineRecordRequest(changeRequestId, comment);
        //assert
        assertAll(() -> {
            verify(changeRequestRepository, times(1))
                    .findById(changeRequestId);
            assertThat(actual.getStatus())
                    .as("operation status should be DECLINED")
                    .isEqualTo(OperationStatus.DECLINED.name());
        });
    }

    @Test
    void declineRequestWithNullCommentTest() {
        //arrange
        long changeRequestId = 1L;
        String comment = null;
        when(changeRequestRepository.findById(changeRequestId))
                .thenReturn(Optional.of(ChangeRequest.builder()
                        .build()));
        //act
        ServerResponse actual = changeRequestService.declineRecordRequest(changeRequestId, comment);
        //assert
        assertAll(() -> {
            verify(changeRequestRepository, times(1))
                    .findById(changeRequestId);
            assertThat(actual.getStatus())
                    .as("operation status should be DECLINED")
                    .isEqualTo(OperationStatus.DECLINED.name());
        });
    }

    @Test
    void declineRequestTest_throwIfRequestNotFound() {
        //arrange
        long changeRequestId = 1L;
        String comment = "Test comment";
        when(changeRequestRepository.findById(changeRequestId))
                .thenReturn(Optional.empty());
        //act & assert
        assertThrows(NotFoundException.class, () ->
                        changeRequestService.declineRecordRequest(changeRequestId, comment),
                "should throw if no such change request by id");
    }

    @Test
    void approveRequestTest_requestTypeIsChange() {
        //arrange
        long changeRequestId = 1L;
        Record changedRecord = getChangedRecord();
        when(changeRequestRepository.findById(changeRequestId))
                .thenReturn(Optional.of(ChangeRequest.builder()
                        .type(ChangeRequestType.CHANGE.name())
                        .record(getSavedRecord())
                        .previousValue(SAVED_RECORD_VALUE)
                        .newValue(CHANGE_REQUEST_CHANGED_VALUE)
                        .build()));
        when(tagRepository.findById(any()))
                .thenReturn(Optional.of(Tag.builder().build()));
        when(tagRepository.findTagByName(any()))
                .thenReturn(Optional.empty());
        //act
        ServerResponse actual = changeRequestService.acceptRecordRequest(changeRequestId);
        //assert
        assertAll(() -> {
            verify(changeRequestRepository, times(1))
                    .findById(changeRequestId);
            verify(tagRepository, times(1))
                    .save(any(Tag.class));
            verify(recordRepository, times(1))
                    .save(refEq(changedRecord, "tags"));
            assertThat(actual.getStatus())
                    .as("operation status should be APPROVED")
                    .isEqualTo(OperationStatus.APPROVED.name());
        });
    }

    @Test
    void approveRequestTest_requestTypeIsAdd() {
        //arrange
        long changeRequestId = 1L;
        Record changedRecord = getNewRecord();
        when(changeRequestRepository.findById(changeRequestId))
                .thenReturn(Optional.of(ChangeRequest.builder()
                        .type(ChangeRequestType.ADD.name())
                        .newValue(CHANGE_REQUEST_NEW_VALUE)
                        .build()));
        //act
        ServerResponse actual = changeRequestService.acceptRecordRequest(changeRequestId);
        //assert
        assertAll(() -> {
            verify(changeRequestRepository, times(1))
                    .findById(changeRequestId);
            verify(tagRepository, times(3))
                    .save(any(Tag.class));
            verify(recordRepository, times(1))
                    .save(refEq(changedRecord, "tags"));
            assertThat(actual.getStatus())
                    .as("operation status should be APPROVED")
                    .isEqualTo(OperationStatus.APPROVED.name());
        });
    }

    @Test
    void approveRequestTest_requestTypeIsRemove() {
        //arrange
        long changeRequestId = 1L;
        Record record = Record.builder()
                .id(2L)
                .build();
        when(changeRequestRepository.findById(changeRequestId))
                .thenReturn(Optional.of(ChangeRequest.builder()
                        .type(ChangeRequestType.REMOVE.name())
                        .record(record)
                        .build()));
        //act
        ServerResponse actual = changeRequestService.acceptRecordRequest(changeRequestId);
        //assert
        assertAll(() -> {
            verify(changeRequestRepository, times(1))
                    .findById(changeRequestId);
            verify(recordRepository, times(1))
                    .delete(record);
            assertThat(actual.getStatus())
                    .as("operation status should be APPROVED")
                    .isEqualTo(OperationStatus.APPROVED.name());
        });
    }

    @Test
    void getChangeRequestsByStatusTest() {
        //arrange
        LocalDateTime createdAt = LocalDateTime.of(2020, 5, 1, 10, 10);
        ChangeRequest changeRequest = ChangeRequest.builder()
                .id(1L)
                .newValue(CHANGE_REQUEST_NEW_VALUE)
                .status(OperationStatus.PENDING.name())
                .type(ChangeRequestType.ADD.name())
                .user(User.builder()
                        .username("editor")
                        .build())
                .createdAt(createdAt)
                .build();
        ChangeRequestInfo expected = ChangeRequestInfo.builder()
                .id(changeRequest.getId())
                .userName(changeRequest.getUser().getUsername())
                .changeType(ChangeRequestType.ADD.getValue())
                .createdAt("2020-05-01 10:10:00")
                .recordId("-")
                .contactName("Крюков Денис")
                .status(OperationStatus.PENDING.getValue())
                .build();
        when(changeRequestRepository.findByStatus(OperationStatus.PENDING.name()))
                .thenReturn(List.of(changeRequest));
        //act
        List<ChangeRequestInfo> actual =
                changeRequestService.getChangeRequestsByStatus(OperationStatus.PENDING.name());
        //assert
        assertThat(actual)
                .as("should contains mapped change request info")
                .containsExactly(expected);
    }

    @Test
    void getChangeRequestsByStatusTest_unknownStatus() {
        assertThrows(RuntimeException.class, () -> changeRequestService.getChangeRequestsByStatus("unknown"),
                "status should be one of the OperationStatus enum");
    }

    private Record getNewRecord() {
        return Record.builder()
                .fullName("Крюков Денис")
                .organization("Дом Творчества Переделкино")
                .occupation("начальник библиотеки")
                .country("Россия")
                .city("Новая Москва")
                .contacts(List.of(new Contact(null, "+79161234567", "", "PHONE")))
                .tags(List.of(new Tag(null, "Переделкино"),
                        new Tag(null, "ДомТворчества"),
                        new Tag(null, "Библиотека")))
                .build();
    }

    private Record getSavedRecord() {
        return Record.builder()
                .id(1L)
                .fullName("Крюков Денис")
                .organization("Дом Творчества Переделкино")
                .occupation("начальник библиотеки")
                .country("Россия")
                .city("Новая Москва")
                .contacts(List.of(new Contact(1L, "+79161234567", "", "PHONE")))
                .tags(List.of(new Tag(1L, "Переделкино"),
                        new Tag(2L, "ДомТворчества"),
                        new Tag(3L, "Библиотека")))
                .build();
    }

    private Record getChangedRecord() {
        return Record.builder()
                .id(1L)
                .fullName("Крюков Петр")
                .organization("Дом Творчества Бутово")
                .occupation("директор библиотеки")
                .country("Молдавия")
                .city("Старая Москва")
                .contacts(List.of(new Contact(1L, "+79169876543", "", "PHONE"),
                        new Contact(null, "krykov.petr@mail.ru", "рабочий", "EMAIL")))
                .tags(List.of(new Tag(1L, "Переделкино"),
                        new Tag(null, "ДомТворчества"),
                        new Tag(3L, "Библиотека")))
                .build();
    }
}