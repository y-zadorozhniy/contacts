package dev.domain.contacts.service;

import dev.domain.contacts.dto.ChangeRequestDetails;
import dev.domain.contacts.dto.request.RecordChangeRequest;
import dev.domain.contacts.dto.response.ServerResponse;
import dev.domain.contacts.domain.User;
import dev.domain.contacts.dto.ChangeRequestInfo;

import java.util.List;

public interface ChangeRequestService {

    ServerResponse createNewRecordRequest(RecordChangeRequest request, User user);

    ServerResponse createEditRecordRequest(Long recordId, RecordChangeRequest request, User user);

    ServerResponse createRemoveRecordRequest(Long recordId, User user);

    List<ChangeRequestInfo> getChangeRequests();

    List<ChangeRequestInfo> getChangeRequestsByStatus(String status);

    List<ChangeRequestInfo> getChangeRequestsByUser(Long userId);

    ChangeRequestDetails getChangeRequestDetails(Long requestId);

    ServerResponse declineRecordRequest(Long requestId, String comment);

    ServerResponse acceptRecordRequest(Long requestId);
}
