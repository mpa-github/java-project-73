package hexlet.code.domain.mapper;

import hexlet.code.domain.dto.UserRequestDTO;
import hexlet.code.domain.dto.UserResponseDTO;
import hexlet.code.domain.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserModelMapper {

    public UserResponseDTO toUserResponseDTO(final User user) {
        final UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }

    public User toUserModel(final UserRequestDTO dto) {
        final User user = new User();
        updateUserModel(user, dto);
        return user;
    }

    public void updateUserModel(final User user, final UserRequestDTO dto) {
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setEmail(dto.getEmail().toLowerCase());
        user.setPassword(dto.getPassword());
    }
}
