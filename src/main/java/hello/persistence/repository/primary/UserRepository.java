package hello.persistence.repository.primary;

import org.springframework.data.repository.CrudRepository;

import hello.persistence.entity.primary.User;

public interface UserRepository extends CrudRepository<User, Integer> { }
