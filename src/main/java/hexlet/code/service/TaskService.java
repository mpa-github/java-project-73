package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.domain.dto.TaskRequestDTO;
import hexlet.code.domain.mapper.TaskModelMapper;
import hexlet.code.domain.model.Task;
import hexlet.code.exception.NotFoundException;
import hexlet.code.exception.NotTheOwnerException;
import hexlet.code.repository.TaskRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskModelMapper taskMapper;

    public TaskService(TaskRepository taskRepository, TaskModelMapper taskMapper) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
    }

    public Iterable<Task> findTasksByParams(Predicate predicate) {
        if (predicate == null) {
            return taskRepository.findAll();
        }
        return taskRepository.findAll(predicate);
    }

    public Task findTaskById(long id) {
        return taskRepository.findTaskById(id)
            .orElseThrow(() -> new NotFoundException("Task with id='%d' not found!".formatted(id)));
    }

    public Task createTask(TaskRequestDTO dto, UserDetails authDetails) {
        Task newTask = taskMapper.toTaskModel(dto, authDetails);
        return taskRepository.save(newTask);
    }

    // TODO Only owner?
    public Task updateTask(long id, TaskRequestDTO dto, UserDetails authDetails) {
        Task taskToUpdate = findTaskById(id);
        // TODO Better update only differences (?)
        taskMapper.updateTaskModel(taskToUpdate, dto, authDetails);
        return taskRepository.save(taskToUpdate);
    }

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
