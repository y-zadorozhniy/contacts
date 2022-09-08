package dev.domain.contacts.service;

import dev.domain.contacts.dto.UserInfo;
import dev.domain.contacts.dto.request.UserRequest;
import dev.domain.contacts.dto.response.ServerResponse;

import java.util.List;

public interface UserService {

    UserInfo getUser(Long userId);

    List<UserInfo> getUsers();

    ServerResponse addUser(UserRequest request);

    ServerResponse editUser(Long userId, UserRequest request);

    ServerResponse deleteUser(Long userId, Long authUserId);
}
