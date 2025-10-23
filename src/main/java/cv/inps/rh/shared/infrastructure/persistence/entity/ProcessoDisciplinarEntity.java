/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME. */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_PROCESSO_DISCIPLINAR")
public class ProcessoDisciplinarEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @NotNull(message = "tiprelId is mandatory")


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tiprel_id", referencedColumnName = "id")
    private TiposRelacionamentoEntity tiprelId;


  @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_id", referencedColumnName = "id")
    private FuncionarioEntity funId;
    @Column(name="num_proceso")
    private String numProceso;

  
    @Column(name="entidade")
    private String entidade;

  
    @Column(name="tp_processo")
    private String tpProcesso;

  
    @Column(name="estado")
    private String estado;

  
    @Column(name="pena_discp")
    private String penaDiscp;

  
    @Column(name="date_inic_pd")
    private LocalDate dateInicPd;

  
    @Column(name="date_fim_pd")
    private LocalDate dateFimPd;

  
    @Column(name="date_inic_pena")
    private LocalDate dateInicPena;

  
    @Column(name="date_fim_pena")
    private LocalDate dateFimPena;

  
    @Column(name="num_bo")
    private String numBo;

  
    @Column(name="data_publ_bo")
    private LocalDate dataPublBo;

  
    @Column(name="num_ordem_serv")
    private String numOrdemServ;

  
    @Column(name="data_ordem_serv")
    private LocalDate dataOrdemServ;

  
    @Column(name="num_ofa")
    private String numOfa;

  
    @Column(name="data_emiss_ofa")
    private LocalDate dataEmissOfa;

  
    @Column(name="obs", length=4000)
    private String obs;

  
    @Column(name="uuid")
    private UUID uuid;

  
}