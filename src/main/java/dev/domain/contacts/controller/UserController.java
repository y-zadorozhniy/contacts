package dev.domain.contacts.controller;

import dev.domain.contacts.domain.User;
import dev.domain.contacts.dto.request.UserRequest;
import dev.domain.contacts.dto.response.ServerResponse;
import dev.domain.contacts.service.UserService;
import dev.domain.contacts.utils.SecurityHelper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Log4j2
@RestController
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @ResponseBody
    @PostMapping("/api/user")
    public ServerResponse addUser(@RequestBody UserRequest request, Principal principal) {
        User user = SecurityHelper.parseUserFromPrincipal(principal);
        ServerResponse response = userService.addUser(request);
        log.info("New user added by user {}", user.getUsername());
        return response;
    }

    @ResponseBody
    @PostMapping("/api/user/{userId}")
    public ServerResponse editUser(@PathVariable Long userId,
                                  @RequestBody UserRequest request,
                                  Principal principal) {
        User user = SecurityHelper.parseUserFromPrincipal(principal);
        ServerResponse response = userService.editUser(userId, request);
        log.info("User with id {} was edited by user {}", userId, user.getUsername());
        return response;
    }

    @ResponseBody
    @DeleteMapping("/api/user/{userId}")
    public ServerResponse deleteUser(@PathVariable Long userId, Principal principal) {
        User user = SecurityHelper.parseUserFromPrincipal(principal);
        Long authUserId = user.getId();
        ServerResponse response = userService.deleteUser(userId, authUserId);
        log.info("User with id {} was deleted by user {}", userId, user.getUsername());
        return response;
    }
}
