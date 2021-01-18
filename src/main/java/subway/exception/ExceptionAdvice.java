package subway.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice()
public class ExceptionAdvice {

    @ExceptionHandler(DuplicateNameException.class)
    public ResponseEntity handleDuplicate() {
        return ResponseEntity.status(500).body("Duplicate Exception");
    }

    @ExceptionHandler(NoContentException.class)
    public ResponseEntity handleNoContent() {
        return ResponseEntity.status(500).body("No Content");
    }

    @ExceptionHandler(TwoStationException.class)
    public ResponseEntity handleTwoStationException() {
        return ResponseEntity.status(500).body("TwoStation Exception");
    }
}
