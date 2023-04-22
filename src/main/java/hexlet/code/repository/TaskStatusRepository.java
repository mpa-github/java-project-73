package hexlet.code.repository;

import hexlet.code.domain.model.TaskStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskStatusRepository extends CrudRepository<TaskStatus, Long> {

    List<TaskStatus> findAll();
    Optional<TaskStatus> findTaskStatusById(long id);
}
