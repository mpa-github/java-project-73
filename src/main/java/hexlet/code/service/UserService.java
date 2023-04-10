package hexlet.code.service;

import hexlet.code.domain.dto.UserModelMapper;
import hexlet.code.domain.dto.UserRequestDTO;
import hexlet.code.domain.model.User;
import hexlet.code.exception.NotFoundInDatabaseException;
import hexlet.code.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserModelMapper userMapper;
    //private final PasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository, UserModelMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(long userId) {
        Optional<User> optionalUser = userRepository.findUserById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundInDatabaseException("User with id='%d' not found!".formatted(userId));
        }
        return optionalUser.get();
    }

    public User createUser(UserRequestDTO userDTO) {
        User newUser = userMapper.toUserModel(userDTO);
        return userRepository.save(newUser);
    }

    public User updateUser(UserRequestDTO userDTO, long userId) {
        User userToUpdate = getUserById(userId);
        userMapper.updateUserModel(userToUpdate, userDTO);
        return userRepository.save(userToUpdate);
    }

    public void deleteUser(long userId) {
        User existedUser = getUserById(userId);
        userRepository.delete(existedUser);
    }
}
