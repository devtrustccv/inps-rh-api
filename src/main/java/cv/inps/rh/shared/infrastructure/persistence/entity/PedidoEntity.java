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

import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PEDIDO")
public class PedidoEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_pedido")
  @SequenceGenerator(name = "seq_pedido", sequenceName = "SEQ_PEDIDO", allocationSize = 1)
  @Column(name = "id", unique = true, nullable = false)
  private Long id;




  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "fun_id", referencedColumnName = "id")
  private FuncionarioEntity funId;
  @Column(name = "tipo_pedido")
  private String tipoPedido;


  @Column(name = "origem")
  private String origem;


  @Column(name = "uuid")
  private UUID uuid;


  @Column(name = "etapa")
  private String etapa;

  @Column(name = "estado")
  private String estado;


}
