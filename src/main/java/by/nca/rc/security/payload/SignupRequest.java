package by.nca.rc.security.payload;

import by.nca.rc.security.model.ERole;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SignupRequest {

    @NotBlank(message = "Username should not be blank")
    @NotEmpty(message = "Username should not be empty")
    @Size(min = 2, max = 40, message = "Username should be between 2 and 40 characters")
    private String username;

    @Enumerated(value = EnumType.STRING)
    private ERole role;

    @NotBlank(message = "Name should not be blank")
    @NotEmpty(message = "Name should not be empty")
    @Size(min = 2, max = 20, message = "Name should be between 2 and 20 characters")
    private String name;

    @NotBlank(message = "Surname should not be blank")
    @NotEmpty(message = "Surname should not be empty")
    @Size(min = 2, max = 20, message = "Surname should be between 2 and 20 characters")
    private String surname;

    @NotBlank(message = "Parent should not be blank")
    @NotEmpty(message = "Parent name should not be empty")
    @Size(min = 2, max = 20, message = "Parent name should be between 2 and 20 characters")
    private String parentName;
}
