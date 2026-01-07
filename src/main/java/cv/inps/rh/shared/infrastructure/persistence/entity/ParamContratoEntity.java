/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import jakarta.validation.constraints.NotBlank;
import cv.inps.rh.shared.application.constants.Estado;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PARAM_CONTRATO")
public class ParamContratoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_param_contrato")
    @SequenceGenerator(name = "seq_param_contrato", sequenceName = "seq_param_contrato", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @Column(name="uuid")
    private UUID uuid;

  
    @NotBlank(message = "codigo is mandatory")
    @Column(name="codigo", nullable = false)
    private String codigo;

  
    @Column(name="nome")
    private String nome;

  
    @Column(name="natureza")
    private String natureza;

  
    @Column(name="flg_renovavel")
    private Integer flgRenovavel;

  
    @Column(name="duracao_renovavel")
    private Integer duracaoRenovavel;

  
    @Column(name="prazo_obrigatorio")
    private Integer prazoObrigatorio;

  
    @Column(name="max_renovacao")
    private Integer maxRenovacao;

  


  
    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;

  
}