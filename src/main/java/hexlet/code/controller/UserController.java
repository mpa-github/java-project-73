package hexlet.code.controller;

import hexlet.code.domain.mapper.UserModelMapper;
import hexlet.code.domain.dto.UserRequestDTO;
import hexlet.code.domain.dto.UserResponseDTO;
import hexlet.code.domain.model.User;
import hexlet.code.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// TODO Validate dto field names (can be extra fields now)

@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final UserModelMapper userMapper;

    public UserController(UserService userService, UserModelMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping(path = "/users")
    public List<UserResponseDTO> findAllUsers() {
        Iterable<User> existedUsers = userService.findAllUsers();
        List<UserResponseDTO> userDTOList = new ArrayList<>();
        existedUsers.forEach(user -> userDTOList.add(userMapper.toUserResponseDTO(user)));
        userDTOList.sort(Comparator.comparing(UserResponseDTO::getId));
        return userDTOList;
    }

    @GetMapping(path = "/users/{id}")
    public UserResponseDTO findUserById(@PathVariable(name = "id") long id) {
        User existedUser = userService.findUserById(id);
        return userMapper.toUserResponseDTO(existedUser);
    }

    @PostMapping(path = "/users")
    public UserResponseDTO registerUser(@RequestBody @Valid UserRequestDTO dto) {
        User createdUser = userService.createUser(dto);
        return userMapper.toUserResponseDTO(createdUser);
    }

    @PutMapping(path = "/users/{id}")
    public UserResponseDTO updateUser(@RequestBody @Valid UserRequestDTO dto,
                                      @PathVariable(name = "id") long id,
                                      @AuthenticationPrincipal UserDetails authDetails) {
        User updatedUser = userService.updateUser(id, dto, authDetails);
        return userMapper.toUserResponseDTO(updatedUser);
    }

    @DeleteMapping(path = "/users/{id}")
    public void deleteUser(@PathVariable(name = "id") long id,
                           @AuthenticationPrincipal UserDetails authDetails) {
        userService.deleteUser(id, authDetails);
    }
}
