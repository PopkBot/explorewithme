package main.access;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.user.dto.GetUserListParamsDto;
import main.user.dto.UserDto;
import main.user.dto.UserInputDto;
import main.user.service.UserService;
import main.user.validator.UserCreate;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("/admin")
@Slf4j
@RequiredArgsConstructor
@Validated
public class AdminUser {

    private final UserService userService;

    @GetMapping("/users")
    @ResponseStatus(HttpStatus.OK)
    public List<UserDto> getListOfUsers(@RequestParam(defaultValue = "[]") List<Long> ids,
                                        @RequestParam(defaultValue = "0") Integer from,
                                        @RequestParam(defaultValue = "10") Integer size) {
        GetUserListParamsDto paramsDto = GetUserListParamsDto.builder()
                .ids(ids)
                .from(from)
                .size(size)
                .build();
        log.info("List of users is requested {}", paramsDto);
        return userService.getUsers(paramsDto);
    }

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@UserCreate @RequestBody UserInputDto userInputDto){
        log.info("Request for user creating {}",userInputDto);
        return userService.createUser(userInputDto);
    }
}
