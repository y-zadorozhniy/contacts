package dev.domain.contacts.controller;

import dev.domain.contacts.domain.User;
import dev.domain.contacts.dto.request.RecordChangeRequest;
import dev.domain.contacts.dto.response.ServerResponse;
import dev.domain.contacts.service.ChangeRequestService;
import dev.domain.contacts.service.RecordService;
import dev.domain.contacts.utils.SecurityHelper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;
import dev.domain.contacts.security.Role;

import java.security.Principal;

@Log4j2
@RestController
@AllArgsConstructor
public class RecordController {

    private final RecordService recordService;
    private final ChangeRequestService requestService;

    @ResponseBody
    @PostMapping("/api/record")
    public ServerResponse addRecord(@RequestBody RecordChangeRequest request, Principal principal) {
        User user = SecurityHelper.parseUserFromPrincipal(principal);
        ServerResponse response;
        if (StringUtils.equalsIgnoreCase(user.getRole(), Role.MODERATOR.name())) {
            response = recordService.addRecord(request);
            log.info("New record added by user {}", user.getUsername());
        } else {
            response = requestService.createNewRecordRequest(request, user);
        }
        return response;
    }

    @ResponseBody
    @PostMapping("/api/record/{recordId}")
    public ServerResponse editRecord(@PathVariable Long recordId,
                                     @RequestBody RecordChangeRequest request,
                                     Principal principal) {
        User user = SecurityHelper.parseUserFromPrincipal(principal);
        ServerResponse response;
        if (StringUtils.equalsIgnoreCase(user.getRole(), Role.MODERATOR.name())) {
            response = recordService.editRecord(recordId, request);
            log.info("Record with id {} was edited by {}", recordId, user.getUsername());
        } else {
            response = requestService.createEditRecordRequest(recordId, request, user);
        }
        return response;
    }

    @DeleteMapping("/api/record/{recordId}")
    public ServerResponse deleteRecord(@PathVariable Long recordId, Principal principal) {
        User user = SecurityHelper.parseUserFromPrincipal(principal);
        ServerResponse response;
        if (StringUtils.equalsIgnoreCase(user.getRole(), Role.MODERATOR.name())) {
            response = recordService.removePermanently(recordId);
            log.info("Record with id {} removed by user {}", recordId, user.getUsername());
        } else {
            response = requestService.createRemoveRecordRequest(recordId, user);
            log.info(String.format("Requested record %d remove by user %s", recordId,
                    user.getUsername()));
        }
        return response;
    }
}
