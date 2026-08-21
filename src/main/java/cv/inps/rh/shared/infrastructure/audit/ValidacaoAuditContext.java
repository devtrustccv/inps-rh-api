package cv.inps.rh.shared.infrastructure.audit;

import java.util.UUID;

/**
 * Contexto request-scoped que liga um commit do JaVers à validação em curso.
 *
 * <p>O problema: o {@link org.javers.spring.auditable.CommitPropertiesProvider} é global (um único
 * bean, chamado em <em>qualquer</em> commit auto-audit) e não recebe o {@code VALIDACAO_ID} como
 * argumento. Este holder é a ponte: o write-service preenche-o no momento em que conhece/cria a
 * {@code ValidacaoEntity}, e o provider lê-o para carimbar cada commit com a validação correta.
 *
 * <p>É {@link ThreadLocal} porque uma validação vive inteira numa só thread/transação. Tem de ser
 * limpo no fim do request ({@link #clear()}) — o {@link ValidacaoAuditContextFilter} garante-o para
 * o caso de o service falhar antes de limpar.
 */
public final class ValidacaoAuditContext {

  /** Snapshot imutável da validação a que as próximas alterações pertencem. */
  public record Ref(Long validacaoId, UUID validacaoUuid, String tabela) {}

  private static final ThreadLocal<Ref> CURRENT = new ThreadLocal<>();

  private ValidacaoAuditContext() {}

  /** Marca a validação a que os commits seguintes (nesta thread) devem ser atados. */
  public static void set(Long validacaoId, UUID validacaoUuid, String tabela) {
    CURRENT.set(new Ref(validacaoId, validacaoUuid, tabela));
  }

  /** A validação corrente, ou {@code null} se o fluxo atual não é uma validação. */
  public static Ref current() {
    return CURRENT.get();
  }

  public static void clear() {
    CURRENT.remove();
  }
}
