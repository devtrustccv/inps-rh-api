/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_V_FERIAS_MENSAL")
public class VFeriasMensalEntity  {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotNull(message = "uuidFuncionario is mandatory")
    @Column(name="uuid_funcionario", nullable = false)
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

  
    @Column(name="vinculo")
    private String vinculo;

  
    @Column(name="categoria")
    private String categoria;

  
    @Column(name="total_direito")
    private Integer totalDireito;

  
    @Column(name="total_direito_ano")
    private Integer totalDireitoAno;

  
    @Column(name="total_planeado")
    private Integer totalPlaneado;

  
    @Column(name="total_gozado")
    private Integer totalGozado;

  
    @Column(name="ano")
    private Integer ano;

  
    @Column(name="estado")
    private String estado;

  
    @Column(name="estado_desc")
    private String estadoDesc;

  
}