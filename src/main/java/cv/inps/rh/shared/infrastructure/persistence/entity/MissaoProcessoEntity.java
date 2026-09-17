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
@Table(name = "RH_T_MISSAO_PROCESSO")
public class MissaoProcessoEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_missao_processo")
  @SequenceGenerator(name = "seq_missao_processo", sequenceName = "SEQ_MISSAO_PROCESSO", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @NotNull(message = "missaoServId is mandatory")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "missao_serv_id", referencedColumnName = "id", nullable = false)
  private MissaoServicoEntity missaoServId;

  // Domínio TIPO_PROCESSO: BILHETE_PASSAGEM | SEGURO_VIAGEM | AJUDA_CUSTO | ALOJAMENTO
  @NotBlank(message = "tipoProcesso is mandatory")
  @Column(name = "tipo_processo", length = 100, nullable = false)
  private String tipoProcesso;

  // Domínio TIPO_PROCESSO_ETAPA (referência MISSAO_SERVICO)
  @NotBlank(message = "etapa is mandatory")
  @Column(name = "etapa", length = 100, nullable = false)
  private String etapa;

  @NotBlank(message = "estado is mandatory")
  @Column(name = "estado", length = 1, nullable = false)
  private String estado;

  @NotNull(message = "uuid is mandatory")
  @Column(name = "uuid", nullable = false, length = 36)
  private UUID uuid;
}
