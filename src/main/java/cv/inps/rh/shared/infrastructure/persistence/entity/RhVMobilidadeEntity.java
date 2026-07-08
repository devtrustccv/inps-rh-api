/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

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
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Immutable
@Table(name = "RH_V_MOBILIDADE")
public class RhVMobilidadeEntity {

  @Id
  @Column(name = "mob_id", unique = true, nullable = false)
  private Long mobId;


  @Column(name = "fun_id")
  private Long funId;


  @Column(name = "fun_uuid")
  private String funUuid;


  @Column(name = "mob_uuid")
  private String mobUuid;


  @Column(name = "unidade_desc")
  private String unidadeDesc;


  @Column(name = "unidade_id")
  private Long unidadeId;


  @Column(name = "local_trab_nome")
  private String localTrabNome;


  @Column(name = "local_trab_id")
  private Long localTrabId;


  @Column(name = "direcao_nome")
  private String direcaoNome;


  @Column(name = "direcao_id")
  private Long direcaoId;


  @Column(name = "direcao_codigo")
  private String direcaoCodigo;


  @Column(name = "nome_funcionario")
  private String nomeFuncionario;


  @Column(name = "data_inicio")
  private LocalDate dataInicio;


  @Column(name = "data_fim")
  private LocalDate dataFim;


  @Column(name = "estado")
  private String estado;


  @Column(name = "obs")
  private String obs;


  @Column(name = "tipo_situacao")
  private String tipoSituacao;


  @Column(name = "tipo_situacao_desc")
  private String tipoSituacaoDesc;


}
