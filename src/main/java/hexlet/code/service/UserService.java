package hexlet.code.service;

import hexlet.code.domain.dto.UserModelMapper;
import hexlet.code.domain.dto.UserRequestDTO;
import hexlet.code.domain.model.User;
import hexlet.code.exception.NotTheOwnerException;
import hexlet.code.exception.NotFoundException;
import hexlet.code.exception.UserAlreadyExistException;
import hexlet.code.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserModelMapper userMapper;
    private final PasswordEncoder bCryptPasswordEncoder;

    public UserService(UserRepository userRepository,
                       UserModelMapper userMapper,
                       PasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(long id) {
        return userRepository.findUserById(id)
            .orElseThrow(() -> new NotFoundException("User with id='%d' not found!".formatted(id)));
    }

    public User createUser(UserRequestDTO dto) {
        if (userRepository.existsUserByEmailIgnoreCase(dto.getEmail())) {
            throw new UserAlreadyExistException("User already exists!");
        }
        User newUser = userMapper.toUserModel(dto);
        encodePassword(newUser);
        return userRepository.save(newUser);
    }

    public User updateUser(long userId, UserRequestDTO dto, UserDetails userDetails) {
        // TODO Can we update email (Spring Username)?
        // TODO generate new token after update?
        User userToUpdate = getUserById(userId);
        validateOwnerByEmail(userToUpdate.getEmail(), userDetails);
        userMapper.updateUserModel(userToUpdate, dto);
        encodePassword(userToUpdate);
        return userRepository.save(userToUpdate);
    }

    public void deleteUser(long id, UserDetails userDetails) {
        User existedUser = getUserById(id);
        validateOwnerByEmail(existedUser.getEmail(), userDetails);
        userRepository.delete(existedUser);
    }

    private void encodePassword(User user) {
        String userPassword = user.getPassword();
        String encodedRow = bCryptPasswordEncoder.encode(userPassword);
        user.setPassword(encodedRow);
    }

    // TODO We can use @PreAuthorize instead
    private void validateOwnerByEmail(String userEmail, UserDetails userDetails) {
        String authenticatedEmail = userDetails.getUsername();
        if (!authenticatedEmail.equalsIgnoreCase(userEmail)) {
            throw new NotTheOwnerException("Access denied. For owner only!");
        }
    }
}
