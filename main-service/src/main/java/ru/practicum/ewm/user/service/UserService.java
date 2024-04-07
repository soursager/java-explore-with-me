package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.model.User;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest userRequest);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    User returnIfExists(Long userId);

    void checkExistingUser(Long userId);

    void deleteUser(long userId);
}
