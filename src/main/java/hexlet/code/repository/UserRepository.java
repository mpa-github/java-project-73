package hexlet.code.repository;

import hexlet.code.domain.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    List<User> findAll();
    Optional<User> findUserById(long id);
    Optional<User> findUserByEmailIgnoreCase(String email);
    Optional<User> findUserByLastName(String LastName);
}
