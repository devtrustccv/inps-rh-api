package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Nível 1 da lista de hora extra — agregado por pedido, que é a unidade de validação.
 * O detalhe (colaborador × mês) vive em {@link VHoraExtraMensalEntity}.
 */
@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_V_HORA_EXTRA_PEDIDO")
public class VHoraExtraPedidoEntity {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;


    @Column(name = "pedido_id")
    private Long pedidoId;


    @Column(name = "pedido_uuid")
    private String pedidoUuid;


    @Column(name = "estado")
    private String estado;


    @Column(name = "estado_desc")
    private String estadoDesc;


    @Column(name = "etapa")
    private String etapa;


    @Column(name = "data_pedido")
    private LocalDate dataPedido;


    @Column(name = "periodo_inicio")
    private LocalDate periodoInicio;


    @Column(name = "periodo_fim")
    private LocalDate periodoFim;


    @Column(name = "total_colaboradores")
    private Integer totalColaboradores;


    @Column(name = "total_registos")
    private Integer totalRegistos;


    @Column(name = "total_meses")
    private Integer totalMeses;


    @Column(name = "valor_total")
    private BigDecimal valorTotal;


    /** Só vem preenchida quando é única em todo o pedido. */
    @Column(name = "id_direcao")
    private Long idDirecao;


    @Column(name = "nome_direcao")
    private String nomeDirecao;


    @Column(name = "id_secao")
    private Long idSecao;


    @Column(name = "nome_secao")
    private String nomeSecao;


    @Column(name = "id_ilha")
    private Long idIlha;


    @Column(name = "nome_ilha")
    private String nomeIlha;
}
