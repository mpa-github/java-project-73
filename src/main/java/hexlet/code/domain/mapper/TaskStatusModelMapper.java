package hexlet.code.domain.mapper;

import hexlet.code.domain.dto.TaskStatusRequestDTO;
import hexlet.code.domain.dto.TaskStatusResponseDTO;
import hexlet.code.domain.model.TaskStatus;
import org.springframework.stereotype.Component;

@Component
public class TaskStatusModelMapper {

    public TaskStatusResponseDTO toTaskStatusResponseDTO(TaskStatus status) {
        TaskStatusResponseDTO dto = new TaskStatusResponseDTO();
        dto.setId(status.getId());
        dto.setName(status.getName());
        dto.setCreatedAt(status.getCreatedAt());
        return dto;
    }

    public TaskStatus toTaskStatusModel(TaskStatusRequestDTO dto) {
        TaskStatus status = new TaskStatus();
        status.setName(dto.getName());
        return status;
    }

    public void updateStatusModel(TaskStatus status, TaskStatusRequestDTO dto) {
        status.setName(dto.getName());
    }
}
