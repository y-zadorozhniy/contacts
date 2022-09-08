package dev.domain.contacts.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import dev.domain.contacts.domain.User;
import dev.domain.contacts.dto.UserInfo;
import dev.domain.contacts.dto.internal.OperationStatus;
import dev.domain.contacts.dto.request.UserRequest;
import dev.domain.contacts.dto.response.ServerResponse;
import dev.domain.contacts.exceptions.NotFoundException;
import dev.domain.contacts.mappers.Mapper;
import dev.domain.contacts.repository.UserRepository;
import dev.domain.contacts.security.Role;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final Mapper<UserRequest, User> userRequestUserMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UserInfo> getUsers() {
        return userRepository.findAll().stream()
                .map(UserInfo::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserInfo getUser(Long userId) {
        return userRepository.findById(userId)
                .map(UserInfo::toDto)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    @Override
    public ServerResponse addUser(UserRequest request) {
        if (StringUtils.isAnyBlank(request.getUsername(),
                request.getEmail(),
                request.getRole(),
                request.getPassword())) {
            throw new IllegalArgumentException("Переданы не все обязательные поля");
        }
        if (!StringUtils.equals(request.getPassword(), request.getPasswordRepeat())) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }
        userRepository.save(userRequestUserMapper.map(request));
        return ServerResponse.builder()
                .message("Пользователь успешно добавлен")
                .status(OperationStatus.APPROVED.name())
                .build();
    }

    @Transactional
    @Override
    public ServerResponse deleteUser(Long userId, Long authUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        if(userId.equals(authUserId)) {
            throw new IllegalArgumentException("Нельзя удалить себя");
        }
        userRepository.deleteById(userId);
        return ServerResponse.builder()
                .message("Пользователь успешно удалён")
                .status(OperationStatus.APPROVED.name())
                .build();
    }

    @Override
    @Transactional
    public ServerResponse editUser(Long userId, UserRequest request) {
        if (StringUtils.isAnyBlank(request.getEmail(), request.getRole())) {
            throw new IllegalArgumentException("Переданы не все обязательные поля");
        }
        if (!StringUtils.equals(request.getPassword(), request.getPasswordRepeat())) {
            throw new IllegalArgumentException("Пароли не совпадают");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));
        user.setEmail(request.getEmail());
        if (!StringUtils.isBlank(request.getPassword())) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setRole(Role.from(request.getRole()).name());
        return ServerResponse.builder()
                .message("Пользователь успешно изменен")
                .status(OperationStatus.APPROVED.name())
                .build();
    }
}
