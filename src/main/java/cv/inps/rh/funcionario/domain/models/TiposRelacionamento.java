package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.parametrizacao.domain.models.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.domain.models.Instituicao;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
public class TiposRelacionamento {
  private final Long id;
  private final IdentificadorUnico uuid;

  private ParamCargo cargo;
  private Instituicao instituicao;
  private ParamVinculo vinculo;
  private Secao seccao;
  private ParamCategoria categoria;
  private ParamEscalao escalao;
  private ParamCarreira carrPcc;

  private BigDecimal salario;
  private String moeda;
  private String regime;
  private String tipoSituacao;

  private TiposRelacionamento tiprelAnterior;
  private String flgProcessa;
  private Estado estado;
  private String obs;

  private LocalDate dataInicio;
  private LocalDate dataFim;
  private LocalDate dataInicioContrato;
  private LocalDate dataFimContrato;

  private Contrato contrato;
  private Carreira carreira;
  private Mobilidade mobilidade;

  private ParamLocalTrab locTrab;
  private RegimeTrabalho regimeTrabalho;
  private ParamContrato tipoContrato;

  private String referente;
  private LocalDate ultProc;
  private String motivoSitLab;
  private ParamSitLaboral situacLaboral;
  private String tpContrato;

  // Construtor privado
  private TiposRelacionamento(
      Long id,
      IdentificadorUnico uuid,
      ParamCargo cargo,
      Instituicao instituicao,
      ParamVinculo vinculo,
      Secao seccao,
      ParamCategoria categoria,
      ParamEscalao escalao,
      ParamCarreira carrPcc,
      BigDecimal salario,
      String moeda,
      String regime,
      String tipoSituacao,
      TiposRelacionamento tiprelAnterior,
      String flgProcessa,
      Estado estado,
      String obs,
      LocalDate dataInicio,
      LocalDate dataFim,
      LocalDate dataInicioContrato,
      LocalDate dataFimContrato,
      Contrato contrato,
      Carreira carreira,
      Mobilidade mobilidade,
      ParamLocalTrab locTrab,
      RegimeTrabalho regimeTrabalho,
      ParamContrato tipoContrato,
      String referente,
      LocalDate ultProc,
      String motivoSitLab,
      ParamSitLaboral situacLaboral,
      String tpContrato
  ) {
    this.id = id;
    this.uuid = uuid;
    this.cargo = cargo;
    this.instituicao = instituicao;
    this.vinculo = vinculo;
    this.seccao = seccao;
    this.categoria = categoria;
    this.escalao = escalao;
    this.carrPcc = carrPcc;
    this.salario = salario;
    this.moeda = moeda;
    this.regime = regime;
    this.tipoSituacao = tipoSituacao;
    this.tiprelAnterior = tiprelAnterior;
    this.flgProcessa = flgProcessa;
    this.estado = estado;
    this.obs = obs;
    this.dataInicio = dataInicio;
    this.dataFim = dataFim;
    this.dataInicioContrato = dataInicioContrato;
    this.dataFimContrato = dataFimContrato;
    this.contrato = contrato;
    this.carreira = carreira;
    this.mobilidade = mobilidade;
    this.locTrab = locTrab;
    this.regimeTrabalho = regimeTrabalho;
    this.tipoContrato = tipoContrato;
    this.referente = referente;
    this.ultProc = ultProc;
    this.motivoSitLab = motivoSitLab;
    this.situacLaboral = situacLaboral;
    this.tpContrato = tpContrato;
  }

  // Factory para criar novo relacionamento
  public static TiposRelacionamento create(
      ParamCargo cargo,
      Instituicao instituicao,
      ParamVinculo vinculo,
      Secao seccao,
      ParamCategoria categoria,
      ParamEscalao escalao,
      ParamCarreira carrPcc,
      BigDecimal salario,
      String moeda,
      String regime,
      TiposRelacionamento tiprelAnterior,
      String flgProcessa,
      LocalDate dataInicio,
      LocalDate dataFim,
      Contrato contrato,
      Carreira carreira,
      Mobilidade mobilidade,
      ParamLocalTrab locTrab,
      RegimeTrabalho regimeTrabalho,
      ParamContrato tipoContrato,
      String referente,
      LocalDate ultProc,
      String motivoSitLab,
      ParamSitLaboral situacLaboral,
      String tpContrato
  ) {
    return new TiposRelacionamento(
        null,
        IdentificadorUnico.create(),
        cargo,
        instituicao,
        vinculo,
        seccao,
        categoria,
        escalao,
        carrPcc,
        salario,
        moeda,
        regime,
        "INICIO",
        tiprelAnterior,
        flgProcessa,
        Estado.P,
        "NOVO_CONTRATO",
        dataInicio,
        dataFim,
        dataInicio,
        dataFim,
        contrato,
        carreira,
        mobilidade,
        locTrab,
        regimeTrabalho,
        tipoContrato,
        referente,
        ultProc,
        motivoSitLab,
        situacLaboral,
        tpContrato
    );
  }

