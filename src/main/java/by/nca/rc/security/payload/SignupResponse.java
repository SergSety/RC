package by.nca.rc.security.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SignupResponse {

    private String username;
    private String pass;
    private String message;
}
