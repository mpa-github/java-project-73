package hexlet.code.service;

import hexlet.code.domain.mapper.TaskStatusModelMapper;
import hexlet.code.domain.dto.TaskStatusRequestDTO;
import hexlet.code.domain.model.TaskStatus;
import hexlet.code.exception.NotFoundException;
import hexlet.code.repository.TaskStatusRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class TaskStatusService {

    private final TaskStatusRepository statusRepository;
    private final TaskStatusModelMapper statusMapper;

    public TaskStatusService(TaskStatusRepository statusRepository,
                             TaskStatusModelMapper statusMapper) {
        this.statusRepository = statusRepository;
        this.statusMapper = statusMapper;
    }

    public List<TaskStatus> findAllStatuses() {
        return statusRepository.findAllByOrderByIdAsc();
    }

    public TaskStatus findStatusById(long id) {
        return statusRepository.findTaskStatusById(id)
            .orElseThrow(() -> new NotFoundException("Status with id='%d' not found!".formatted(id)));
    }

    // TODO For Hibernate request optimization
    public TaskStatus getStatusReferenceById(long id) {
        return statusRepository.getReferenceById(id);
    }

    @Transactional
    public TaskStatus createStatus(TaskStatusRequestDTO dto) {
        TaskStatus newStatus = statusMapper.toTaskStatusModel(dto);
        return statusRepository.save(newStatus);
    }

    @Transactional
    public TaskStatus updateStatus(long id, TaskStatusRequestDTO dto) {
        TaskStatus statusToUpdate = findStatusById(id);
        statusToUpdate.setName(dto.getName());
        return statusToUpdate;
    }

    @Transactional
    public void deleteStatus(long id) {
        TaskStatus existedStatus = findStatusById(id);
        statusRepository.delete(existedStatus);
    }
}
