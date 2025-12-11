/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.Estado;
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
@Table(name = "RH_EQUIP_CONTR_ACESSO")
public class EquipamentoEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_rh_equip_contr_acesso")
  @SequenceGenerator(name = "seq_rh_equip_contr_acesso", sequenceName = "SEQ_RH_EQUIP_CONTR_ACESSO", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;


  @NotNull(message = "uuid is mandatory")
  @Column(name = "uuid", nullable = false)
  private UUID uuid;


  @NotBlank(message = "idEquipamento is mandatory")
  @Column(name = "id_equipamento", nullable = false)
  private String idEquipamento;


  @NotBlank(message = "local is mandatory")
  @Column(name = "local", nullable = false)
  private String local;


  @NotBlank(message = "ipAddress is mandatory")
  @Column(name = "ip_address", nullable = false)
  private String ipAddress;


  @NotNull(message = "picagem is mandatory")
  @Column(name = "picagem", nullable = false)
  private Integer picagem;


  @Column(name = "tp_movimento")
  private String tpMovimento;


  @Column(name = "tp_movimento_desc")
  private String tpMovimentoDesc;


  @NotNull(message = "estado is mandatory")
  @Enumerated(EnumType.STRING)
  @Column(name = "estado", nullable = false)
  private Estado estado;


  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_ups", unique = true, referencedColumnName = "id")
  private UpsEntity idUps;


  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "id_local_trabalho", unique = true, referencedColumnName = "id")
  private ParamLocalTrabEntity idLocalTrabalho;
  @NotBlank(message = "tipo is mandatory")
  @Column(name = "tipo", nullable = false)
  private String tipo;


}
