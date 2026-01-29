/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.math.BigDecimal;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_V_RESUMO_ASSIDUIDADE")
public class VResumoAssiduidadeEntity extends AuditEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @Column(name="funcionario_id")
    private Long funcionarioId;

  
    @Column(name="funcionario_uuid")
    private UUID funcionarioUuid;

  
    @Column(name="nome_funcionario")
    private String nomeFuncionario;

  
    @Column(name="id_direcao")
    private Long idDirecao;

  
    @Column(name="nome_direcao")
    private String nomeDirecao;

  
    @Column(name="id_secao")
    private Long idSecao;

  
    @Column(name="nome_secao")
    private String nomeSecao;

  
    @Column(name="id_ilha")
    private Long idIlha;

  
    @Column(name="nome_ilha")
    private String nomeIlha;

  
    @Column(name="ano")
    private Integer ano;

  
    @Column(name="mes")
    private Integer mes;

  
    @Column(name="total_dias")
    private Integer totalDias;

  
    @Column(name="total_faltas")
    private Integer totalFaltas;

  
    @Column(name="horas_trabalhadas")
    private BigDecimal horasTrabalhadas;

  
    @Column(name="horas_almoco")
    private BigDecimal horasAlmoco;

  
    @Column(name="horas_extras")
    private BigDecimal horasExtras;

  
    @Column(name="horas_ausencia")
    private BigDecimal horasAusencia;

  
    @Column(name="estado")
    private String estado;

  
}