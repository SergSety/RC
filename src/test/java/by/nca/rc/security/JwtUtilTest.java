package by.nca.rc.security;

import by.nca.rc.models.Customer;
import by.nca.rc.security.model.ERole;
import by.nca.rc.security.model.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    JwtUtil jwtUtil;

    private final String jwtSecret = "jwtSecret";
    private final Long jwtExpirationMs = 300000L;
    private String accessToken;

    @BeforeEach
    public void init() throws NoSuchFieldException, IllegalAccessException {

        Field field = jwtUtil.getClass().getDeclaredField("jwtSecret");
        field.setAccessible(true);
        field.set(jwtUtil, "jwtSecret");

        Field fieldOne = jwtUtil.getClass().getDeclaredField("jwtExpirationMs");
        fieldOne.setAccessible(true);
        fieldOne.set(jwtUtil, 300000L);

        accessToken = jwtUtil.generateTokenFromUsername("Username");
    }

    @Test
    @DisplayName("Generate Token From Username. Should return token")
    void generateToken_shouldReturnToken() {

        // given
        Role admin = new Role(ERole.ROLE_ADMIN);

        Set<Role> roles = new HashSet<>();
        roles.add(admin);

        Customer customer = new Customer(

                "Username",
                passwordEncoder.encode("Username"),
                "Username",
                "Username",
                "Username",
                new Date(),
                new Date(),
                roles
        );

        CustomerDetails customerDetails = CustomerDetails.build(customer);

        // when
        final String accessToken = jwtUtil.generateToken(customerDetails);

        // then
        assertNotNull(accessToken);
    }

    @Test
    @DisplayName("Generate Token From Username. The username in the token is used")
    void generateTokenFromUsername_UsesUsername() {

        String subject = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(accessToken)
                .getBody()
                .getSubject();

        assertThat(subject).isEqualTo("Username");
    }

    @Test
    @DisplayName("Generate Token From Username. The IssueDate in the token is used")
    void generateTokenFromUsername_usesIssueDate() {

        Date issuedAt = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(accessToken)
                .getBody()
                .getIssuedAt();
        assertThat(issuedAt).isNotNull();
    }

    @Test
    @DisplayName("Generate Token From Username. The Expiration (300000 ms) in the token is used")
    void generateTokenFromUsername_hasExpiration300000Ms() {

        Claims claims = Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(accessToken)
                .getBody();
        Date issuedAt = claims.getIssuedAt();
        Date expiration = claims.getExpiration();
        assertThat(expiration.getTime() - issuedAt.getTime()).isEqualTo(jwtExpirationMs);
    }

    @Test
    @DisplayName("validateToken. Valid JWT. Should return TRUE")
    void validateToken_ValidJWT_shouldReturnTrue() {

        assertNotNull(accessToken);

        // when

        // then
        assertTrue(jwtUtil.validateToken(accessToken));
    }

    @Test
    @DisplayName("validateToken. Invalid JWT. Should Throw Exception")
    void validateToken_InvalidJWT_shouldThrowException() {

        assertThrows(Exception.class,
                () -> jwtUtil.validateToken("some_wrong_token"));
    }

    @Test
    @DisplayName("validateToken. Invalid JWT Signature. Should Throw Signature Exception")
    void validateToken_InvalidJWTSignature_shouldThrowSignatureException() {

        assertNotNull(accessToken);

        assertThrows(SignatureException.class,
                () -> Jwts.parser().setSigningKey("wrongSecret").parseClaimsJws(accessToken));
    }
}