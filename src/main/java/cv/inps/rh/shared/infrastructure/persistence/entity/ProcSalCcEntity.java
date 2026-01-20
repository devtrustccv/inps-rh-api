package cv.inps.rh.shared.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@Immutable
@Table(name = "PROC_SAL_CC")
public class ProcSalCcEntity {

  @Id
  @Size(max = 4)
  @Column(name = "TIPO", length = 4)
  private String tipo;

  @Size(max = 150)
  @Column(name = "CENTRO_DE_CUSTO", length = 150)
  private String centroDeCusto;

  @Column(name = "DATA_PROCESSAMENTO")
  private LocalDate dataProcessamento;

  @Column(name = "REMUNERACAO_TRIBUTAVEL")
  private Long remuneracaoTributavel;

  @Column(name = "VALOR")
  private Long valor;

  @Column(name = "VALOR_DEF_REMUN", precision = 38, scale = 2)
  private BigDecimal valorDefRemun;

  @Column(name = "TM_ID")
  private Long tmId;

  @Size(max = 4000)
  @Column(name = "OBS_RUB", length = 4000)
  private String obsRub;

  @Size(max = 145)
  @Column(name = "DESCRICAO", length = 145)
  private String descricao;

  @Size(max = 255)
  @Column(name = "NOME")
  private String nome;

  @Size(max = 918)
  @Column(name = "NOME_CARGO_ESCALAO", length = 918)
  private String nomeCargoEscalao;

  @Column(name = "TOTAL_LIQUIDO")
  private Long totalLiquido;

  @Column(name = "TOTAL_DESCONTOS")
  private Long totalDescontos;

  @Column(name = "TOTAL_REMUNERACOES")
  private Long totalRemuneracoes;

  @Column(name = "CC_ID")
  private Long ccId;

  @Column(name = "FUN_ID")
  private Long funId;

  @Column(name = "ITEM_ACTO")
  private Long itemActo;

  @Size(max = 3)
  @Column(name = "TRIBUTAVEL", length = 3)
  private String tributavel;

  @Column(name = "PROC_SAL_ID")
  private Long procSalId;

  @Size(max = 3)
  @Column(name = "SOCIAL", length = 3)
  private String social;

  @Column(name = "VALOR_REAL")
  private Long valorReal;

  @Column(name = "TOTAL_SEG_SOCIAL")
  private Long totalSegSocial;

  @Size(max = 4)
  @Column(name = "DET_TIPO", length = 4)
  private String detTipo;

  @Size(max = 6)
  @Column(name = "SHORT_DESC", length = 6)
  private String shortDesc;

  @Column(name = "PROC_FUNC_ID")
  private Long procFuncId;

  @Column(name = "REGISTO_ID")
  private Long registoId;

  @Size(max = 3)
  @Column(name = "FAVOR_ESTADO", length = 3)
  private String favorEstado;

  @Size(max = 40)
  @Column(name = "PERC_IMPOSTO", length = 40)
  private String percImposto;

  @Size(max = 200)
  @Column(name = "PROCESSAMENTO", length = 200)
  private String processamento;

  @Column(name = "TIPREL_ID")
  private Long tiprelId;

  @Column(name = "NIF")
  private Long nif;

  @Size(max = 21)
  @Column(name = "NIB", length = 21)
  private String nib;

  @Size(max = 200)
  @Column(name = "EMAIL", length = 200)
  private String email;

  @Column(name = "PROC_CC_ID")
  private Long procCcId;

  @Column(name = "NU_CONTA")
  private Long nuConta;

  @Column(name = "RHB_ID")
  private Long rhbId;

  @Size(max = 100)
  @Column(name = "NM_BANCO", length = 100)
  private String nmBanco;

  @Column(name = "BAN_ID")
  private Long banId;

  @Size(max = 10)
  @Column(name = "SIGLA", length = 10)
  private String sigla;

  @Size(max = 500)
  @Column(name = "OBS", length = 500)
  private String obs;

  @Size(max = 21)
  @Column(name = "NIB_REF", length = 21)
  private String nibRef;

  @Size(max = 200)
  @Column(name = "ENTIDADE_REF", length = 200)
  private String entidadeRef;

  @Column(name = "ENT_REF")
  private Long entRef;

  @Size(max = 11)
  @Column(name = "CONTA_REF", length = 11)
  private String contaRef;

  @Size(max = 100)
  @Column(name = "NM_BANCO_BEN", length = 100)
  private String nmBancoBen;

  @Size(max = 255)
  @Column(name = "FUNC_BI")
  private String funcBi;

  @Size(max = 150)
  @Column(name = "LOCAL_TRAB", length = 150)
  private String localTrab;

  @Column(name = "LOCAL_ID")
  private Long localId;

  @Size(max = 200)
  @Column(name = "CD_INSTIT", length = 200)
  private String cdInstit;

  @Size(max = 200)
  @Column(name = "CD_LOCAL", length = 200)
  private String cdLocal;

  @Column(name = "DT_INI_LOC_TRAB")
  private LocalDate dtIniLocTrab;

  @Column(name = "DT_FIM_LOC_TRAB")
  private LocalDate dtFimLocTrab;

  @Column(name = "CAB_ID")
  private Long cabId;

  @Size(max = 6)
  @Column(name = "PERIODO", length = 6)
  private String periodo;

  @Size(max = 255)
  @Column(name = "CARGO")
  private String cargo;

  @Size(max = 255)
  @Column(name = "RELACAO")
  private String relacao;

  @Size(max = 140)
  @Column(name = "ESCALAO", length = 140)
  private String escalao;


}
