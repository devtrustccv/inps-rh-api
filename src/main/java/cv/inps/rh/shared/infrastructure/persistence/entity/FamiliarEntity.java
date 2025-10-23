/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotNull(message = "tpDocumento is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tp_documento", referencedColumnName = "id")
    private TipoDocumentoEntity tpDocumento;
    @Column(name="num_documento")
    private String numDocumento;

  
    @Column(name="nome")
    private String nome;

  
    @Column(name="data_nascimento")
    private String dataNascimento;

  
    @Column(name="sexo")
    private String sexo;

  
    @Column(name="gpd_id")
    private String gpdId;

  
    @Column(name="dependencia")
    private String dependencia;

  
    @Column(name="membro_agr")
    private String membroAgr;

  
    @Column(name="nm_pai")
    private String nmPai;

  
    @Column(name="nm_mae")
    private String nmMae;

  
    @Column(name="estado")
    private String estado;

  
    @Column(name="uuid")
    private UUID uuid;

     @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "fun_id")
   private FuncionarioEntity funId;


}