/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;
import java.time.LocalDate;
import java.math.BigDecimal;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_V_HORA_EXTRA_MENSAL")
public class VHoraExtraMensalEntity  {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotBlank(message = "funcionarioId is mandatory")
    @Column(name="funcionario_id", nullable = false)
    private String funcionarioId;

  
    @Column(name="funcionario_uuid")
    private UUID funcionarioUuid;

  
    @Column(name="nome_funcionario")
    private String nomeFuncionario;

  
    @Column(name="cargo_id")
    private Long cargoId;

  
    @Column(name="nome_cargo")
    private String nomeCargo;

  
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

  
    @Column(name="data_inicio")
    private LocalDate dataInicio;

  
    @Column(name="data_fim")
    private LocalDate dataFim;

  
    @Column(name="horas_contratado_diario")
    private BigDecimal horasContratadoDiario;

  
    @Column(name="horas_contratado_mensal")
    private BigDecimal horasContratadoMensal;

  
    @Column(name="horas_trabalho")
    private BigDecimal horasTrabalho;

  
    @Column(name="salario_mensal")
    private BigDecimal salarioMensal;

  
    @Column(name="valor_horas_mensal")
    private BigDecimal valorHorasMensal;

  
    @Column(name="valor_horas_diario")
    private BigDecimal valorHorasDiario;

  
    @Column(name="percentagem")
    private Integer percentagem;

  
    @Column(name="pedido_id")
    private Long pedidoId;

  
    @Column(name="estado")
    private String estado;

  
    @Column(name="estado_desc")
    private String estadoDesc;

  
}