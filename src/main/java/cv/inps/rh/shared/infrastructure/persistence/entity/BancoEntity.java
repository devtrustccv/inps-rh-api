/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotBlank;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_BANCO")
public class BancoEntity  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotBlank(message = "cdBanco is mandatory")
    @Column(name="cd_banco", nullable = false)
    private String cdBanco;

  
    @Column(name="sigla")
    private String sigla;

  
    @Column(name="nm_banco")
    private String nmBanco;

  
    @Column(name="nu_conta")
    private Long nuConta;

  
    @Column(name="ent_id")
    private Long entId;

  
    @Column(name="nib")
    private String nib;

  
    @Column(name="tm_id")
    private Long tmId;

  
}