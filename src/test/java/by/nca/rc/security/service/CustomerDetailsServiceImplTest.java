package by.nca.rc.security.service;

import by.nca.rc.models.Customer;
import by.nca.rc.repositories.CustomerRepository;
import by.nca.rc.security.CustomerDetails;
import by.nca.rc.security.model.ERole;
import by.nca.rc.security.model.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerDetailsServiceImplTest {

    @Mock
    CustomerRepository customerRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    CustomerDetailsServiceImpl customerDetailsService;

    private Customer customer;

    @BeforeEach
    public void setup() {

        Role admin = new Role(ERole.ROLE_ADMIN);

        Set<Role> roles = new HashSet<>();
        roles.add(admin);

        customer = new Customer(

                "TestAdmin",
                passwordEncoder.encode("TestAdmin"),
                "TestAdmin",
                "TestAdmin",
                "TestAdmin",
                new Date(),
                new Date(),
                roles
        );
    }

    @Test
    @DisplayName("LoadUserByUsername. User not found. Throw UsernameNotFoundException")
    void loadUserByUsername_UserNotFound_ThrowUsernameNotFoundException() {

        // given
        String login = "username";
        when(customerRepository.findCustomerByUsername(anyString())).thenReturn(Optional.empty());

        // when
        UsernameNotFoundException exception = org.junit.jupiter.api.Assertions
                .assertThrows(UsernameNotFoundException.class,
                        () -> customerDetailsService.loadUserByUsername(login));

        // then
        assertEquals("User Not Found with username: " + login, exception.getMessage());
        verify(customerRepository, times(1)).findCustomerByUsername(anyString());
        verifyNoMoreInteractions(customerRepository);
    }

    @Test
    @DisplayName("LoadUserByUsername. User exist. Should call Repository and retrieve Customer by username")
    void loadUserByUsername_UserExist_shouldLoadUserByUsername() {

        // given
        when(customerRepository.findCustomerByUsername(customer.getUsername()))
                .thenReturn(Optional.ofNullable(customer));

        // when
        final Customer actualCustomer = customerRepository.findCustomerByUsername(customer.getUsername()).get();

        // then
        assertNotNull(actualCustomer);
        assertEquals(customer, actualCustomer);

        assertThat(actualCustomer.getName()).isEqualTo(customer.getName());
        assertThat(actualCustomer.getSurname()).isEqualTo(customer.getSurname());

        verify(customerRepository).findCustomerByUsername(customer.getUsername());
    }

    @Test
    @DisplayName("Build CustomerDetails from the Customer")
    void buildCustomerDetails_ShouldReturnCustomerDetails() {

        CustomerDetails customerDetails = CustomerDetails.build(customer);

        assertEquals("TestAdmin", customerDetails.getUsername());
    }
}