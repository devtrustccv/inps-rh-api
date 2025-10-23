/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_VALIDACAO")
public class ValidacaoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotBlank(message = "tipoAccao is mandatory")
    @Column(name="tipo_accao", nullable = false)
    private String tipoAccao;

  
    @Column(name="referencia_name")
    private String referenciaName;

  
    @Column(name="referencia_id")
    private Long referenciaId;

  


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fund_id", referencedColumnName = "id")
    private FuncionarioEntity fundId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiprel_id", referencedColumnName = "id")
    private TiposRelacionamentoEntity tiprelId;
    @Column(name="estado")
    private String estado;

  
    @Column(name="obs")
    private String obs;

  
    @Column(name="uuid")
    private UUID uuid;

  
}