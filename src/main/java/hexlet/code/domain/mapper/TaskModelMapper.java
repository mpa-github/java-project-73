package hexlet.code.domain.mapper;

import hexlet.code.domain.dto.TaskRequestDTO;
import hexlet.code.domain.dto.TaskResponseDTO;
import hexlet.code.domain.model.Task;
import hexlet.code.domain.model.TaskStatus;
import hexlet.code.domain.model.User;
import hexlet.code.service.TaskStatusService;
import hexlet.code.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class TaskModelMapper {

    private final TaskStatusModelMapper taskStatusModelMapper;
    private final TaskStatusService statusService;
    private final UserModelMapper userModelMapper;
    private final UserService userService;

    public TaskModelMapper(TaskStatusModelMapper taskStatusModelMapper,
                           TaskStatusService statusService,
                           UserModelMapper userModelMapper,
                           UserService userService) {
        this.taskStatusModelMapper = taskStatusModelMapper;
        this.statusService = statusService;
        this.userModelMapper = userModelMapper;
        this.userService = userService;
    }

    public TaskResponseDTO toTaskResponseDTO(Task task) {
        TaskResponseDTO dto = new TaskResponseDTO();
        dto.setId(task.getId());
        dto.setAuthor(userModelMapper.toUserResponseDTO(task.getAuthor()));
        dto.setExecutor(userModelMapper.toUserResponseDTO(task.getExecutor()));
        dto.setTaskStatus(taskStatusModelMapper.toTaskStatusResponseDTO(task.getTaskStatus()));
        dto.setName(task.getName());
        dto.setDescription(task.getDescription());
        dto.setCreatedAt(task.getCreatedAt());
        return dto;
    }

    public Task toTaskModel(TaskRequestDTO dto, UserDetails authDetails) {
        String authenticatedEmail = authDetails.getUsername();
        TaskStatus taskStatus = statusService.findStatusById(dto.getTaskStatusId());
        User author = userService.findUserByEmail(authenticatedEmail);
        User executor = userService.findUserById(dto.getExecutorId());

        Task task = new Task();
        task.setName(dto.getName());
        task.setDescription(dto.getDescription());
        task.setTaskStatus(taskStatus);
        task.setAuthor(author);
        task.setExecutor(executor);
        return task;
    }

    public void updateTaskModel(Task task, TaskRequestDTO dto, UserDetails authDetails) {
        String authenticatedEmail = authDetails.getUsername();
        TaskStatus taskStatus = statusService.findStatusById(dto.getTaskStatusId());
        User author = userService.findUserByEmail(authenticatedEmail);
        User executor = userService.findUserById(dto.getExecutorId());

        task.setName(dto.getName());
        task.setDescription(dto.getDescription());
        task.setTaskStatus(taskStatus);
        task.setAuthor(author);
        task.setExecutor(executor);
    }
}
