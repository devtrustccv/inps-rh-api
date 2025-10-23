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
@Table(name = "RH_T_ORDEM_SERVICO")
public class OrdemServicoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotBlank(message = "nuOrdem is mandatory")
    @Column(name="nu_ordem", nullable = false)
    private String nuOrdem;

  
    @Column(name="descricao")
    private String descricao;

  
    @Column(name="referente")
    private String referente;

  


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_id", referencedColumnName = "id")
    private FuncionarioEntity funId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiprel_id", referencedColumnName = "id")
    private TiposRelacionamentoEntity tiprelId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", referencedColumnName = "id")
    private ContratoEntity contratoId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "validacao_id", referencedColumnName = "id")
    private ValidacaoEntity validacaoId;
    @Column(name="estado")
    private String estado;

  
    @Column(name="obs", length=4000)
    private String obs;

  
    @Column(name="uuid")
    private UUID uuid;

  
}