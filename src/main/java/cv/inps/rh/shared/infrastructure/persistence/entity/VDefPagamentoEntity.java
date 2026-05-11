/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.Estado;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Immutable
@Table(name = "RH_V_DEF_PAGAMENTO")
public class VDefPagamentoEntity {

  @Id
  @Column(name = "PAG_ID")
  private Long pagId;

  @Column(name = "TIPREL_ID")
  private Long tiprelId;

  @Column(name = "FUN_ID")
  private Long funId;

  @Column(name = "FUN_UUID")
  private UUID funUuid;

  @Enumerated(EnumType.STRING)
  @Column(name = "ESTADO")
  private Estado estado;

  @Column(name = "VALOR")
  private BigDecimal valor;

  @Column(name = "DATA_INICIO")
  private LocalDate dataInicio;

  @Column(name = "DATA_FIM")
  private LocalDate dataFim;

  @Column(name = "DATA_ULTIMO_PROC")
  private LocalDate dataUltimoProc;

  @Column(name = "OBS", length = 4000)
  private String obs;

  @Column(name = "PERCENTAGEM")
  private BigDecimal percentagem;

  @Column(name = "USER_ALTERACAO_ID")
  private Long userAlteracaoId;

  @Column(name = "USER_ALTERACAO_NAME")
  private String userAlteracaoName;

  @Column(name = "USER_REGISTO_ID")
  private Long userRegistoId;

  @Column(name = "USER_REGISTO_NAME")
  private String userRegistoName;

  @Column(name = "UUID")
  private UUID uuid;

  @Column(name = "DATA_REGISTO")
  private LocalDateTime dataRegisto;

  @Column(name = "TM_ID")
  private Long tmId;

  @Column(name = "DESCRICAO")
  private String descricao;

  @Column(name = "EST_ACT_ADM")
  private Integer estActAdm;

  @Column(name = "CONTR_VINCULO_ID")
  private Long contrVinculoId;

  @Column(name = "CARREIRA_ID")
  private Long carreiraId;

  @Column(name = "SITUAC_LABORAL_ID")
  private Long situacLaboralId;

}
