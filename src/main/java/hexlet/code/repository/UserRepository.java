package hexlet.code.repository;

import hexlet.code.domain.model.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    Optional<User> findUserById(long id);
    Optional<User> findUserByEmailIgnoreCase(String email);
}
