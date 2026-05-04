/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_DADOS_BANCARIOS")
public class DadosBancariosEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_dados_banco")
  @SequenceGenerator(name = "seq_dados_banco", sequenceName = "SEQ_DADOS_BANCO", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;


  @NotNull(message = "rhbId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "rhb_id", referencedColumnName = "id")
  private BancoEntity rhbId;
  @Column(name = "num_conta")
  private Long numConta;


  @Column(name = "data_inicio")
  private LocalDate dataInicio;


  @Column(name = "data_fim")
  private LocalDate dataFim;


  @Enumerated(EnumType.STRING)
  @Column(name = "estado")
  private Estado estado;


  @Column(name = "obs", length = 4000)
  private String obs;


  @Column(name = "nib", length = 4000)
  private String nib;


  @Column(name = "uuid")
  private UUID uuid;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fun_id")
  private FuncionarioEntity funId;


}
