package cv.inps.rh.shared.domain.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(IgrpResponseStatusException.class)
  public ProblemDetail handleIgrpResponseStatusException(IgrpResponseStatusException ex) {

    LOGGER.error(ex.getMessage(), ex);

    return ex.getBody();
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleIllegalArgumentException(IllegalArgumentException ex) {

    LOGGER.error(ex.getMessage(), ex);

    var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

    problemDetail.setTitle(ex.getMessage());

    return problemDetail;
  }

  @ExceptionHandler(ClassCastException.class)
  public ProblemDetail handleClassCastException(ClassCastException ex) {

    var stackTrace = ex.getStackTrace();

    var origin = stackTrace.length > 0 ? stackTrace[0] : null;

    var detailedMessage = ex.getMessage();

    if (origin != null) {
      detailedMessage += " at " + origin.getClassName() + "." + origin.getMethodName() +
          "(" + origin.getFileName() + ":" + origin.getLineNumber() + ")";
    }

    LOGGER.error("CLASS CAST EXCEPTION: {}", detailedMessage, ex);

    return ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
    var allErrors = ex.getBindingResult().getFieldErrors().stream()
        .map(fe -> String.format("Campo '%s': %s", fe.getField(), fe.getDefaultMessage()))
        .collect(Collectors.joining("; "));

    var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetail.setTitle("Erro de validação nos campos enviados");
    problemDetail.setDetail(allErrors);

    return problemDetail;
  }


  @ExceptionHandler(ConstraintViolationException.class)
  public ProblemDetail handleConstraintViolationException(ConstraintViolationException ex) {

    var allErrors = ex.getConstraintViolations().stream()
        .map(v -> String.format("Campo '%s': %s", v.getPropertyPath(), v.getMessage()))
        .collect(Collectors.joining("; "));

    var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetail.setTitle("Violação de restrição nos dados");
    problemDetail.setDetail(allErrors);

    return problemDetail;
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ProblemDetail handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {

    LOGGER.error("HTTP MESSAGE NOT READABLE EXCEPTION", ex);

    var problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);

    if (ex.getCause() instanceof InvalidFormatException ife && ife.getTargetType().isEnum()) {

      var targetType = ife.getTargetType();

      var allowedValues = Arrays.stream(targetType.getEnumConstants())
          .map(Object::toString)
          .toArray(String[]::new);

      problem.setTitle("Invalid value for enum type: " + targetType.getSimpleName());
      problem.setProperty("CurrentValue", ife.getValue());
      problem.setProperty("AllowedValues", allowedValues);
      return problem;
    }

    problem.setTitle("Malformed JSON request");
    problem.setDetail(ex.getMessage());

    return problem;
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ProblemDetail handleDataIntegrityViolation(DataIntegrityViolationException ex) {

    Throwable rootCause = getRootCause(ex);

    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problem.setTitle("Data Integrity Violation");

    if (rootCause instanceof SQLException sqlEx) {
      problem.setDetail(sqlEx.getMessage()); // retorna a mensagem completa do Oracle
    } else {
      problem.setDetail(ex.getMostSpecificCause().getMessage());
    }

    return problem;
  }

  private Throwable getRootCause(Throwable throwable) {
    Throwable cause = throwable.getCause();
    return (cause == null || cause == throwable) ? throwable : getRootCause(cause);
  }

}
