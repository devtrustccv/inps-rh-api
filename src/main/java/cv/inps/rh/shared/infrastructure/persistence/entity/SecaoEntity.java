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
@Table(name = "RH_T_SECAO")
public class SecaoEntity extends AuditEntity {

    @Id
    @SequenceGenerator(name = "seq_secao", sequenceName = "SEQ_SECAO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_secao")
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @Column(name="uuid")
    private UUID uuid;


    @NotBlank(message = "nome is mandatory")
    @Column(name="nome", nullable = false)
    private String nome;




  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "instit_id", referencedColumnName = "id")
    private InstituicaoEntity instId;
    @Enumerated(EnumType.STRING)
    @Column(name="estado")
    private Estado estado;


    @Column(name="nome_normalizado")
    private String nomeNormalizado;


}
