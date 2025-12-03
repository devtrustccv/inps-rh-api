/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_ENDERECO")
public class EnderecoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_endereco")
    @SequenceGenerator(name = "seq_endereco", sequenceName = "SEQ_ENDERECO", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @NotNull(message = "paisId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pais_id", referencedColumnName = "id")
    private GeografiaEntity paisId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ilha_id", referencedColumnName = "id")
    private GeografiaEntity ilhaId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concelho_id", referencedColumnName = "id")
    private GeografiaEntity concelhoId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "freguesia_id", referencedColumnName = "id")
    private GeografiaEntity freguesiaId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "zona_id", referencedColumnName = "id")
    private GeografiaEntity zonaId;
    @Column(name="morada")
    private String morada;


    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


    @Column(name="uuid")
    private UUID uuid;





    @OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinColumn(name = "fun_id", unique = true, referencedColumnName = "id")
    private FuncionarioEntity funId;
}
