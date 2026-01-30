/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import java.time.LocalDate;
import java.math.BigDecimal;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_V_FALTA_MENSAL")
public class VfaltaMensalEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotNull(message = "funcionarioId is mandatory")
    @Column(name="funcionario_id", nullable = false)
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

  
    @Column(name="cargo_id")
    private Long cargoId;

  
    @Column(name="nome_cargo")
    private String nomeCargo;

  
    @Column(name="ano")
    private Integer ano;

  
    @Column(name="mes")
    private Integer mes;

  
    @Column(name="data_inicio")
    private LocalDate dataInicio;

  
    @Column(name="data_fim")
    private LocalDate dataFim;

  
    @Column(name="tot_faltas")
    private Integer totFaltas;

  
    @Column(name="tot_hor_aus")
    private BigDecimal totHorAus;

  
    @Column(name="tot_val_desc")
    private BigDecimal totValDesc;

  
    @Column(name="tot_inj")
    private Integer totInj;

  
    @Column(name="tot_jus")
    private Integer totJus;

  
    @Column(name="est_mensal")
    private String estMensal;

  
    @Column(name="est_proc")
    private String estProc;

  
    @Column(name="flg_desc_sal")
    private String flgDescSal;

  
}