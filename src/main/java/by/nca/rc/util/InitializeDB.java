package by.nca.rc.util;

import by.nca.rc.models.Customer;
import by.nca.rc.repositories.CustomerRepository;
import by.nca.rc.security.model.ERole;
import by.nca.rc.security.model.Role;
import by.nca.rc.security.repository.RolesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Component
public class InitializeDB {

    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository rolesRepository;

    @Autowired
    public InitializeDB(CustomerRepository customerRepository, PasswordEncoder passwordEncoder,
                        RolesRepository rolesRepository) {
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.rolesRepository = rolesRepository;
    }

    @PostConstruct
    public void createAdmin() {

        if (rolesRepository.findByName(ERole.ROLE_ADMIN).isEmpty()) {

            rolesRepository.save(new Role(ERole.ROLE_ADMIN));
        }

        if (rolesRepository.findByName(ERole.ROLE_JOURNALIST).isEmpty()) {

            rolesRepository.save(new Role(ERole.ROLE_JOURNALIST));
        }

        if (rolesRepository.findByName(ERole.ROLE_SUBSCRIBER).isEmpty()) {

            rolesRepository.save(new Role(ERole.ROLE_SUBSCRIBER));
        }

        Set<Role> rolesAdmin = new HashSet<>();
        Role roleAdmin = rolesRepository.findByName(ERole.ROLE_ADMIN)
                .orElseThrow(() -> new RuntimeException("Error: Role Admin is not found."));
        rolesAdmin.add(roleAdmin);

        Set<Role> rolesJournalist = new HashSet<>();
        Role roleJournalist = rolesRepository.findByName(ERole.ROLE_JOURNALIST)
                .orElseThrow(() -> new RuntimeException("Error: Role Journalist is not found."));
        rolesJournalist.add(roleJournalist);

        Set<Role> rolesSubscriber = new HashSet<>();
        Role roleSubscriber = rolesRepository.findByName(ERole.ROLE_SUBSCRIBER)
                .orElseThrow(() -> new RuntimeException("Error: Role Subscriber is not found."));
        rolesSubscriber.add(roleSubscriber);

        if (customerRepository.findCustomerByUsername("Admin").isEmpty()) {

            Customer customerAdmin  = new Customer(

                    "Admin",
                    passwordEncoder.encode("12345"),
                    "Admin",
                    "Admin",
                    "Admin",
                    new Date(),
                    new Date(),
                    rolesAdmin
            );
            customerRepository.save(customerAdmin);
        };

        if (customerRepository.findCustomerByUsername("Journalist").isEmpty()) {

            Customer customerJournalist  = new Customer(

                    "Journalist",
                    passwordEncoder.encode("12345"),
                    "Journalist",
                    "Journalist",
                    "Journalist",
                    new Date(),
                    new Date(),
                    rolesJournalist
            );
            customerRepository.save(customerJournalist);
        };

        if (customerRepository.findCustomerByUsername("Subscriber").isEmpty()) {

            Customer customerSubscriber  = new Customer(

                    "Subscriber",
                    passwordEncoder.encode("12345"),
                    "Subscriber",
                    "Subscriber",
                    "Subscriber",
                    new Date(),
                    new Date(),
                    rolesSubscriber
            );
            customerRepository.save(customerSubscriber);
        };
    }
}
