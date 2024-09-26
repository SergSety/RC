package by.nca.rc.security;

import by.nca.rc.exceptions.NotFoundException;
import by.nca.rc.models.Customer;
import by.nca.rc.repositories.NewsRepository;
import by.nca.rc.repositories.RemarkRepository;
import by.nca.rc.services.CustomerService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("methodAccess")
public class MethodAccessHandler {

    private final CustomerService customerService;
    private final NewsRepository newsRepository;
    private final RemarkRepository remarkRepository;

    public MethodAccessHandler(CustomerService customerService, NewsRepository newsRepository,
                               RemarkRepository remarkRepository) {
        this.customerService = customerService;
        this.newsRepository = newsRepository;
        this.remarkRepository = remarkRepository;
    }

    public  boolean hasCustomerIdForNews(Long id) {

        Long customerId = newsRepository.findById(id).orElseThrow(() -> new NotFoundException("News not found"))
                .getInsertedById();

        return isOwner(customerId);
    }

    public  boolean hasCustomerIdForRemark(Long id) {

        Long customerId = remarkRepository.findById(id).orElseThrow(() -> new NotFoundException("Remark not found"))
                .getInsertedById();

        return isOwner(customerId);
    }

    private boolean isOwner(Long id) {
        CustomerDetails customerDetails = getAuthenticatedUser();
        Customer customer = customerService.findById(id);
        return (customer.getUsername().equals(customerDetails.getUsername()));
    }

    private CustomerDetails getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (CustomerDetails) authentication.getPrincipal();
    }
}
