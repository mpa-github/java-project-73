package hexlet.code.domain.mapper;

import hexlet.code.domain.dto.TaskStatusRequestDTO;
import hexlet.code.domain.dto.TaskStatusResponseDTO;
import hexlet.code.domain.model.TaskStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusModelMapper {

    public TaskStatusResponseDTO toTaskStatusResponseDTO(final TaskStatus status) {
        return new TaskStatusResponseDTO(
            status.getId(),
            status.getName(),
            status.getCreatedAt()
        );
    }

    public TaskStatus toTaskStatusModel(final TaskStatusRequestDTO dto) {
        return new TaskStatus(
            dto.getName()
        );
    }
}
