package cv.inps.rh.shared.domain.exceptions;

import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

/** Tradução dos erros Oracle de constraint em mensagens para o utilizador (nunca o ORA cru). */
class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

  private ProblemDetail traduzir(String oraMessage) {
    var ex = new DataIntegrityViolationException("could not execute statement",
        new RuntimeException("wrapper", new SQLException(oraMessage)));
    return handler.handleDataIntegrityViolation(ex);
  }

  @Test
  void checkConstraintDePeriodo() {
    var p = traduzir("ORA-02290: check constraint (INPSRH.CK_EXP_PROF_PERIODO) violated\n"
        + "https://docs.oracle.com/error-help/db/ora-02290/");
    assertThat(p.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
    assertThat(p.getDetail())
        .isEqualTo("A data de fim não pode ser anterior à data de início (Experiência Profissional).")
        .doesNotContain("ORA-");
    assertThat(p.getProperties()).containsEntry("constraint", "CK_EXP_PROF_PERIODO");
  }

  @Test
  void checkConstraintDeEstadoEDesconhecida() {
    assertThat(traduzir("ORA-02290: check constraint (INPSRH.CK_CONTR_ESTADO) violated").getDetail())
        .isEqualTo("Estado inválido (Contrato).");
    assertThat(traduzir("ORA-02290: check constraint (INPSRH.CK_QUALQUER_COISA) violated").getDetail())
        .isEqualTo("Os dados enviados não respeitam as regras definidas.");
  }

  @Test
  void outrosCodigos() {
    assertThat(traduzir("ORA-12899: value too large for column \"INPSRH\".\"RH_T_EXP_PROF\".\"FUNCAO\" (actual: 300, maximum: 255)")
        .getDetail()).isEqualTo("O valor do campo 'funcao' é demasiado longo.");
    assertThat(traduzir("ORA-01400: cannot insert NULL into (\"INPSRH\".\"RH_T_EXP_PROF\".\"PAIS_ID\")").getDetail())
        .isEqualTo("Campo obrigatório em falta: 'pais_id'.");
    assertThat(traduzir("ORA-02292: integrity constraint (INPSRH.FK_X) violated - child record found").getDetail())
        .isEqualTo("Não é possível concluir a operação: o registo está a ser utilizado noutros dados.");
    assertThat(traduzir("ORA-99999: algo inesperado").getDetail()).doesNotContain("ORA-");
  }
}
