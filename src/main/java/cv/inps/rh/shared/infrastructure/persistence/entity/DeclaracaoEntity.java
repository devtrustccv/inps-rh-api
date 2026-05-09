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

import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_DECLARACAO")
public class DeclaracaoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_rh_t_declaracao")
    @SequenceGenerator(name = "seq_rh_t_declaracao", sequenceName = "SEQ_RH_T_DECLARACAO", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


  @NotNull(message = "funId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_id", referencedColumnName = "id")
    private FuncionarioEntity funId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiprel_id", referencedColumnName = "id")
    private TiposRelacionamentoEntity tiprelId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", referencedColumnName = "id")
    private PedidoEntity pedidoId;
    @Column(name="finalidade")
    private String finalidade;


  @Column(name="data_pedido")
    private LocalDate dataPedido;


  @Column(name="obs")
    private String obs;


  @Column(name="tipo_declaracao")
    private String tipoDeclaracao;


  @Column(name="decisao_analise")
    private String decisaoAnalise;


  @Column(name="obs_analise")
    private String obsAnalise;


  @Column(name="decisao_rh")
    private String decisaoRh;


  @Column(name="entrega")
    private String entrega;


  @Column(name="estado")
    private String estado;


  @Column(name="entidade_destinado")
    private String entidadeDestinado;


  @Column(name="uuid")
    private UUID uuid;


}
