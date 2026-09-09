/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

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
@Table(name = "RH_T_FERIAS_MAPA")
public class FeriasMapaEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_ferias_mapa")
    @SequenceGenerator(name = "seq_ferias_mapa", sequenceName = "SEQ_FERIAS_MAPA", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


  @NotNull(message = "dataInicio is mandatory")
    @Column(name="data_inicio", nullable = false)
    private LocalDate dataInicio;


  @Column(name="data_fim")
    private LocalDate dataFim;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_id", referencedColumnName = "id")
    private FuncionarioEntity funId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ano_id", referencedColumnName = "id")
    private AnoEntity anoId;
    @Column(name="uuid")
    private UUID uuid;


  /**
   * Estado do mapa. A coluna é NOT NULL em BD e não estava mapeada — sem isto qualquer insert
   * falhava. Ao importar um mapa novo para o mesmo colaborador/ano, os anteriores passam a 'I' e
   * só o importado fica 'A' (regra da spec: "caso se importar um mapa que já existe, deve
   * inactivar o outro").
   */
  @Enumerated(EnumType.STRING)
    @Column(name="estado", nullable = false)
    private Estado estado;


}
