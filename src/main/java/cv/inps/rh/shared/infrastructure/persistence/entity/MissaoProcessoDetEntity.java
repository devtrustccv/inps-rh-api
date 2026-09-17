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
@Table(name = "RH_T_MISSAO_PROCESSO_DET")
public class MissaoProcessoDetEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_missao_processo_det")
  @SequenceGenerator(name = "seq_missao_processo_det", sequenceName = "SEQ_MISSAO_PROCESSO_DET", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @NotNull(message = "missaoProcessoId is mandatory")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "missao_processo_id", referencedColumnName = "id", nullable = false)
  private MissaoProcessoEntity missaoProcessoId;

  // Domínio PARECER: FAVORAVEL | DESFAVORAVEL
  @NotBlank(message = "parecer is mandatory")
  @Column(name = "parecer", length = 50, nullable = false)
  private String parecer;

  @Column(name = "observacao", length = 500)
  private String observacao;

  // UGAL | COORDENADOR_RH | DIRECTOR_RH
  @NotBlank(message = "responsavel is mandatory")
  @Column(name = "responsavel", length = 50, nullable = false)
  private String responsavel;

  @NotBlank(message = "estado is mandatory")
  @Column(name = "estado", length = 1, nullable = false)
  private String estado;

  @NotNull(message = "uuid is mandatory")
  @Column(name = "uuid", nullable = false, length = 36)
  private UUID uuid;
}
