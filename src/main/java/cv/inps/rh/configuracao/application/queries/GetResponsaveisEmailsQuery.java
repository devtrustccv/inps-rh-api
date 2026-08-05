package cv.inps.rh.configuracao.application.queries;

import cv.igrp.framework.core.domain.Query;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Emails de RH_T_RESPONSAVEL para o multiselect do ecrã de notificação.
 *
 * <p>Os três filtros são opcionais e alternativos. O {@code funcionarioId} é o caminho normal:
 * o ecrã conhece o colaborador do registo, não a direção onde ele está colocado. Os outros dois
 * existem para quem já tem os ids em mão; sem nenhum, devolve todos os responsáveis.</p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetResponsaveisEmailsQuery implements Query {

  /** UUID do colaborador; a direção/secção é deduzida da mobilidade activa. */
  private String funcionarioId;

  private Long idInstituicao;

  private Long idSeccao;
}
