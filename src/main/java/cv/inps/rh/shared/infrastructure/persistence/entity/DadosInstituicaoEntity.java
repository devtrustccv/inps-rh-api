package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "RH_T_DADOS_INSTITUICAO")
public class DadosInstituicaoEntity extends AuditEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "RH_T_DADOS_INSTITUICAO_id_gen")
  @SequenceGenerator(name = "RH_T_DADOS_INSTITUICAO_id_gen", sequenceName = "SEQ_DADOS_INSTITUICAO", allocationSize = 1)
  @Column(name = "ID", nullable = false)
  private Long id;

  @Size(max = 200)
  @NotNull
  @Column(name = "NOME", nullable = false, length = 200)
  private String nome;

  @NotNull
  @Column(name = "NIF", nullable = false)
  private Long nif;

  @Size(max = 200)
  @Column(name = "COD_CAE", length = 200)
  private String codCae;

  @Size(max = 300)
  @Column(name = "ATIVIDADE_ECONOMICA", length = 300)
  private String atividadeEconomica;

  @Size(max = 100)
  @Column(name = "NUM_CERTIDAO_COMERCIAL", length = 100)
  private String numCertidaoComercial;

  @Column(name = "DATA_VALIDADE")
  private LocalDate dataValidade;

  @Column(name = "TELEFONE")
  private Long telefone;

  @Size(max = 300)
  @Column(name = "LOCALIDADE", length = 300)
  private String localidade;

  @Size(max = 200)
  @Column(name = "EMAIL", length = 200)
  private String email;

  @Size(max = 300)
  @Column(name = "MORADA", length = 300)
  private String morada;

  @Column(name = "CONCELHO_ID")
  private Long concelhoId;

  @Size(max = 1)
  @NotNull
  @Column(name = "ESTADO", nullable = false, length = 1)
  private String estado;

  @Size(max = 100)
  @NotNull
  @Column(name = "UUID", nullable = false, length = 100)
  private String uuid;
}
