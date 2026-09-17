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

import java.util.UUID;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_MISSAO_REQUISICAO_COLAB")
public class MissaoRequisicaoColabEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_missao_requisicao_colab")
  @SequenceGenerator(name = "seq_missao_requisicao_colab", sequenceName = "SEQ_MISSAO_REQUISICAO_COLAB", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @NotNull(message = "missaoRequisicaoId is mandatory")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "missao_requisicao_id", referencedColumnName = "id", nullable = false)
  private MissaoRequisicaoEntity missaoRequisicaoId;

  @NotNull(message = "missaoColabId is mandatory")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "missao_colab_id", referencedColumnName = "id", nullable = false)
  private MissaoColaboradorEntity missaoColabId;

  @NotBlank(message = "estado is mandatory")
  @Column(name = "estado", length = 1, nullable = false)
  private String estado;

  @NotNull(message = "uuid is mandatory")
  @Column(name = "uuid", nullable = false, length = 36)
  private UUID uuid;
}