  // Reconstrução para repositório
  public static TiposRelacionamento rebuild(
      Long id,
      UUID uuid,
      ParamCargo cargo,
      Instituicao instituicao,
      ParamVinculo vinculo,
      Secao seccao,
      ParamCategoria categoria,
      ParamEscalao escalao,
      ParamCarreira carrPcc,
      BigDecimal salario,
      String moeda,
      String regime,
      String tipoSituacao,
      TiposRelacionamento tiprelAnterior,
      String flgProcessa,
      Estado estado,
      String obs,
      LocalDate dataInicio,
      LocalDate dataFim,
      LocalDate dataInicioContrato,
      LocalDate dataFimContrato,
      Contrato contrato,
      Carreira carreira,
      Mobilidade mobilidade,
      ParamLocalTrab locTrab,
      RegimeTrabalho regimeTrabalho,
      ParamContrato tipoContrato,
      String referente,
      LocalDate ultProc,
      String motivoSitLab,
      ParamSitLaboral situacLaboral,
      String tpContrato
  ) {
    return new TiposRelacionamento(
        id,
        IdentificadorUnico.from(uuid),
        cargo,
        instituicao,
        vinculo,
        seccao,
        categoria,
        escalao,
        carrPcc,
        salario,
        moeda,
        regime,
        tipoSituacao,
        tiprelAnterior,
        flgProcessa,
        estado,
        obs,
        dataInicio,
        dataFim,
        dataInicioContrato,
        dataFimContrato,
        contrato,
        carreira,
        mobilidade,
        locTrab,
        regimeTrabalho,
        tipoContrato,
        referente,
        ultProc,
        motivoSitLab,
        situacLaboral,
        tpContrato
    );
  }

  public void update(
      ParamCargo cargo,
      Instituicao instituicao,
      ParamVinculo vinculo,
      Secao seccao,
      ParamCategoria categoria,
      ParamEscalao escalao,
      ParamCarreira carrPcc,
      BigDecimal salario,
      String moeda,
      String regime,
      String tipoSituacao,
      TiposRelacionamento tiprelAnterior,
      String flgProcessa,
      String obs,
      LocalDate dataInicio,
      LocalDate dataFim,
      LocalDate dataInicioContrato,
      LocalDate dataFimContrato,
      Contrato contrato,
      Carreira carreira,
      Mobilidade mobilidade,
      ParamLocalTrab locTrab,
      RegimeTrabalho regimeTrabalho,
      ParamContrato tipoContrato,
      String referente,
      LocalDate ultProc,
      String motivoSitLab,
      ParamSitLaboral situacLaboral,
      String tpContrato
  ) {
    if (cargo != null) this.cargo = cargo;
    if (instituicao != null) this.instituicao = instituicao;
    if (vinculo != null) this.vinculo = vinculo;
    if (seccao != null) this.seccao = seccao;
    if (categoria != null) this.categoria = categoria;
    if (escalao != null) this.escalao = escalao;
    if (carrPcc != null) this.carrPcc = carrPcc;
    if (salario != null) this.salario = salario;
    if (moeda != null) this.moeda = moeda;
    if (regime != null) this.regime = regime;
    if (tipoSituacao != null) this.tipoSituacao = tipoSituacao;
    if (tiprelAnterior != null) this.tiprelAnterior = tiprelAnterior;
    if (flgProcessa != null) this.flgProcessa = flgProcessa;
    if (obs != null) this.obs = obs;
    if (dataInicio != null) this.dataInicio = dataInicio;
    if (dataFim != null) this.dataFim = dataFim;
    if (dataInicioContrato != null) this.dataInicioContrato = dataInicioContrato;
    if (dataFimContrato != null) this.dataFimContrato = dataFimContrato;
    if (contrato != null) this.contrato = contrato;
    if (carreira != null) this.carreira = carreira;
    if (mobilidade != null) this.mobilidade = mobilidade;
    if (locTrab != null) this.locTrab = locTrab;
    if (regimeTrabalho != null) this.regimeTrabalho = regimeTrabalho;
    if (tipoContrato != null) this.tipoContrato = tipoContrato;
    if (referente != null) this.referente = referente;
    if (ultProc != null) this.ultProc = ultProc;
    if (motivoSitLab != null) this.motivoSitLab = motivoSitLab;
    if (situacLaboral != null) this.situacLaboral = situacLaboral;
    if (tpContrato != null) this.tpContrato = tpContrato;
  }

  // Soft delete
  public void eliminar() {
    this.estado = Estado.E;
  }
}
