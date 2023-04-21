package hexlet.code.controller;

import hexlet.code.domain.dto.TaskRequestDTO;
import hexlet.code.domain.dto.TaskResponseDTO;
import hexlet.code.domain.mapper.TaskModelMapper;
import hexlet.code.domain.model.Task;
import hexlet.code.service.TaskService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api")
public class TaskController {

    private final TaskService taskService;
    private final TaskModelMapper taskMapper;

    public TaskController(TaskService taskService, TaskModelMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @GetMapping(path = "/tasks")
    public List<TaskResponseDTO> findAllTasks() {
        Iterable<Task> existedTasks = taskService.findAllTasks();
        List<TaskResponseDTO> taskDTOList = new ArrayList<>();
        existedTasks.forEach(task -> taskDTOList.add(taskMapper.toTaskResponseDTO(task)));
        taskDTOList.sort(Comparator.comparing(TaskResponseDTO::getId));
        return taskDTOList;
    }

    @GetMapping(path = "/tasks/{id}")
    public TaskResponseDTO findTaskById(@PathVariable(name = "id") long id) {
        Task existedTask = taskService.findTaskById(id);
        return taskMapper.toTaskResponseDTO(existedTask);
    }

    @PostMapping(path = "/tasks")
    public TaskResponseDTO createTask(@RequestBody @Valid TaskRequestDTO dto,
                                      @AuthenticationPrincipal UserDetails authDetails) {
        Task createdTask = taskService.createTask(dto, authDetails);
        return taskMapper.toTaskResponseDTO(createdTask);
    }

    @PutMapping(path = "/tasks/{id}")
    public TaskResponseDTO updateTask(@RequestBody @Valid TaskRequestDTO dto,
                                      @PathVariable(name = "id") long id,
                                      @AuthenticationPrincipal UserDetails authDetails) {
        Task updatedTask = taskService.updateTask(id, dto, authDetails);
        return taskMapper.toTaskResponseDTO(updatedTask);
    }

    @DeleteMapping(path = "/tasks/{id}")
    public void deleteTask(@PathVariable(name = "id") long id,
                           @AuthenticationPrincipal UserDetails authDetails) {
        taskService.deleteTask(id, authDetails);
    }
}
