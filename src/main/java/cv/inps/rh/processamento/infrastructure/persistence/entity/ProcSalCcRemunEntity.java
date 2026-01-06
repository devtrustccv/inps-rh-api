package cv.inps.rh.processamento.infrastructure.persistence.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Mapping for DB view
 */
@Getter
@Setter
@Entity
@Immutable
@Table(name = "PROC_SAL_CC_REMUN")
public class ProcSalCcRemunEntity {
  @Id
  @Column(name = "CC_ID")
  private Long ccId;

  @Size(max = 4)
  @Column(name = "TIPO", length = 4)
  private String tipo;

  @Size(max = 150)
  @Column(name = "CENTRO_DE_CUSTO", length = 150)
  private String centroDeCusto;

  @NotNull
  @Column(name = "DATA_PROCESSAMENTO", nullable = false)
  private LocalDate dataProcessamento;

  @NotNull
  @Column(name = "REMUNERACAO_TRIBUTAVEL", nullable = false)
  private Long remuneracaoTributavel;

  @NotNull
  @Column(name = "VALOR", nullable = false)
  private Long valor;

  @NotNull
  @Column(name = "VALOR_DEF_REMUN", nullable = false, precision = 38, scale = 2)
  private BigDecimal valorDefRemun;

  @NotNull
  @Column(name = "TM_ID", nullable = false)
  private Long tmId;

  @Size(max = 4000)
  @Column(name = "OBS_RUB", length = 4000)
  private String obsRub;

  @Size(max = 100)
  @Column(name = "DESCRICAO", length = 100)
  private String descricao;

  @Size(max = 255)
  @NotNull
  @Column(name = "NOME", nullable = false)
  private String nome;

  @Size(max = 917)
  @Column(name = "NOME_CARGO_ESCALAO", length = 917)
  private String nomeCargoEscalao;

  @Size(max = 4000)
  @Column(name = "MORADA", length = 4000)
  private String morada;

  @Size(max = 255)
  @Column(name = "CATEGORIA")
  private String categoria;

  @Column(name = "TOTAL_LIQUIDO")
  private Long totalLiquido;

  @Column(name = "TOTAL_DESCONTOS")
  private Long totalDescontos;

  @Column(name = "TOTAL_REMUNERACOES")
  private Long totalRemuneracoes;

  @NotNull
  @Column(name = "FUN_ID", nullable = false)
  private Long funId;

  @Size(max = 255)
  @NotNull
  @Column(name = "FUNC_BI", nullable = false)
  private String funcBi;

  @Column(name = "ITEM_ACTO")
  private Long itemActo;

  @Column(name = "ITEM_ACTO_NOVO")
  private Long itemActoNovo;

  @Size(max = 3)
  @Column(name = "TRIBUTAVEL", length = 3)
  private String tributavel;

  @NotNull
  @Column(name = "PROC_SAL_ID", nullable = false)
  private Long procSalId;

  @Size(max = 3)
  @Column(name = "SOCIAL", length = 3)
  private String social;

  @Column(name = "VALOR_REAL")
  private Long valorReal;

  @Column(name = "TOTAL_SEG_SOCIAL")
  private Long totalSegSocial;

  @Size(max = 3)
  @Column(name = "DET_TIPO", length = 3)
  private String detTipo;

  @Size(max = 6)
  @Column(name = "SHORT_DESC", length = 6)
  private String shortDesc;

  @NotNull
  @Column(name = "PROC_FUNC_ID", nullable = false)
  private Long procFuncId;

  @NotNull
  @Column(name = "REGISTO_ID", nullable = false)
  private Long registoId;

  @Size(max = 3)
  @Column(name = "FAVOR_ESTADO", length = 3)
  private String favorEstado;

  @Size(max = 200)
  @NotNull
  @Column(name = "PROCESSAMENTO", nullable = false, length = 200)
  private String processamento;

  @NotNull
  @Column(name = "TIPREL_ID", nullable = false)
  private Long tiprelId;

  @Column(name = "NIF")
  private Long nif;

  @Size(max = 200)
  @Column(name = "EMAIL", length = 200)
  private String email;

  @Size(max = 21)
  @Column(name = "NIB", length = 21)
  private String nib;

  @NotNull
  @Column(name = "PROC_CC_ID", nullable = false)
  private Long procCcId;

  @Column(name = "NU_CONTA")
  private Long nuConta;

  @Column(name = "RHB_ID")
  private Long rhbId;

  @Size(max = 100)
  @Column(name = "NM_BANCO", length = 100)
  private String nmBanco;

  @Size(max = 10)
  @Column(name = "CD_BANCO", length = 10)
  private String cdBanco;

  @Size(max = 10)
  @Column(name = "SIGLA", length = 10)
  private String sigla;

  @Column(name = "BANCO_ID")
  private Long bancoId;

  @Column(name = "ENT_BAN")
  private Long entBan;

  @Size(max = 500)
  @Column(name = "OBS", length = 500)
  private String obs;

  @Column(name = "CAB_IAC_ID")
  private Long cabIacId;

  @Size(max = 200)
  @Column(name = "IAC_DESCRICAO", length = 200)
  private String iacDescricao;

  @Lob
  @Column(name = "NIB_REF")
  private String nibRef;

  @Lob
  @Column(name = "ENTIDADE_REF")
  private String entidadeRef;

  @Lob
  @Column(name = "ENT_REF")
  private String entRef;

  @Size(max = 4000)
  @Column(name = "ENDERECO", length = 4000)
  private String endereco;

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
