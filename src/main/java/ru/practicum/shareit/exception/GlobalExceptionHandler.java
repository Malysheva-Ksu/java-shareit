package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            ItemUnavailableException.class,
            InvalidBookingTimeException.class,
            BookingAlreadyApprovedException.class,
            IllegalArgumentException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleInvalidBookingAndArguments(final RuntimeException e) {
        log.warn("Ошибка 400 Bad Request: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler({
            BookingNotFoundException.class,
            BookingSelfOwnershipException.class,
            BookingPermissionException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, String>> handleBookingNotFoundOrAccessDenied(final RuntimeException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", "Booking Not Found or Access Denied.",
                        "errorMessage", e.getMessage()
                ));
    }

    @ExceptionHandler({UserNotFoundException.class, ItemNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, String>> handleNotFoundExceptions(final RuntimeException e) {
        String errorType = "Object not found";
        if (e instanceof UserNotFoundException) {
            errorType = "User not found";
        } else if (e instanceof ItemNotFoundException) {
            errorType = "Item not found";
        }
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", errorType,
                        "errorMessage", e.getMessage()
                ));
    }

    @ExceptionHandler(UnknownStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleUnknownStateException(final UnknownStateException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", "Status validation error",
                        "errorMessage", e.getMessage()
                ));
    }

    @ExceptionHandler({ItemRequestNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, String>> handleResourceNotFound(final RuntimeException e) {
        log.warn("Ресурс не найден: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", "Resource Not Found",
                        "errorMessage", e.getMessage()
                ));
    }

    @ExceptionHandler(CommentValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> CommentValidationException(final CommentValidationException e) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", e.getMessage()
                ));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(final EmailAlreadyExistsException e) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error", "Email already exists",
                        "errorMessage", e.getMessage()
                ));
    }

    @ExceptionHandler(UserAccessDeniedException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, String>> handleUserAccessDeniedException(final UserAccessDeniedException e) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", "Access Denied",
                        "errorMessage", e.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(final MethodArgumentNotValidException e) {
        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .reduce((msg1, msg2) -> msg1 + "; " + msg2)
                .orElse("Validation failed");
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "error", "Validation Error",
                        "errorMessage", errorMessage
                ));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleMissingParams(final MissingServletRequestParameterException e) {
        String message = String.format("Required request parameter '%s' for method parameter type %s is not present",
                e.getParameterName(), e.getParameterType());
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "error", "Missing Request Parameter",
                        "errorMessage", message
                ));
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleMissingHeader(final MissingRequestHeaderException e) {
        String message = String.format("Required request header '%s' for method parameter type %s is not present",
                e.getHeaderName(), Objects.requireNonNull(e.getParameter().getParameterType().getName()));
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "error", "Missing Request Header",
                        "errorMessage", message
                ));
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> handleGenericException(final Throwable e) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Internal Server Error",
                        "errorMessage", e.getMessage()
                ));
    }
}