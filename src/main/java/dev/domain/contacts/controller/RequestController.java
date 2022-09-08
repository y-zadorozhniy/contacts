package dev.domain.contacts.controller;

import dev.domain.contacts.dto.request.ChangeRequestDeclineRequest;
import dev.domain.contacts.dto.response.ServerResponse;
import dev.domain.contacts.service.ChangeRequestService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@AllArgsConstructor
public class RequestController {

    private final ChangeRequestService requestService;

    @PutMapping("/api/request/{requestId}")
    public ServerResponse declineRequest(@PathVariable Long requestId,
                                         @RequestBody ChangeRequestDeclineRequest changeRequestDeclineRequest) {
        return requestService.declineRecordRequest(requestId, changeRequestDeclineRequest.getComment());
    }

    @PostMapping("/api/request/{requestId}/accept")
    public ServerResponse acceptRequest(@PathVariable Long requestId) {
        return requestService.acceptRecordRequest(requestId);
    }
}
