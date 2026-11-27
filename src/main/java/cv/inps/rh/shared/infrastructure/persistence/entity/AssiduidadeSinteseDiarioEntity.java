/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import cv.inps.rh.shared.infrastructure.persistence.type.IntervalDsStringJdbcType;
import org.hibernate.annotations.JdbcType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


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


  @JdbcType(IntervalDsStringJdbcType.class)
  @Column(name = "hora_primeira_entrada", columnDefinition = IntervalDsStringJdbcType.DDL)
    private String horaPrimeiraEntrada;


  @JdbcType(IntervalDsStringJdbcType.class)
  @Column(name = "hora_ultima_saida", columnDefinition = IntervalDsStringJdbcType.DDL)
    private String horaUltimaSaida;


  @JdbcType(IntervalDsStringJdbcType.class)
  @Column(name = "horas_trabalhadas", columnDefinition = IntervalDsStringJdbcType.DDL)
    private String horasTrabalhadas;


  @JdbcType(IntervalDsStringJdbcType.class)
  @Column(name = "hora_primeira_saida_almoco", columnDefinition = IntervalDsStringJdbcType.DDL)
    private String horaPrimeiraSaidaAlmoco;


  @JdbcType(IntervalDsStringJdbcType.class)
  @Column(name = "hora_ultima_entrada_almoco", columnDefinition = IntervalDsStringJdbcType.DDL)
    private String horaUltimaEntradaAlmoco;


  @JdbcType(IntervalDsStringJdbcType.class)
  @Column(name = "horas_almoco", columnDefinition = IntervalDsStringJdbcType.DDL)
    private String horasAlmoco;


  @JdbcType(IntervalDsStringJdbcType.class)
  @Column(name = "horas_extras", columnDefinition = IntervalDsStringJdbcType.DDL)
    private String horasExtras;


  @JdbcType(IntervalDsStringJdbcType.class)
  @Column(name = "horas_ausencia", columnDefinition = IntervalDsStringJdbcType.DDL)
    private String horasAusencia;


  @Column(name="falta")
    private Integer falta;


  @Column(name="estado")
    private String estado;


  @Column(name="flag_rececao")
    private String flagRececao;


  /**
   * Origem do registo: {@code MANUAL} (marcado pelo RH) ou {@code AUTOMATICO}
   * (importado do relógio de ponto via IMPORT_DADOS_CONTR_ACESSO).
   */
  @Column(name="forma")
    private String forma;


}
