/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import java.time.LocalDate;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_V_FERIAS_VER_MAPA")
public class VFeriasDetalheColaboradorEntity  {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotNull(message = "funcionarioId is mandatory")
    @Column(name="funcionario_id", nullable = false)
    private Long funcionarioId;

  
    @Column(name="uuid_funcionario")
    private UUID uuidFuncionario;

  
    @Column(name="nome_colaborador")
    private String nomeColaborador;

  
    @Column(name="direcao")
    private String direcao;

  
    @Column(name="direcao_id")
    private Long direcaoId;

  
    @Column(name="ano_id")
    private Long anoId;

  
    @Column(name="ano_referente")
    private Integer anoReferente;

  
    @Column(name="ferias_marcadas_inicio")
    private LocalDate feriasMarcadasInicio;

  
    @Column(name="ferias_marcadas_fim")
    private LocalDate feriasMarcadasFim;

  
    @Column(name="ferias_gozadas_inicio")
    private LocalDate feriasGozadasInicio;

  
    @Column(name="ferias_gozadas_fim")
    private LocalDate feriasGozadasFim;

  
}