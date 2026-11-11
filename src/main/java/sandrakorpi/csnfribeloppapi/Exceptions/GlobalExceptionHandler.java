package sandrakorpi.csnfribeloppapi.Exceptions;

import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Ger klienten tydliga fel utan att läcka stacktraces eller interna detaljer.
 * Standardfel i Spring MVC (405, 404, saknade headers med mera) hanteras av basklassen.
 */
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, String> fieldErrors = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.putIfAbsent(error.getField(), error.getDefaultMessage()));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("message", "Ogiltiga uppgifter");
        body.put("errors", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, String>> handleBadRequest(Exception ex) {
        String message = ex instanceof IllegalArgumentException ? ex.getMessage() : "Ogiltigt värde i anropet";
        return message(HttpStatus.BAD_REQUEST, message);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleConflict(UserAlreadyExistsException ex) {
        return message(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return message(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<Map<String, String>> handleResponseStatus(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.resolve(ex.getStatusCode().value());
        return message(status != null ? status : HttpStatus.BAD_REQUEST, ex.getReason());
    }

    // Spring Securitys @PreAuthorize kastar undantaget inne i controllern, så det måste fångas här.
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, String>> handleAccessDenied(AccessDeniedException ex) {
        return message(HttpStatus.FORBIDDEN, "Du har inte behörighet till den här resursen");
    }

    @ExceptionHandler({AuthenticationException.class, JwtException.class})
    public ResponseEntity<Map<String, String>> handleUnauthorized(Exception ex) {
        return message(HttpStatus.UNAUTHORIZED, "Inloggningen är ogiltig eller har gått ut");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleUnexpected(Exception ex) {
        log.error("Oväntat fel", ex);
        return message(HttpStatus.INTERNAL_SERVER_ERROR, "Något gick fel. Försök igen senare.");
    }

    private ResponseEntity<Map<String, String>> message(HttpStatus status, String text) {
        Map<String, String> body = new LinkedHashMap<>();
        body.put("message", text != null ? text : status.getReasonPhrase());
        return ResponseEntity.status(status).body(body);
    }
}
