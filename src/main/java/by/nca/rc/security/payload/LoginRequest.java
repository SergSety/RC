package by.nca.rc.security.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

	@NotBlank(message = "Username should not be blank")
	@NotEmpty(message = "Username should not be empty")
	@Size(min = 2, max = 40, message = "Username should be between 2 and 40 characters")
	private String username;

	@NotBlank(message = "Password should not be blank")
	@NotEmpty(message = "Username should not be empty")
	private String password;
}
