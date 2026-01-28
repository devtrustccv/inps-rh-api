/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import cv.inps.rh.shared.application.constants.Estado;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_ASSIDUIDADE_SINTESE_DIARIA")
public class AssiduidadeSinteseDiarioEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "rh_assi_sintese_diario_seq")
    @SequenceGenerator(name = "rh_assi_sintese_diario_seq", sequenceName = "RH_ASSI_SINTESE_DIARIO_SEQ", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotNull(message = "funcionarioId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id", referencedColumnName = "id")
    private FuncionarioEntity funcionarioId;
    @Column(name="data")
    private LocalDate data;

  
    @Column(name="mes")
    private Integer mes;

  
    @Column(name="ano")
    private Integer ano;

  
    @Column(name="hora_primeira_entrada")
    private String horaPrimeiraEntrada;

  
    @Column(name="hora_ultima_saida")
    private String horaUltimaSaida;

  
    @Column(name="horas_trabalhadas")
    private String horasTrabalhadas;

  
    @Column(name="hora_primeira_saida_almoco")
    private String horaPrimeiraSaidaAlmoco;

  
    @Column(name="hora_ultima_entrada_almoco")
    private String horaUltimaEntradaAlmoco;

  
    @Column(name="horas_almoco")
    private String horasAlmoco;

  
    @Column(name="horas_extras")
    private String horasExtras;

  
    @Column(name="horas_ausencia")
    private String horasAusencia;

  
    @Column(name="falta")
    private String falta;

  
    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;

  
    @Column(name="flag_rececao")
    private String flagRececao;

  
}