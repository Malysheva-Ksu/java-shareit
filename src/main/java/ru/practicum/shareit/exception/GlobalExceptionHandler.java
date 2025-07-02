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
            BookingAlreadyApprovedException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleInvalidBooking(final RuntimeException e) {
        log.warn("Ошибка 400 Bad Request (Booking): {}", e.getMessage());
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
        log.warn("Ошибка 404 Not Found (Booking Access): {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(final IllegalArgumentException e) {
        log.warn("Ошибка 400 Bad Request (Illegal Argument): {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", e.getMessage()));
    }

    @ExceptionHandler(UserNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, String>> handleUserNotFoundException(final UserNotFoundException e) {
        log.warn("Ошибка 404 Not Found: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", "User not found",
                        "errorMessage", e.getMessage()
                ));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, String>> handleEmailAlreadyExistsException(final EmailAlreadyExistsException e) {
        log.warn("Ошибка 409 Conflict: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "error", "Email already exists",
                        "errorMessage", e.getMessage()
                ));
    }

    @ExceptionHandler(ItemNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<Map<String, String>> handleItemNotFoundException(final ItemNotFoundException e) {
        log.warn("Ошибка 404 Not Found (Item): {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "error", "Item not found",
                        "errorMessage", e.getMessage()
                ));
    }

    @ExceptionHandler(UserAccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ResponseEntity<Map<String, String>> handleUserAccessDeniedException(final UserAccessDeniedException e) {
        log.warn("Ошибка 403 Forbidden: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Map.of(
                        "error", "Access Denied",
                        "errorMessage", e.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(final MethodArgumentNotValidException e) {
        log.warn("Ошибка 400 Bad Request (Validation): {}", e.getMessage());
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
        log.warn("Ошибка 400 Bad Request (Missing Parameter): {}", message);
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
        log.warn("Ошибка 400 Bad Request (Missing Header): {}", message);
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "error", "Missing Request Header",
                        "errorMessage", message
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(final IllegalArgumentException e) {
        log.warn("Ошибка 400 Bad Request: {}", e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(Map.of(
                        "error", "Illegal Argument",
                        "errorMessage", e.getMessage()
                ));
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<Map<String, String>> handleGenericException(final Throwable e) {
        log.error("Внутренняя ошибка сервера 500: ", e);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                        "error", "Internal Server Error",
                        "errorMessage", e.getMessage()
                ));
    }
}