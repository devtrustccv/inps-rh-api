package cv.inps.rh.shared.domain.exceptions;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
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

    var body = ex.getBody();
    body.setProperty("igrpType", "validation");

    return body;
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
    problem.setTitle("Erro de dados");

    if (rootCause instanceof SQLException sqlEx) {
      String msg = sqlEx.getMessage();
      if (msg != null) {
        if (msg.contains("ORA-01400")) {
          // ORA-01400: cannot insert NULL into ("SCHEMA"."TABLE"."COLUMN")
          String column = extractOraColumn(msg);
          problem.setDetail(column != null
              ? "Campo obrigatório em falta: '" + column.toLowerCase() + "'"
              : "Campo obrigatório em falta.");
        } else if (msg.contains("ORA-02291")) {
          // ORA-02291: integrity constraint (SCHEMA.FK_NAME) violated - parent key not found
          String constraint = extractOraConstraint(msg);
          problem.setDetail(constraint != null
              ? "Referência inválida (constraint: " + constraint + "): o valor indicado não existe."
              : "Referência inválida: o valor indicado não existe na tabela relacionada.");
        } else if (msg.contains("ORA-00001")) {
          // ORA-00001: unique constraint (SCHEMA.UK_NAME) violated
          String constraint = extractOraConstraint(msg);
          problem.setDetail(constraint != null
              ? "Valor duplicado (constraint: " + constraint + "): já existe um registo com este valor."
              : "Já existe um registo com este valor.");
        } else {
          problem.setDetail(msg);
        }
      } else {
        problem.setDetail("Erro interno de base de dados.");
      }
    } else {
      problem.setDetail(ex.getMostSpecificCause().getMessage());
    }

    LOGGER.error("DataIntegrityViolationException: {}", ex.getMostSpecificCause().getMessage());
    return problem;
  }

  /** Extrai o nome da coluna de mensagens ORA-01400: ...("SCHEMA"."TABLE"."COLUMN") */
  private String extractOraColumn(String message) {
    int start = message.lastIndexOf(".\"");
    int end = message.lastIndexOf("\")");
    if (start != -1 && end > start + 2) {
      return message.substring(start + 2, end);
    }
    return null;
  }

  /** Extrai o nome da constraint de mensagens ORA-02291/ORA-00001: ...(SCHEMA.CONSTRAINT)... */
  private String extractOraConstraint(String message) {
    int start = message.indexOf('(');
    int end = message.indexOf(')');
    if (start != -1 && end > start + 1) {
      String full = message.substring(start + 1, end); // e.g. "INPSRH.FK_FUN_LOCAL_NASC"
      int dot = full.indexOf('.');
      return dot != -1 ? full.substring(dot + 1) : full;
    }
    return null;
  }

  @ExceptionHandler(JpaObjectRetrievalFailureException.class)
  public ProblemDetail handleJpaObjectRetrievalFailure(JpaObjectRetrievalFailureException ex) {
    LOGGER.error(ex.getMessage(), ex);
    var problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    problem.setTitle("Entity not found");
    ex.getMostSpecificCause();
    var detail = ex.getMostSpecificCause().getMessage();
    problem.setDetail(detail);
    return problem;
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ProblemDetail handleEntityNotFound(EntityNotFoundException ex) {
    LOGGER.error(ex.getMessage(), ex);
    var problem = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    problem.setTitle("Entity not found");
    problem.setDetail(ex.getMessage());
    return problem;
  }



  private Throwable getRootCause(Throwable throwable) {
    Throwable cause = throwable.getCause();
    return (cause == null || cause == throwable) ? throwable : getRootCause(cause);
  }

}
