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
  private final Long id;
  private final IdentificadorUnico uuid;

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

  // 🔹 Mestre (null na primeira versão)
  private Contrato contratoMestre;

  // 🔹 Filhos (versões seguintes)
  private List<Contrato> contratosFilhos;

  private Contrato(Long id,
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
                   Contrato contratoMestre,
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
    this.contratoMestre = contratoMestre;
    this.contratosFilhos = contratosFilhos != null ? contratosFilhos : new ArrayList<>();
  }

  // 🔹 Criar primeira versão (mestre)
  public static Contrato create(LocalDate dataInicio,
                                LocalDate dataFim,
                                Integer duracao,
                                String tpContrato,
                                String situacaoLaboral,
                                ParamVinculo vinculo,
                                ParamContrato tpContratoParam) {
    return new Contrato(
        null,
        IdentificadorUnico.create(),
        Estado.P,
        dataInicio,
        dataFim,
        duracao,
        1,
        tpContrato,
        situacaoLaboral,
        "NOVO_CONTRATO",
        vinculo,
        tpContratoParam,
        null,               // sem contrato mestre
        new ArrayList<>()   // ainda não tem filhos
    );
  }

  // Reconstrução a partir da entidade persistida ou query do banco
  public static Contrato rebuild(Long id,
                                 UUID uuid,
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
                                 Contrato contratoMestre,
                                 List<Contrato> contratosFilhos) {
    return new Contrato(
        id,
        IdentificadorUnico.from(uuid),
        estado,
        dataInicio,
        dataFim,
        duracao,
        versao,
        tpContrato,
        situacaoLaboral,
        obs,
        vinculo,
        tpContratoParam,
        contratoMestre,
        contratosFilhos
    );
  }


  // 🔹 Criar versão filha
  public static Contrato createFilha(LocalDate dataInicio,
                                     LocalDate dataFim,
                                     Integer duracao,
                                     Integer versao,
                                     String tpContrato,
                                     String situacaoLaboral,
                                     ParamVinculo vinculo,
                                     ParamContrato tpContratoParam,
                                     Contrato contratoMestre) {
    var filha = new Contrato(
        null,
        IdentificadorUnico.create(),
        Estado.P,
        dataInicio,
        dataFim,
        duracao,
        versao,
        tpContrato,
        situacaoLaboral,
        "VERSAO_FILHA",
        vinculo,
        tpContratoParam,
        contratoMestre,
        new ArrayList<>()
    );

    // adiciona a filha na lista do mestre
    if (contratoMestre != null) {
      contratoMestre.getContratosFilhos().add(filha);
    }

    return filha;
  }

  // Atualização parcial
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

  // Soft delete
  public void eliminar() {
    this.estado = Estado.E;
  }

}
