package by.nca.rc.security;

import by.nca.rc.security.model.ERole;
import by.nca.rc.security.service.CustomerDetailsServiceImpl;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    private MockHttpServletRequest mockRequest;
    private MockHttpServletResponse mockResponse;
    private MockFilterChain mockFilterChain;

    @Mock
    private CustomerDetails customerDetails;
    @Mock
    private CustomerDetailsServiceImpl customerDetailsService;

    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    void setUp() {

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        mockRequest = new MockHttpServletRequest();
        mockResponse = new MockHttpServletResponse();
        mockFilterChain = new MockFilterChain();


        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(ERole.ROLE_ADMIN.name()));

        customerDetails = new CustomerDetails(

                1L,
                "TestAdmin",
                passwordEncoder.encode("TestAdmin"),
                authorities
        );

        String token = Jwts.builder()
                .setSubject("TestAdmin")
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + 300000L))
                .setIssuer("NCA")
                .signWith(SignatureAlgorithm.HS512, "jwtSecret")
                .compact();

        String tokenValue = "Bearer " + token;
        mockRequest.addHeader("Authorization", tokenValue);

        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("JwtFilter.doFilterInternal. Should Continues To Next Filter")
    void doFilterInternal_ShouldContinuesToNextFilter() throws IOException, ServletException {

        // given
        customerDetailsService.loadUserByUsername(anyString());
        MockFilterChain mockFilterChainSpy = spy(mockFilterChain);

        // when
        jwtFilter.doFilter(mockRequest, mockResponse, mockFilterChainSpy);

        // then
        verify(mockFilterChainSpy, times(1)).doFilter(mockRequest, mockResponse);
    }

    @Test
    @DisplayName("JwtFilter.doFilterInternal. Should Loads User From Token by 'username'")
    void doFilterInternal_ShouldLoadsUserFromToken() throws ServletException, IOException {

        // given
        customerDetailsService.loadUserByUsername(customerDetails.getUsername());

        // when
        jwtFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // then
        ArgumentCaptor<String> usernameCaptor = ArgumentCaptor.forClass(String.class);
        verify(customerDetailsService, times(1)).loadUserByUsername(usernameCaptor.capture());
        assertThat(usernameCaptor.getValue(), equalTo("TestAdmin"));
    }

    @Test
    @DisplayName("JwtFilter.doFilterInternal. Should Set Authentication In Security Context")
    void doFilterInternal_ShouldSetsAuthenticationInSecurityContext() throws ServletException, IOException {

        // given
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getPrincipal()).thenReturn(customerDetails);

        // when
        jwtFilter.doFilter(mockRequest, mockResponse, mockFilterChain);

        // then
        authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomerDetails principal = (CustomerDetails) authentication.getPrincipal();
        assertThat(principal.getUsername(), equalTo("TestAdmin"));
        assertThat(principal.getAuthorities().stream().findFirst().get().toString(), equalTo(ERole.ROLE_ADMIN.name()));

        verify(securityContext, never()).setAuthentication(any(Authentication.class));
    }

    @Test
    @DisplayName("JwtFilter.doFilterInternal. When Request Has No Token_Should Continues To Next Filter")
    void doFilterInternal_WhenRequestHasNoToken_ShouldContinuesToNextFilter() throws ServletException, IOException {

        // Given
        MockFilterChain mockFilterChainSpy = spy(mockFilterChain);
        MockHttpServletRequest requestWithoutToken = new MockHttpServletRequest();

        // when
        jwtFilter.doFilter(requestWithoutToken, mockResponse, mockFilterChainSpy);

        // then
        verify(mockFilterChainSpy, times(1)).doFilter(requestWithoutToken, mockResponse);
    }
}