package factorystudios.web;

import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Traduce le eccezioni in risposte JSON coerenti invece di stack trace
 * o errori 500 generici, come richiesto dal punto 4.2 ("valida i
 * parametri in ingresso e gestisci gli errori").
 */
@RestControllerAdvice
public class GestoreErrori {

    // Dimensione di aggregazione non nell'allowlist (vedi AggregazioniRepository)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> gestisciParametroNonValido(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpoErrore(ex.getMessage()));
    }

    // Parametri di paginazione/filtro fuori dai vincoli @Min/@Max
    @ExceptionHandler(jakarta.validation.ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> gestisciVincoloNonValido(
            jakarta.validation.ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpoErrore(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> gestisciBodyNonValido(MethodArgumentNotValidException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(corpoErrore(ex.getMessage()));
    }

    private Map<String, Object> corpoErrore(String messaggio) {
        return Map.of(
                "timestamp", Instant.now().toString(),
                "errore", messaggio
        );
    }
}
