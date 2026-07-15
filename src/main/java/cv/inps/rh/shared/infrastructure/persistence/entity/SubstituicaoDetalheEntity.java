package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.application.constants.Estado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Detalhe mensal de uma substituição (RH_T_SUBSTITUICAO_DETALHE): um registo por mês do período,
 * com o nº de dias e os valores do substituto/substituído — o mesmo cálculo do endpoint/proc
 * CALCULAR_SUBSTITUICAO. Caso de uso "Substituição".
 */
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_SUBSTITUICAO_DETALHE")
public class SubstituicaoDetalheEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_substituicao_detalhe")
  @SequenceGenerator(name = "seq_substituicao_detalhe", sequenceName = "SEQ_SUBSTITUICAO_DETALHE", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @Column(name = "mes_ano", nullable = false)
  private String mesAno;

  @Column(name = "nr_dias", nullable = false)
  private Integer nrDias;

  @Column(name = "valor_do_substituto", nullable = false)
  private BigDecimal valorDoSubstituto;

  @Column(name = "valor_do_substituido", nullable = false)
  private BigDecimal valorDoSubstituido;

  // Diferença salarial proporcional aos dias do mês (proc CALCULAR_SUBSTITUICAO) — o valor a
  // favor do substituto. Coluna acrescentada à RH_T_SUBSTITUICAO_DETALHE.
  @Column(name = "valor_diferenca")
  private BigDecimal valorDiferenca;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "substituicao_id", referencedColumnName = "id", nullable = false)
  private SubstituicaoEntity substituicaoId;

  @Enumerated(EnumType.STRING)
  @Column(name = "estado")
  private Estado estado;

  @Column(name = "data_registo")
  private LocalDateTime dataRegisto;

  @Column(name = "user_registo_id")
  private Long userRegistoId;

}
