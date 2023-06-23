package main.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import main.CustomPageRequest;
import main.exceptions.ObjectAlreadyExistsException;
import main.exceptions.ObjectNotFoundException;
import main.user.dto.GetUserListParamsDto;
import main.user.dto.UserDto;
import main.user.dto.UserInputDto;
import main.user.mapper.UserMapper;
import main.user.model.User;
import main.user.repository.UserRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImp implements UserService{

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(UserInputDto userInputDto) {
        if(userRepository.findByEmail(userInputDto.getEmail()).isPresent()){
            throw new ObjectAlreadyExistsException("Email is already taken");
        }
        User user = userRepository.save(userMapper.convertToUser(userInputDto));
        log.info("User has been created {}",user);
        return userMapper.convertToDto(user);
    }

    @Override
    public List<UserDto> getUsers(GetUserListParamsDto paramsDto) {
        Pageable page = new CustomPageRequest(paramsDto.getFrom(),paramsDto.getSize());
        List<UserDto> userDtos;
        if(paramsDto.getIds().size()!=0){
            userDtos = userRepository.findAllByIdIn(paramsDto.getIds(),page).getContent()
                    .stream().map(userMapper::convertToDto).collect(Collectors.toList());
        } else {
            userDtos = userRepository.findAll(page).getContent()
                    .stream().map(userMapper::convertToDto).collect(Collectors.toList());
        }
        log.info("Page of users has been returned {}",userDtos);
        return userDtos;

    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                ()-> new ObjectNotFoundException("User not found")
        );
        userRepository.delete(user);
        log.info("User has been deleted {}",user);
    }
}
