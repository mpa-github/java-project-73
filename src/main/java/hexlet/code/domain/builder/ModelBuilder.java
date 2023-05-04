package hexlet.code.domain.builder;

import org.springframework.security.core.userdetails.UserDetails;

public interface ModelBuilder<T, E> {

    T create(E dto, UserDetails userDetails);
    T update(T model, E dto);
}
