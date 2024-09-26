package by.nca.rc.services;

import by.nca.rc.exceptions.AlreadyExistException;
import by.nca.rc.exceptions.NotFoundException;
import by.nca.rc.models.Customer;
import by.nca.rc.repositories.CustomerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void existsByUsername(String username) {

        if (customerRepository.existsByUsername(username))
            throw new AlreadyExistException("This username already exist");
    }

    @Transactional
    public void save(Customer customer) {
        customerRepository.save(customer);
    }

    public Customer findById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new NotFoundException("Customer not found"));
    }
}
