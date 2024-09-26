package by.nca.rc.repositories;

import by.nca.rc.models.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Boolean existsByUsername(String username);
    Optional<Customer> findCustomerByUsername(String username);
}
