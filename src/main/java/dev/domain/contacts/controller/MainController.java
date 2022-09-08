package dev.domain.contacts.controller;

import dev.domain.contacts.domain.User;
import dev.domain.contacts.dto.ChangeRequestDetails;
import dev.domain.contacts.dto.RecordEditInfo;
import dev.domain.contacts.dto.internal.ContactType;
import dev.domain.contacts.dto.internal.OperationStatus;
import dev.domain.contacts.service.ChangeRequestService;
import dev.domain.contacts.service.RecordService;
import dev.domain.contacts.service.UserService;
import dev.domain.contacts.utils.SecurityHelper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import dev.domain.contacts.security.Role;

import java.security.Principal;

@Log4j2
@Controller
@AllArgsConstructor
public class MainController {

    private final RecordService recordService;
    private final ChangeRequestService requestService;
    private final UserService userService;

    @GetMapping("/")
    public String listRecords(@RequestParam(value = "offset", defaultValue = "0") int page,
                              Model model,
                              Principal principal) {
        User user = SecurityHelper.parseUserFromPrincipal(principal);
        model.addAttribute("user", user);
        model.addAttribute("records", recordService.getRecordsWithOffset(page));
        return "contacts";
    }

    @GetMapping("/requests")
    public String listRecordChanges(Model model,
                                    Principal principal) {
        User user = SecurityHelper.parseUserFromPrincipal(principal);
        model.addAttribute("user", user);
        model.addAttribute("requests", requestService.getChangeRequestsByStatus(OperationStatus.PENDING.name()));
        return "change_requests";
    }

    @GetMapping("/user/requests")
    public String listUserRequests(Model model, Principal principal) {
        User user = SecurityHelper.parseUserFromPrincipal(principal);
        model.addAttribute("user", user);
        model.addAttribute("user_requests", requestService.getChangeRequestsByUser(user.getId()));
        return "user_requests";
    }

    @GetMapping("/users")
    public String listUsers(Model model, Principal principal) {
        User user = SecurityHelper.parseUserFromPrincipal(principal);
        model.addAttribute("user", user);
        model.addAttribute("users", userService.getUsers());
        return "users";
    }

    @GetMapping("/user/add")
    public String addUser(Model model, Principal principal) {
        User user = SecurityHelper.parseUserFromPrincipal(principal);
        model.addAttribute("user", user);
        model.addAttribute("userRoles", Role.values());
        return "user_add";
    }

    @GetMapping("/user/{userId}")
    public String editUser(@PathVariable Long userId,Model model, Principal principal) {
        User user = SecurityHelper.parseUserFromPrincipal(principal);
        model.addAttribute("user", user);
        model.addAttribute("userRoles", Role.values());
        model.addAttribute("userToEdit", userService.getUser(userId));
        return "user_edit";
    }

    @GetMapping("/record/add")
    public String addRecord(Model model, Principal principal) {
        User user = SecurityHelper.parseUserFromPrincipal(principal);
        model.addAttribute("user", user);
        model.addAttribute("contactTypes", ContactType.values());
        return "contact_add";
    }

    @GetMapping("/record/{recordId}")
    public String editRecord(@PathVariable Long recordId, Model model, Principal principal) {
        User user = SecurityHelper.parseUserFromPrincipal(principal);
        RecordEditInfo contact = recordService.getRecordForEdit(recordId);
        model.addAttribute("user", user);
        model.addAttribute("contactTypes", ContactType.values());
        model.addAttribute("record", contact);
        return "contact_edit";
    }

    @GetMapping("/request/{requestId}")
    public String requestDetails(@PathVariable Long requestId, Model model, Principal principal) {
        User user = SecurityHelper.parseUserFromPrincipal(principal);
        ChangeRequestDetails changeRequestDetails = requestService.getChangeRequestDetails(requestId);
        model.addAttribute("user", user);
        model.addAttribute("request", changeRequestDetails);
        return "change_request_details";
    }
}
