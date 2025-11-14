/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import cv.inps.rh.shared.application.constants.Estado;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_FAMILIARES")
public class FamiliarEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_familiar")
    @SequenceGenerator(name = "seq_familiar", sequenceName = "SEQ_FAMILIAR", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @Column(name="uuid")
    private UUID uuid;

  
    @NotNull(message = "tpDocumento is mandatory")


  @ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
    @JoinColumn(name = "tp_documento", referencedColumnName = "id")
    private TipoDocumentoEntity tpDocumento;
    @Column(name="num_documento")
    private String numDocumento;

  
    @Column(name="nome")
    private String nome;

  
    @Column(name="data_nascimento")
    private LocalDate dataNascimento;

  
    @Column(name="sexo")
    private String sexo;

  
    @Column(name="gdp_id")
    private String gdpId;

  
    @Column(name="dependencia")
    private String dependencia;

  
    @Column(name="membro_agr")
    private String membroAgr;

  
    @Column(name="nm_pai")
    private String nmPai;

  
    @Column(name="nm_mae")
    private String nmMae;

  
    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;

     @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "fun_id")
   private FuncionarioEntity funId;


}
