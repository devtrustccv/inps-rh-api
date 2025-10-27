package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.parametrizacao.domain.models.ParamContrato;
import cv.inps.rh.parametrizacao.domain.models.ParamVinculo;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Contrato {

  private Long id;
  private IdentificadorUnico uuid;
  private Estado estado;
  private LocalDate dataInicio;
  private LocalDate dataFim;
  private Integer duracao;
  private Integer versao;
  private String tpContrato;
  private String situacaoLaboral;
  private String obs;

  private ParamVinculo vinculo;
  private ParamContrato tpContratoParam;

  private List<Contrato> contratosFilhos;

  private Contrato( Long id,
                    IdentificadorUnico uuid,
                    Estado estado,
                    LocalDate dataInicio,
                    LocalDate dataFim,
                    Integer duracao,
                    Integer versao,
                    String tpContrato,
                    String situacaoLaboral,
                    String obs,
                    ParamVinculo vinculo,
                    ParamContrato tpContratoParam,
                    List<Contrato> contratosFilhos) {
    this.id = id;
    this.uuid = uuid;
    this.estado = estado;
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
    this.duracao = duracao;
    this.versao = versao;
    this.tpContrato = tpContrato;
    this.situacaoLaboral = situacaoLaboral;
    this.obs = obs;
    this.vinculo = vinculo;
    this.tpContratoParam = tpContratoParam;
    this.contratosFilhos = contratosFilhos != null ? contratosFilhos : new ArrayList<>();
  }

  // Criação de novo contrato
  public static Contrato create( Estado estado,
                                 LocalDate dataInicio,
                                 LocalDate dataFim,
                                 Integer duracao,
                                 Integer versao,
                                 String tpContrato,
                                 String situacaoLaboral,
                                 String obs,
                                 ParamVinculo vinculo,
                                 ParamContrato tpContratoParam) {
    return new Contrato(null, IdentificadorUnico.create(), estado, dataInicio, dataFim,
        duracao, versao, tpContrato, situacaoLaboral, obs, vinculo, tpContratoParam, new ArrayList<>());
  }

  // Reconstrução a partir da entity
  public static Contrato rebuild(Long id, UUID uuid,
                                 Estado estado,
                                 LocalDate dataInicio,
                                 LocalDate dataFim,
                                 Integer duracao,
                                 Integer versao,
                                 String tpContrato,
                                 String situacaoLaboral,
                                 String obs,
                                 ParamVinculo vinculo,
                                 ParamContrato tpContratoParam,
                                 List<Contrato> contratosFilhos) {
    return new Contrato(id, IdentificadorUnico.from(uuid), estado, dataInicio, dataFim,
        duracao, versao, tpContrato, situacaoLaboral, obs, vinculo, tpContratoParam, contratosFilhos);
  }

  public void update(LocalDate dataInicio,
                     LocalDate dataFim,
                     Integer duracao,
                     Integer versao,
                     String tpContrato,
                     String situacaoLaboral,
                     String obs,
                     ParamVinculo vinculo,
                     ParamContrato tpContratoParam) {

    if (dataInicio != null) this.dataInicio = dataInicio;
    if (dataFim != null) this.dataFim = dataFim;
    if (duracao != null) this.duracao = duracao;
    if (versao != null) this.versao = versao;
    if (tpContrato != null) this.tpContrato = tpContrato;
    if (situacaoLaboral != null) this.situacaoLaboral = situacaoLaboral;
    if (obs != null) this.obs = obs;
    if (vinculo != null) this.vinculo = vinculo;
    if (tpContratoParam != null) this.tpContratoParam = tpContratoParam;
  }

  public void addContratoFilho(Contrato contratoFilho) {
    if (contratoFilho != null) this.contratosFilhos.add(contratoFilho);
  }
}
