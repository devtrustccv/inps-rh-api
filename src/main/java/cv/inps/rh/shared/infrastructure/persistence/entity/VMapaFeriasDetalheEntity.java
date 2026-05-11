/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
@Table(name = "RH_V_MAPA_FERIAS_DETALHE")
public class VMapaFeriasDetalheEntity  {

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


    @Column(name="direcao_id")
    private Long direcaoId;


    @Column(name="direcao")
    private String direcao;


    @Column(name="secao_id")
    private Long secaoId;


    @Column(name="secao")
    private String secao;


    @Column(name="ilha_id")
    private Long ilhaId;


    @Column(name="ilha")
    private String ilha;


    @Column(name="ano_id")
    private Long anoId;


    @Column(name="ano_referente")
    private Integer anoReferente;


    @Column(name="total_direito")
    private Integer totalDireito;


    @Column(name="total_direito_ano")
    private Integer totalDireitoAno;


    @Column(name="data_inicio_mapa")
    private LocalDate dataInicioMapa;


    @Column(name="data_fim_mapa")
    private LocalDate dataFimMapa;


}
