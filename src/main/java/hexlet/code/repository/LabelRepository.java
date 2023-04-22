package hexlet.code.repository;

import hexlet.code.domain.model.Label;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LabelRepository extends CrudRepository<Label, Long> {

    List<Label> findAll();
    //Set<Label> findAllById(Set<Long> ids);
    //List<Label> findAllById(List<Long> ids);
    //Set<Label> findAllById(Set<Long> ids);

    Optional<Label> findLabelById(long id);
}
