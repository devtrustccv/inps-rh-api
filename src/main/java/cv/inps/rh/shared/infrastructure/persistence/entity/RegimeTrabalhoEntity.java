/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_REGIME_TRAB")
public class RegimeTrabalhoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotBlank(message = "tipoRegime is mandatory")
    @Column(name="tipo_regime", nullable = false)
    private String tipoRegime;

  
    @Column(name="tipo_situacao")
    private String tipoSituacao;

  
    @Column(name="data_fim")
    private LocalDate dataFim;

  
    @Column(name="estado")
    private String estado;

  
    @Column(name="obs", length=4000)
    private String obs;

  


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_id", referencedColumnName = "id")
    private FuncionarioEntity funId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", referencedColumnName = "id")
    private ContratoEntity contratoId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiprel_id", referencedColumnName = "id")
    private TiposRelacionamentoEntity tiprelId;
}