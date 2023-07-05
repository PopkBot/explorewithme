package main.user.service;

import main.user.dto.GetUserListParamsDto;
import main.user.dto.UserDto;
import main.user.dto.UserInputDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserInputDto userInputDto);

    List<UserDto> getUsers(GetUserListParamsDto paramsDto);

    void deleteUser(Long id);
}
