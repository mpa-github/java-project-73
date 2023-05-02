package hexlet.code.domain.mapper;

import hexlet.code.domain.dto.TaskStatusRequestDTO;
import hexlet.code.domain.dto.TaskStatusResponseDTO;
import hexlet.code.domain.model.TaskStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusModelMapper {

    public TaskStatusResponseDTO toTaskStatusResponseDTO(final TaskStatus status) {
        final TaskStatusResponseDTO dto = new TaskStatusResponseDTO();
        dto.setId(status.getId());
        dto.setName(status.getName());
        dto.setCreatedAt(status.getCreatedAt());
        return dto;
    }

    public TaskStatus toTaskStatusModel(final TaskStatusRequestDTO dto) {
        final TaskStatus status = new TaskStatus();
        updateStatusModel(status, dto);
        return status;
    }

    public void updateStatusModel(final TaskStatus status, final TaskStatusRequestDTO dto) {
        status.setName(dto.getName());
    }
}
