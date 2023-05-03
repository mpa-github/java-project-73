package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.domain.dto.TaskRequestDTO;
import hexlet.code.domain.model.Label;
import hexlet.code.domain.model.Task;
import hexlet.code.domain.model.TaskStatus;
import hexlet.code.domain.model.User;
import hexlet.code.exception.NotFoundException;
import hexlet.code.exception.NotTheOwnerException;
import hexlet.code.repository.TaskRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskStatusService statusService;
    private final LabelService labelService;
    private final UserService userService;

    public TaskService(TaskRepository taskRepository,
                       TaskStatusService statusService,
                       LabelService labelService, UserService userService) {
        this.taskRepository = taskRepository;
        this.statusService = statusService;
        this.labelService = labelService;
        this.userService = userService;
    }

    public List<Task> findTasksByParams(Predicate predicate) {
        if (predicate == null) {
            return taskRepository.findAllByOrderByIdAsc();
        }
        return taskRepository.findAll(predicate, Sort.by(Sort.Direction.ASC, "id"));
    }

    public Task findTaskById(long id) {
        return taskRepository.findTaskById(id)
            .orElseThrow(() -> new NotFoundException("Task with id='%d' not found!".formatted(id)));
    }

    public Task createTask(TaskRequestDTO dto, UserDetails authDetails) {
        Task newTask = buildTask(dto, authDetails);
        return taskRepository.save(newTask);
    }

    public Task updateTask(long id, TaskRequestDTO dto, UserDetails authDetails) {
        Task existedTask = findTaskById(id);
        validateOwnerByEmail(existedTask.getAuthor().getEmail(), authDetails);
        Task taskToUpdate = buildTask(dto, authDetails);
        taskToUpdate.setId(id);
        return taskRepository.save(taskToUpdate);
    }

    public void deleteTask(long id, UserDetails authDetails) {
        Task existedTask = findTaskById(id);
        validateOwnerByEmail(existedTask.getAuthor().getEmail(), authDetails);
        taskRepository.delete(existedTask);
    }

    private Task buildTask(TaskRequestDTO dto, UserDetails authDetails) {
        String name = dto.getName();
        String description = dto.getDescription();
        TaskStatus status = statusService.findStatusById(dto.getTaskStatusId());
        User author = userService.findUserByEmail(authDetails.getUsername());
        User executor = userService.findUserById(dto.getExecutorId());
        List<Label> labels = labelService.findAllLabelsById(dto.getLabelIds());
        return new Task.Builder(name, status, author)
            .setDescription(description)
            .setExecutor(executor)
            .setLabels(labels)
            .createTask();
    }

    // TODO We can use @PreAuthorize instead
    private void validateOwnerByEmail(String userEmail, UserDetails authDetails) {
        String authenticatedEmail = authDetails.getUsername();
        if (!authenticatedEmail.equalsIgnoreCase(userEmail)) {
            throw new NotTheOwnerException("Access denied. For owner only!");
        }
    }
}
