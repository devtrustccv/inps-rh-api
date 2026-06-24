/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_RESPONSAVEL")
public class ResponsavelEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_responsavel")
  @SequenceGenerator(name = "seq_responsavel", sequenceName = "SEQ_RESPONSAVEL", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;

  @NotNull(message = "institId is mandatory")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "instit_id", referencedColumnName = "id")
  private DirecaoEntity institId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fun_id", referencedColumnName = "id")
  private FuncionarioEntity funId;

  @Column(name = "email")
  private String email;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "secao_id", referencedColumnName = "id")
  private SecaoEntity secaoId;
}
