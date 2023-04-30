package hexlet.code.domain.mapper;

import hexlet.code.domain.dto.LabelResponseDTO;
import hexlet.code.domain.dto.TaskRequestDTO;
import hexlet.code.domain.dto.TaskResponseDTO;
import hexlet.code.domain.model.Label;
import hexlet.code.domain.model.Task;
import hexlet.code.domain.model.TaskStatus;
import hexlet.code.domain.model.User;
import hexlet.code.service.LabelService;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class TaskModelMapper {

    private final TaskStatusModelMapper taskStatusMapper;
    private final TaskStatusService statusService;
    private final LabelModelMapper labelMapper;
    private final LabelService labelService;
    private final UserModelMapper userMapper;
    private final UserService userService;

    public TaskModelMapper(TaskStatusModelMapper taskStatusMapper,
                           TaskStatusService statusService,
                           LabelModelMapper labelMapper,
                           LabelService labelService,
                           UserModelMapper userMapper,
                           UserService userService) {
        this.taskStatusMapper = taskStatusMapper;
        this.statusService = statusService;
        this.labelMapper = labelMapper;
        this.labelService = labelService;
        this.userMapper = userMapper;
        this.userService = userService;
    }

    // TODO find better way for mapping (!)
    public TaskResponseDTO toTaskResponseDTO(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();
        if (task.getExecutor() != null) {
            dto.setExecutor(userMapper.toUserResponseDTO(task.getExecutor()));
        }
        if (task.getLabels() != null) {
            List<LabelResponseDTO> labelsDTO = new ArrayList<>();
            task.getLabels().forEach(label -> labelsDTO.add(labelMapper.toLabelResponseDTO(label)));
            labelsDTO.sort(Comparator.comparing(LabelResponseDTO::getId));
            dto.setLabels(labelsDTO);
        }
        if (task.getDescription() != null) {
            dto.setDescription(task.getDescription());
        }

        dto.setId(task.getId());
        dto.setAuthor(userMapper.toUserResponseDTO(task.getAuthor()));
        dto.setTaskStatus(taskStatusMapper.toTaskStatusResponseDTO(task.getTaskStatus()));
        dto.setName(task.getName());
        dto.setCreatedAt(task.getCreatedAt());
        return dto;
    }

    public Task toTaskModel(TaskRequestDTO dto, UserDetails authDetails) {
        Task task = new Task();
        updateTaskModel(task, dto, authDetails);
        return task;
    }

    // TODO find better way for mapping (!)
    // TODO Use JPA ModelReferences for request to DB optimization
    public void updateTaskModel(Task task, TaskRequestDTO dto, UserDetails authDetails) {
        if (dto.getDescription() != null) {
            task.setDescription(dto.getDescription());
        }
        if (dto.getExecutorId() != null) {
            User executor = userService.findUserById(dto.getExecutorId());
            //User executor = userService.getUserReferenceById(dto.getExecutorId());
            task.setExecutor(executor);
        }
        if (dto.getLabelIds() != null) {
            List<Label> labels = labelService.findAllLabelsById(dto.getLabelIds());
            //List<Label> labels = labelService.getAllLabelReferencesById(dto.getLabelIds());
            task.setLabels(labels);
        }

        TaskStatus taskStatus = statusService.findStatusById(dto.getTaskStatusId());
        //TaskStatus taskStatus = statusService.getStatusReferenceById(dto.getTaskStatusId());
        String authenticatedEmail = authDetails.getUsername();
        User author = userService.findUserByEmail(authenticatedEmail);

        task.setName(dto.getName());
        task.setTaskStatus(taskStatus);
        task.setAuthor(author);
    }
}
