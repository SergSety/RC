package by.nca.rc.exceptions;

import by.nca.rc.util.ErrorsResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.MimeType;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {

//---------------------------------------------------------------------------------------------------

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorsResponse> handlerException(HttpMessageNotReadableException e, WebRequest request) {

        String logMessage = String.format("Exception: %s, message: %s, request: %s",
                e.getClass().getName(), e.getMessage(), request.getDescription(false));
        log.error(logMessage, e);

        ErrorsResponse response = new ErrorsResponse(
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis(),
                e.getMessage(),
                request.getDescription(false),
                "The HttpMessageNotReadableException occurs when the request body is missing or is unreadable"
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, TypeMismatchException.class})
    public ResponseEntity<ErrorsResponse> handlerException(TypeMismatchException e, WebRequest request) {

        String logMessage = String.format("Exception: %s, message: %s, request: %s",
                e.getClass().getName(), e.getMessage(), request.getDescription(false));
        log.error(logMessage, e);

        ErrorsResponse response = new ErrorsResponse(
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis(),
                e.getLocalizedMessage(),
                request.getDescription(false),
                "The Type Mismatch Exceptions occur when Spring Controller cannot map the request parameters, " +
                       "path variables, or header values into controller method arguments"

        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<Map<String, String>> handlerException(HttpMediaTypeNotSupportedException e) {

        String logMessage = String.format("Exception: %s, message: %s",
                e.getClass().getName(), e.getMessage());
        log.error(logMessage, e);

        String provided = e.getContentType().toString();
        List<String> supported = e.getSupportedMediaTypes().stream()
                .map(MimeType::toString)
                .collect(Collectors.toList());

        String error = provided + " is not one of the supported media types (" +
                String.join(", ", supported) + ")";

        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", error);
        errorResponse.put("message", e.getLocalizedMessage());
        errorResponse.put("status", HttpStatus.UNSUPPORTED_MEDIA_TYPE.toString());

        return new ResponseEntity<>(errorResponse, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorsResponse> handlerException(HttpRequestMethodNotSupportedException e,
                                                           WebRequest request) {

        String logMessage = String.format("Exception: %s, message: %s, request: %s",
                e.getClass().getName(), e.getMessage(), request.getDescription(false));
        log.error(logMessage, e);

        ErrorsResponse errorsResponse = new ErrorsResponse(
                HttpStatus.METHOD_NOT_ALLOWED.value(),
                System.currentTimeMillis(),
                e.getMessage(),
                request.getDescription(false),
                "The HttpMethodNotSupportedException occurs when the HTTP endpoint " +
                        "on the REST API does not support the HTTP request method"
        );

        return new ResponseEntity<>(errorsResponse, HttpStatus.METHOD_NOT_ALLOWED);
    }

    // ------------------------------------CUSTOM EXCEPTIONS------------------------------------------------

    @ExceptionHandler
    private ResponseEntity<ErrorsResponse> handlerException(AlreadyExistException e, WebRequest request) {

        String logMessage = String.format("Exception: %s, message: %s, request: %s",
                e.getClass().getName(), e.getMessage(), request.getDescription(false));
        log.error(logMessage, e);

        ErrorsResponse response = new ErrorsResponse(
                HttpStatus.CONFLICT.value(),
                System.currentTimeMillis(),
                e.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT); // 409
    }

    @ExceptionHandler
    private ResponseEntity<ErrorsResponse> handlerException(NotFoundException e, WebRequest request) {

        String logMessage = String.format("Exception: %s, message: %s, request: %s",
                e.getClass().getName(), e.getMessage(), request.getDescription(false));
        log.error(logMessage, e);

        ErrorsResponse response = new ErrorsResponse(
                HttpStatus.NOT_FOUND.value(),
                System.currentTimeMillis(),
                e.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND); // 404
    }

    @ExceptionHandler
    private ResponseEntity<ErrorsResponse> handlerException(NotCreatedException e, WebRequest request) {

        String logMessage = String.format("Exception: %s, message: %s, request: %s",
                e.getClass().getName(), e.getMessage(), request.getDescription(false));
        log.error(logMessage, e);

        ErrorsResponse response = new ErrorsResponse(
                HttpStatus.BAD_REQUEST.value(),
                System.currentTimeMillis(),
                e.getMessage(),
                request.getDescription(false)
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST); // BAD_REQUEST = 400
    }
}
