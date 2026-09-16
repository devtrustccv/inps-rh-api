package cv.inps.rh.shared.application.detalhe;

import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;

import java.util.List;

/**
 * Ponto de entrada único do "Detalhe de alterações". Qualquer módulo que submeta algo a validação
 * usa esta interface e não precisa de saber como o diff é feito nem onde é guardado.
 *
 * <p>Fluxo típico num write-service, dentro da mesma transação:
 * <pre>{@code
 * var campos = mobilidadeCampos.get();
 * var antes = detalhe.capturar(campos, mobilidade);   // ANTES de aplicar o payload
 * // ... aplicar payload, criar a validação ...
 * repo.save(mobilidade);
 * detalhe.congelar(validacao, "RH_T_MOBILIDADE", campos, antes, detalhe.capturar(campos, mobilidade));
 * }</pre>
 *
 * <p>A implementação vive em {@link DetalheAlteracoesService}. A interface existe para que trocar o
 * motor de comparação não obrigue a tocar em nenhum chamador.
 */
public interface DetalheAlteracoes {

  /**
   * Fotografa o estado actual da entidade — valores já formatados para exibição.
   *
   * <p><b>Tem de ser chamado ANTES de aplicar o payload.</b> O UPDATE dos módulos do dossiê é
   * <em>in place</em>: depois de aplicar, o estado anterior deixa de existir na base e o diff seria
   * vazio (a mesma instância gerida comparada consigo própria).
   *
   * @param alvo a entidade, ou {@code null} para o estado inexistente de uma criação
   */
  <T> Estado capturar(Campos<T> campos, T alvo);

  /** Diff entre dois estados capturados. Só campos que mudaram, pela ordem de declaração. */
  <T> List<CampoAlterado> comparar(Campos<T> campos, Estado antes, Estado depois);

  /**
   * Congela o diff em {@code RH_T_VALIDACAO_DETALHE} — uma linha por campo alterado.
   *
   * <p>Idempotente por (validação, tabela): um reenvio de correção apaga as linhas anteriores dessa
   * tabela e regrava, porque o detalhe antigo já não descreve o que o checker vai ver.
   *
   * @return número de linhas gravadas
   */
  <T> int congelar(ValidacaoEntity validacao, String tabela,
      Campos<T> campos, Estado antes, Estado depois);

  /**
   * Como {@link #congelar}, mas para módulos em que a validação cobre uma <b>coleção</b> de linhas da
   * mesma tabela (ex.: DADOS_BANCÁRIOS — vários NIBs sob uma só validação).
   *
   * <p>O {@code tabelaId} (id da linha concreta) entra na coluna {@code TABELA_ID} e passa a fazer
   * parte da chave de fusão. Sem ele, duas linhas a alterar o <em>mesmo</em> campo colidiam: a segunda
   * sobrepunha-se à primeira e a grelha perdia uma das alterações.
   *
   * <p>Chamar uma vez por linha da coleção; cada chamada só mexe nas suas próprias linhas de detalhe.
   */
  <T> int congelar(ValidacaoEntity validacao, String tabela, Long tabelaId,
      Campos<T> campos, Estado antes, Estado depois);

  /** Valores já formatados, alinhados por índice com {@code campos.lista()}. Imutável. */
  record Estado(List<Valor> valores) {
  }

  /** O valor de exibição e, quando o campo é uma FK, o id que lhe deu origem. */
  record Valor(String display, Long id) {

    static final Valor VAZIO = new Valor(null, null);

    boolean vazio() {
      return display == null && id == null;
    }
  }
}
