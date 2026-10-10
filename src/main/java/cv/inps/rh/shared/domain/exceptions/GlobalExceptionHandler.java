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
    LOGGER.error("DataIntegrityViolationException: {}", ex.getMostSpecificCause().getMessage());

    if (rootCause instanceof SQLException sqlEx) {
      return problemaBaseDados(sqlEx);
    }
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problem.setTitle("Erro de dados");
    problem.setDetail("Os dados enviados não são válidos.");
    problem.setProperty("igrpType", "validation");
    return problem;
  }

  /**
   * Erros de base de dados que chegam embrulhados noutras exceções (ex. a violação só rebenta no
   * commit da transação → TransactionSystemException / JpaSystemException). Só os traduz quando a
   * causa raiz é uma violação de dados conhecida (ORA de constraint/valor); o resto mantém o
   * tratamento por omissão (500).
   */
  @ExceptionHandler({org.springframework.transaction.TransactionSystemException.class,
      org.springframework.orm.jpa.JpaSystemException.class})
  public ProblemDetail handleErroBaseDadosEmbrulhado(RuntimeException ex) {
    Throwable rootCause = getRootCause(ex);
    if (rootCause instanceof SQLException sqlEx && codigoOra(sqlEx.getMessage()) != null
        && ORA_VIOLACAO_DADOS.contains(codigoOra(sqlEx.getMessage()))) {
      LOGGER.error("Violação de dados ({}): {}", ex.getClass().getSimpleName(), sqlEx.getMessage());
      return problemaBaseDados(sqlEx);
    }
    LOGGER.error(ex.getMessage(), ex);
    var problem = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    problem.setTitle("Erro interno");
    problem.setDetail("Ocorreu um erro inesperado ao gravar os dados.");
    return problem;
  }

  /** Códigos ORA que correspondem a dados inválidos enviados pelo utilizador (→ 400). */
  private static final java.util.Set<String> ORA_VIOLACAO_DADOS = java.util.Set.of(
      "ORA-00001", "ORA-01400", "ORA-01407", "ORA-01438", "ORA-02290", "ORA-02291", "ORA-02292", "ORA-12899");

  /**
   * Traduz um erro Oracle numa mensagem para o utilizador. A mensagem ORA crua (com link para a
   * documentação da Oracle) nunca é devolvida ao cliente — fica só no log.
   */
  private ProblemDetail problemaBaseDados(SQLException sqlEx) {
    ProblemDetail problem = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problem.setTitle("Erro de dados");
    problem.setProperty("igrpType", "validation");

    String msg = sqlEx.getMessage();
    String codigo = codigoOra(msg);
    String constraint = msg != null ? extractOraConstraint(msg) : null;

    String detail;
    if ("ORA-01400".equals(codigo) || "ORA-01407".equals(codigo)) {
      // ORA-01400: cannot insert NULL into ("SCHEMA"."TABLE"."COLUMN")
      String column = extractOraColumn(msg);
      detail = column != null
          ? "Campo obrigatório em falta: '" + column.toLowerCase() + "'."
          : "Campo obrigatório em falta.";
    } else if ("ORA-02290".equals(codigo)) {
      // ORA-02290: check constraint (SCHEMA.CK_NAME) violated
      detail = mensagemCheckConstraint(constraint);
    } else if ("ORA-02291".equals(codigo)) {
      // ORA-02291: integrity constraint (SCHEMA.FK_NAME) violated - parent key not found
      detail = "Referência inválida: o valor indicado não existe.";
    } else if ("ORA-02292".equals(codigo)) {
      // ORA-02292: integrity constraint (SCHEMA.FK_NAME) violated - child record found
      detail = "Não é possível concluir a operação: o registo está a ser utilizado noutros dados.";
    } else if ("ORA-00001".equals(codigo)) {
      // ORA-00001: unique constraint (SCHEMA.UK_NAME) violated
      detail = "Já existe um registo com estes dados.";
    } else if ("ORA-12899".equals(codigo)) {
      // ORA-12899: value too large for column "SCHEMA"."TABLE"."COLUMN" (actual: X, maximum: Y)
      String column = extractOraColumnValueTooLarge(msg);
      detail = column != null
          ? "O valor do campo '" + column.toLowerCase() + "' é demasiado longo."
          : "Um dos valores indicados é demasiado longo.";
    } else if ("ORA-01438".equals(codigo)) {
      detail = "Um dos valores numéricos indicados é demasiado grande.";
    } else {
      detail = "Os dados enviados não respeitam as regras da base de dados.";
    }

    problem.setDetail(detail);
    if (constraint != null) problem.setProperty("constraint", constraint);
    return problem;
  }

  /**
   * Mensagem para violações de CHECK constraint, a partir da convenção de nomes do schema
   * (CK_&lt;ENTIDADE&gt;_&lt;REGRA&gt;): _PERIODO/_PD/_PENA = data fim &lt; data início, _ESTADO = estado
   * inválido, _PERC = percentagem fora de 0..100, _DURACAO = duração negativa.
   */
  private String mensagemCheckConstraint(String constraint) {
    if (constraint == null) {
      return "Os dados enviados não respeitam as regras definidas.";
    }
    String ck = constraint.toUpperCase();
    String entidade = entidadeDaConstraint(ck);
    String contexto = entidade != null ? " (" + entidade + ")" : "";

    if (ck.endsWith("_PERIODO") || ck.endsWith("_PD") || ck.endsWith("_PENA")) {
      return "A data de fim não pode ser anterior à data de início" + contexto + ".";
    }
    if (ck.endsWith("_ESTADO")) {
      return "Estado inválido" + contexto + ".";
    }
    if (ck.endsWith("_PERC")) {
      return "A percentagem tem de estar entre 0 e 100" + contexto + ".";
    }
    if (ck.endsWith("_DURACAO")) {
      return "A duração não pode ser negativa" + contexto + ".";
    }
    return "Os dados enviados não respeitam as regras definidas" + contexto + ".";
  }

  /** Nome legível da entidade a partir do prefixo da constraint (CK_EXP_PROF_PERIODO → Experiência Profissional). */
  private static String entidadeDaConstraint(String ck) {
    String nome = ck.replaceFirst("^(CK|CHK)_", "");
    for (var e : ENTIDADES_CONSTRAINT.entrySet()) {
      if (nome.startsWith(e.getKey() + "_")) return e.getValue();
    }
    return null;
  }

  private static final java.util.Map<String, String> ENTIDADES_CONSTRAINT = new java.util.LinkedHashMap<>();
  static {
    ENTIDADES_CONSTRAINT.put("EXP_PROF", "Experiência Profissional");
    ENTIDADES_CONSTRAINT.put("HAB_LIT", "Habilitação Literária");
    ENTIDADES_CONSTRAINT.put("FORM_FEITO", "Formação Profissional");
    ENTIDADES_CONSTRAINT.put("CONTR", "Contrato");
    ENTIDADES_CONSTRAINT.put("DD_BANC", "Dados Bancários");
    ENTIDADES_CONSTRAINT.put("DEFREM", "Remuneração");
    ENTIDADES_CONSTRAINT.put("DEF_PAG", "Encargo/Desconto");
    ENTIDADES_CONSTRAINT.put("SIT_LAB", "Situação Laboral");
    ENTIDADES_CONSTRAINT.put("SUBSTIT", "Substituição");
    ENTIDADES_CONSTRAINT.put("RH_T_SUBSTITUICAO", "Substituição");
    ENTIDADES_CONSTRAINT.put("PROC_DISC", "Processo Disciplinar");
    ENTIDADES_CONSTRAINT.put("ESCALAO", "Escalão");
    ENTIDADES_CONSTRAINT.put("CARR", "Carreira");
    ENTIDADES_CONSTRAINT.put("MOB", "Mobilidade");
    ENTIDADES_CONSTRAINT.put("FAM", "Agregado Familiar");
    ENTIDADES_CONSTRAINT.put("DOC_PESS", "Documento Pessoal");
    ENTIDADES_CONSTRAINT.put("CONTACTO", "Contacto");
    ENTIDADES_CONSTRAINT.put("END", "Endereço");
  }

  /** Código ORA-NNNNN da mensagem (o primeiro que aparece), ou null. */
  private static String codigoOra(String message) {
    if (message == null) return null;
    var m = java.util.regex.Pattern.compile("ORA-\\d{5}").matcher(message);
    return m.find() ? m.group() : null;
  }

  /** Extrai a coluna de ORA-12899: value too large for column "SCHEMA"."TABLE"."COLUMN" (...) */
  private String extractOraColumnValueTooLarge(String message) {
    var m = java.util.regex.Pattern.compile("\"[^\"]+\"\\.\"[^\"]+\"\\.\"([^\"]+)\"").matcher(message);
    return m.find() ? m.group(1) : null;
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
