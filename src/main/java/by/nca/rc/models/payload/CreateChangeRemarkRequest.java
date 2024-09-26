package by.nca.rc.models.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateChangeRemarkRequest {

    @NotBlank(message = "Text should not be blank")
    @NotEmpty(message = "Text should not be empty")
    @Size(min = 2, max = 300, message = "Text should be between 2 and 300 characters")
    private String text;
}
