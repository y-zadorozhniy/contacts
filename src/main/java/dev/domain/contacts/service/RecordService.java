package dev.domain.contacts.service;

import dev.domain.contacts.dto.RecordEditInfo;
import dev.domain.contacts.dto.RecordInfo;
import dev.domain.contacts.dto.request.RecordChangeRequest;
import dev.domain.contacts.dto.response.ServerResponse;

import java.util.List;

public interface RecordService {

    List<RecordInfo> getRecordsWithOffset(int page);

    ServerResponse addRecord(RecordChangeRequest request);

    ServerResponse removePermanently(Long recordId);

    RecordEditInfo getRecordForEdit(Long recordId);

    ServerResponse editRecord(Long recordId, RecordChangeRequest request);
}
