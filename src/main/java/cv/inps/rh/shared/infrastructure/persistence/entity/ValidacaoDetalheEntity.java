package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Uma linha por campo alterado, conforme a spec DOSSIÊ:
 * <em>"faz registo de cada campo alterado"</em> (linha 2089) e, no exemplo do Inativar/Ativar,
 * <em>"Faz 2 registo 1 para estado e outro para OBS"</em> (linha 2534).
 *
 * <p>Alimenta a grelha {@code ### Detalhe de alterações} (linha 1559), filtrada por
 * {@code VALIDACAO_ID = RH_T_VALIDACAO.ID} (linha 1612).
 *
 * <p>Escrita exclusivamente pelo {@code AlteracoesValidadasListener} — nenhum service deve
 * instanciar isto diretamente.
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_VALIDACAO_DETALHE")
public class ValidacaoDetalheEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_validacao_detalhe")
  @SequenceGenerator(name = "seq_validacao_detalhe", sequenceName = "SEQ_VALIDACAO_DETALHE", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "validacao_id", referencedColumnName = "id", nullable = false)
  private ValidacaoEntity validacaoId;

  /** O RÓTULO mostrado ao aprovador (ex.: "Direcção"). É o que o frontend lê — não mudar. */
  @Column(name = "campo_alterado", nullable = false, length = 100)
  private String campoAlterado;

  /**
   * Nome técnico da propriedade (ex.: {@code instidId}), separado do rótulo. Permite agrupar,
   * filtrar e re-renderizar sem fazer parsing do texto visível; e se o rótulo mudar amanhã, os
   * registos antigos continuam a dizer o que diziam.
   */
  @Column(name = "campo", length = 100)
  private String campo;

  /** Ordem de apresentação na grelha = ordem de declaração dos campos no módulo. */
  @Column(name = "ordem")
  private Integer ordem;

  /** VALOR | REFERENCIA | INICIAL — ver {@code CampoAlterado.Tipo}. */
  @Column(name = "tipo_alteracao", length = 20)
  private String tipoAlteracao;

  /**
   * Nullable, ao contrário do que a spec de BD indica (obrigatório): um campo que estava vazio e
   * passou a ter valor é uma alteração legítima e não teria como ser representada.
   */
  @Column(name = "valor_anterior", length = 2000)
  private String valorAnterior;

  @Column(name = "valor_novo", length = 2000)
  private String valorNovo;

  /**
   * Id da FK que deu origem ao {@code valorAnterior}/{@code valorNovo}, quando o campo é uma
   * referência. Guardar o id além do nome é a rede: o nome é apresentação e pode mudar, o id é o que
   * identifica de facto a referência — e permite re-renderizar se a resolução do nome falhar.
   */
  @Column(name = "valor_anterior_id")
  private Long valorAnteriorId;

  @Column(name = "valor_novo_id")
  private Long valorNovoId;

  /** Distingue as tabelas quando uma validação atravessa várias (spec linha 1643). */
  @Column(name = "tabela_name", length = 50)
  private String tabelaName;

  @Column(name = "tabela_id")
  private Long tabelaId;

  @Column(name = "uuid")
  private UUID uuid;
}
