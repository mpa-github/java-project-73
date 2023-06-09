package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.domain.builder.TasksFactory;
import hexlet.code.domain.dto.TaskRequestDTO;
import hexlet.code.domain.model.Task;
import hexlet.code.exception.NotFoundException;
import hexlet.code.exception.NotTheOwnerException;
import hexlet.code.repository.TaskRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TaskService {

    private final TaskRepository taskRepository;
    private final TasksFactory tasksFactory;

    public TaskService(TaskRepository taskRepository, TasksFactory tasksFactory) {
        this.taskRepository = taskRepository;
        this.tasksFactory = tasksFactory;
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

    @Transactional
    public Task createTask(TaskRequestDTO dto, UserDetails authDetails) {
        Task newTask = tasksFactory.builder(new Task())
            .setName(dto.getName())
            .setDescription(dto.getDescription())
            .setTaskStatus(dto.getTaskStatusId())
            .setLabels(dto.getLabelIds())
            .setAuthor(authDetails.getUsername())
            .setExecutor(dto.getExecutorId())
            .build();

        return taskRepository.save(newTask);
    }

    @Transactional
    public Task updateTask(long id, TaskRequestDTO dto, UserDetails authDetails) {
        Task existedTask = findTaskById(id);
        validateOwnerByEmail(existedTask.getAuthor().getEmail(), authDetails);

        return tasksFactory.builder(existedTask)
            .setName(dto.getName())
            .setDescription(dto.getDescription())
            .setTaskStatus(dto.getTaskStatusId())
            .setLabels(dto.getLabelIds())
            .setExecutor(dto.getExecutorId())
            .build();
    }

    @Transactional
    public void deleteTask(long id, UserDetails authDetails) {
        Task existedTask = findTaskById(id);
        validateOwnerByEmail(existedTask.getAuthor().getEmail(), authDetails);
        taskRepository.delete(existedTask);
    }

    // TODO We can use @PreAuthorize instead
    private void validateOwnerByEmail(String userEmail, UserDetails authDetails) {
        String authenticatedEmail = authDetails.getUsername();
        if (!authenticatedEmail.equalsIgnoreCase(userEmail)) {
            throw new NotTheOwnerException("Access denied. For owner only!");
        }
    }
}
