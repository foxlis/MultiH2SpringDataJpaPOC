package hello.persistence.repository.secondary;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import hello.persistence.entity.secondary.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    List<Customer> findByLastName(String lastName);
}
