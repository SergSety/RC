package by.nca.rc.util;

import by.nca.rc.exceptions.NotCreatedException;
import lombok.Getter;
import lombok.Setter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

@Getter
@Setter
public class ErrorsResponse {

    private Integer statusCode;
    private Long timestamp;
    private String message;
    private String description;
    private String reason;

    public ErrorsResponse(Integer statusCode, Long timestamp, String message, String description) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
        this.description = description;
    }

    public ErrorsResponse(Integer statusCode, Long timestamp, String message, String description, String reason) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
        this.description = description;
        this.reason = reason;
    }

    public static void errorsResponse(BindingResult bindingResult) {

        StringBuilder errorMsg = new StringBuilder();

        List<FieldError> errors = bindingResult.getFieldErrors();

        for (FieldError error : errors) {

            errorMsg.append(error.getField())
                    .append(" - ")
                    .append(error.getDefaultMessage() == null ? error.getCode() : error.getDefaultMessage())
                    .append("; ");
        }

        throw new NotCreatedException(errorMsg.toString());
    }
}
