/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_CARREIRA")
public class CarreiraEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotNull(message = "salario is mandatory")
    @Column(name="salario", nullable = false)
    private BigDecimal salario;

  
    @Column(name="flg_processa")
    private Integer flgProcessa;

  
    @Column(name="tipo_situacao")
    private String tipoSituacao;

  
    @Column(name="estado")
    private String estado;

  
    @Column(name="obs", length=4000)
    private String obs;

  
    @Column(name="uuid")
    private UUID uuid;

  


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", referencedColumnName = "id")
    private ContratoEntity contratoId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_id", referencedColumnName = "id")
    private FuncionarioEntity funId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cargo_id", referencedColumnName = "id")
    private ParamCargoEntity cargoId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "escalao_id", referencedColumnName = "id")
    private ParamEscalaoEntity escalaoId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", referencedColumnName = "id")
    private ParamCategoriaEntity categoriaId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "carr_pccs_id", referencedColumnName = "id")
    private ParamCarreiraEntity carrPccsId;
}