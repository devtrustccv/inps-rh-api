/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.LocalDate;
import cv.inps.rh.shared.application.constants.Estado;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_ESCALAO")
public class ParamEscalaoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotNull(message = "uuid is mandatory")
    @Column(name="uuid", nullable = false)
    private UUID uuid;

  
    @Column(name="codigo")
    private String codigo;

  


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param_carr_id", referencedColumnName = "id")
    private ParamCarreiraEntity paramCarrId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "param_categoria_id", referencedColumnName = "id")
    private ParamCategoriaEntity paramCategoriaId;
    @Column(name="nivel_referencia")
    private Integer nivelReferencia;

  
    @Column(name="escalao")
    private String escalao;

  
    @Column(name="valor")
    private BigDecimal valor;

  
    @Column(name="data_inicio")
    private LocalDate dataInicio;

  
    @Column(name="data_fim")
    private LocalDate dataFim;

  
    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;

  
}