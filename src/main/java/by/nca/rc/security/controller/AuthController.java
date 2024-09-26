package by.nca.rc.security.controller;

import by.nca.rc.models.Customer;
import by.nca.rc.security.CustomerDetails;
import by.nca.rc.security.JwtUtil;
import by.nca.rc.security.model.Role;
import by.nca.rc.security.payload.LoginRequest;
import by.nca.rc.security.payload.LoginResponse;
import by.nca.rc.security.payload.SignupRequest;
import by.nca.rc.security.payload.SignupResponse;
import by.nca.rc.security.repository.RolesRepository;
import by.nca.rc.services.CustomerService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static by.nca.rc.util.ErrorsResponse.errorsResponse;

@Slf4j
@RestController
@CrossOrigin(origins="*")
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CustomerService customerService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final RolesRepository rolesRepository;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager, CustomerService customerService,
                          JwtUtil jwtUtil, PasswordEncoder passwordEncoder, RolesRepository rolesRepository) {

        this.authenticationManager = authenticationManager;
        this.customerService = customerService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.rolesRepository = rolesRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest loginRequest, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) errorsResponse(bindingResult);

        Authentication authentication;

        try {

// UsernamePasswordAuthenticationToken - стандартный класс для инкапсуляции login and password in the Spring Security

            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
        } catch (BadCredentialsException e) {
            log.error("Invalid username/password combination");
            return new ResponseEntity<>("Invalid username/password combination", HttpStatus.FORBIDDEN);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomerDetails customerDetails = (CustomerDetails) authentication.getPrincipal();

        String jwt = jwtUtil.generateToken(customerDetails);

        LoginResponse loginResponse = new LoginResponse(
                jwt,
                jwtUtil.getJwtExpirationMs());

        return new ResponseEntity<>(loginResponse, HttpStatus.OK);
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> createCustomer(@Valid @RequestBody SignupRequest signupRequest,
                                                         BindingResult bindingResult, HttpServletRequest request) {

        if (bindingResult.hasErrors()) errorsResponse(bindingResult);

        // Checking if this name exists
        customerService.existsByUsername(signupRequest.getUsername());

        // Create new user's account
        Customer customer = new Customer();

        customer.setUsername(signupRequest.getUsername());
        // For convenience in testing the application, we simply encode Username
        customer.setPassword(passwordEncoder.encode(signupRequest.getUsername()));

        customer.setName(signupRequest.getName());
        customer.setSurname(signupRequest.getSurname());
        customer.setParentName(signupRequest.getParentName());

        customer.setCreationDate(new Date());
        customer.setLastEditDate(new Date());

        Set<Role> roles = new HashSet<>();
        Role role = rolesRepository.findByName(signupRequest.getRole())
                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
        roles.add(role);
        customer.setRoles(roles);

        customerService.save(customer);

        SignupResponse signupResponse = new SignupResponse(
                signupRequest.getUsername(),
                signupRequest.getUsername(),
                "We believe that this message was sent by email (for example). Now you have to log in"
        );

        return new ResponseEntity<>(signupResponse, HttpStatus.CREATED);
    }
}
