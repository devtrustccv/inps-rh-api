/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_MISSAO_PRESTADOR_AVAL")
public class MissaoPrestadorAvalEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_missao_prestador_aval")
  @SequenceGenerator(name = "seq_missao_prestador_aval", sequenceName = "SEQ_MISSAO_PRESTADOR_AVAL", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @NotNull(message = "missaoPrestId is mandatory")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "missao_prest_id", referencedColumnName = "id", nullable = false)
  private MissaoPrestadorEntity missaoPrestId;

  // Critérios: descrição da avaliação do domínio AVALIACAO_FORNECEDOR (referência AVALIACAO)
  @NotBlank(message = "sistemaQualidade is mandatory")
  @Column(name = "sistema_qualidade", length = 50, nullable = false)
  private String sistemaQualidade;

  @NotBlank(message = "prazoFornecimento is mandatory")
  @Column(name = "prazo_fornecimento", length = 50, nullable = false)
  private String prazoFornecimento;

  @NotBlank(message = "qualidadeProduto is mandatory")
  @Column(name = "qualidade_produto", length = 50, nullable = false)
  private String qualidadeProduto;

  @NotBlank(message = "capacidadeResposta is mandatory")
  @Column(name = "capacidade_resposta", length = 50, nullable = false)
  private String capacidadeResposta;

  @NotBlank(message = "preco is mandatory")
  @Column(name = "preco", length = 50, nullable = false)
  private String preco;

  // Soma ponderada dos cinco critérios (0–100)
  @NotNull(message = "total is mandatory")
  @Column(name = "total", nullable = false)
  private BigDecimal total;

  // Classe A–D (domínio AVALIACAO_FORNECEDOR, referência DESIGNACAO)
  @NotBlank(message = "designacao is mandatory")
  @Column(name = "designacao", length = 50, nullable = false)
  private String designacao;

  @NotBlank(message = "estado is mandatory")
  @Column(name = "estado", length = 1, nullable = false)
  private String estado;

  @NotNull(message = "uuid is mandatory")
  @Column(name = "uuid", nullable = false, length = 36)
  private UUID uuid;
}
