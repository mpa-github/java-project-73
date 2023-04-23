package hexlet.code.repository;

import hexlet.code.domain.model.TaskStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TaskStatusRepository extends JpaRepository<TaskStatus, Long> {

    Optional<TaskStatus> findTaskStatusById(long id);
}
