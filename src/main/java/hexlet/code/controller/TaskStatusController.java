package hexlet.code.controller;

import hexlet.code.domain.mapper.TaskStatusModelMapper;
import hexlet.code.domain.dto.TaskStatusRequestDTO;
import hexlet.code.domain.dto.TaskStatusResponseDTO;
import hexlet.code.domain.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

// TODO Validate dto field names (can me extra now)

@RestController
@RequestMapping("${base.url}" + "/statuses")
public class TaskStatusController {

    private final TaskStatusService statusService;
    private final TaskStatusModelMapper statusMapper;

    public TaskStatusController(TaskStatusService statusService,
                                TaskStatusModelMapper statusMapper) {
        this.statusService = statusService;
        this.statusMapper = statusMapper;
    }

    @GetMapping
    public List<TaskStatusResponseDTO> findAllStatuses() {
        List<TaskStatus> existedStatuses = statusService.findAllStatuses();
        List<TaskStatusResponseDTO> statusDTOList = new ArrayList<>();
        existedStatuses.forEach(status -> statusDTOList.add(statusMapper.toTaskStatusResponseDTO(status)));
        return statusDTOList;
    }

    @GetMapping(path = "/{id}")
    public TaskStatusResponseDTO findStatusById(@PathVariable(name = "id") long id) {
        TaskStatus existedStatus = statusService.findStatusById(id);
        return statusMapper.toTaskStatusResponseDTO(existedStatus);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskStatusResponseDTO createStatus(@RequestBody @Valid TaskStatusRequestDTO dto) {
        TaskStatus createdStatus = statusService.createStatus(dto);
        return statusMapper.toTaskStatusResponseDTO(createdStatus);
    }

    @PutMapping(path = "/{id}")
    public TaskStatusResponseDTO updateStatus(@RequestBody @Valid TaskStatusRequestDTO dto,
                                              @PathVariable(name = "id") long id) {
        TaskStatus updatedStatus = statusService.updateStatus(id, dto);
        return statusMapper.toTaskStatusResponseDTO(updatedStatus);
    }

    @DeleteMapping(path = "/{id}")
    public void deleteStatus(@PathVariable(name = "id") long id) {
        statusService.deleteStatus(id);
    }
}
