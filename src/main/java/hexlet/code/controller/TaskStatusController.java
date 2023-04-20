package hexlet.code.controller;

import hexlet.code.domain.dto.TaskStatusModelMapper;
import hexlet.code.domain.dto.TaskStatusRequestDTO;
import hexlet.code.domain.dto.TaskStatusResponseDTO;
import hexlet.code.domain.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
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

// TODO Validate dto field names (can me extra now)

@RestController
@RequestMapping("/api")
public class TaskStatusController {

    private final TaskStatusService statusService;
    private final TaskStatusModelMapper statusMapper;

    public TaskStatusController(TaskStatusService statusService,
                                TaskStatusModelMapper statusMapper) {
        this.statusService = statusService;
        this.statusMapper = statusMapper;
    }

    @GetMapping(path = "/statuses")
    public List<TaskStatusResponseDTO> getAllStatuses() {
        Iterable<TaskStatus> existedStatuses = statusService.getAllStatuses();
        List<TaskStatusResponseDTO> statusDTOList = new ArrayList<>();
        existedStatuses.forEach(status -> statusDTOList.add(statusMapper.toTaskStatusResponseDTO(status)));
        statusDTOList.sort(Comparator.comparing(TaskStatusResponseDTO::getId));
        return statusDTOList;
    }

    @GetMapping(path = "/statuses/{id}")
    public TaskStatusResponseDTO getStatusById(@PathVariable(name = "id") long id) {
        TaskStatus existedStatus = statusService.getStatusById(id);
        return statusMapper.toTaskStatusResponseDTO(existedStatus);
    }

    @PostMapping(path = "/statuses")
    public TaskStatusResponseDTO createStatus(@RequestBody @Valid TaskStatusRequestDTO dto) {
        TaskStatus createdStatus = statusService.createStatus(dto);
        return statusMapper.toTaskStatusResponseDTO(createdStatus);
    }

    @PutMapping(path = "/statuses/{id}")
    public TaskStatusResponseDTO updateStatus(@RequestBody @Valid TaskStatusRequestDTO dto,
                                              @PathVariable(name = "id") long id) {
        TaskStatus updatedStatus = statusService.updateStatus(id, dto);
        return statusMapper.toTaskStatusResponseDTO(updatedStatus);
    }

    @DeleteMapping(path = "/statuses/{id}")
    public void deleteStatus(@PathVariable(name = "id") long id) {
        statusService.deleteStatus(id);
    }
}