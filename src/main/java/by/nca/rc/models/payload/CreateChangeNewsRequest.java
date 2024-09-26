package by.nca.rc.models.payload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateChangeNewsRequest {

    @NotBlank(message = "Title should not be blank")
    @NotEmpty(message = "Title should not be empty")
    @Size(min = 2, max = 150, message = "Title should be between 2 and 150 characters")
    private String title;

    @NotBlank(message = "Text should not be blank")
    @NotEmpty(message = "Text should not be empty")
    @Size(min = 2, max = 2000, message = "Text should be between 2 and 2000 characters")
    private String text;
}
