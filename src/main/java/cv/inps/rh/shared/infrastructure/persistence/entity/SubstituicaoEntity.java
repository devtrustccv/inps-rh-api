/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;
import cv.inps.rh.shared.application.constants.Estado;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_SUBSTITUICAO")
public class SubstituicaoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_substituicao")
    @SequenceGenerator(name = "seq_substituicao", sequenceName = "SEQ_SUBSTITUICAO", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotNull(message = "tiprelIdPara is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiprel_id_para", referencedColumnName = "id")
    private TiposRelacionamentoEntity tiprelIdPara;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiprel_id_de", referencedColumnName = "id")
    private TiposRelacionamentoEntity tiprelIdDe;
    @Column(name="data_inicio")
    private LocalDate dataInicio;

  
    @Column(name="data_fim")
    private LocalDate dataFim;

  
    @Column(name="motivo", length=200)
    private String motivo;

  
    @Column(name="obs", length=4000)
    private String obs;

  
    @Column(name="uuid")
    private UUID uuid;

  
    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;

  
}