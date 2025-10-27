package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.parametrizacao.domain.models.ParamCargo;
import cv.inps.rh.parametrizacao.domain.models.ParamCarreira;
import cv.inps.rh.parametrizacao.domain.models.ParamCategoria;
import cv.inps.rh.parametrizacao.domain.models.ParamEscalao;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class Carreira {

  private Long id;
  private IdentificadorUnico uuid;
  private BigDecimal salario;
  private Integer flgProcessa;
  private String tipoSituacao;
  private Estado estado;
  private String obs;

  private Contrato contrato;
  private Funcionario funcionario;
  private ParamCargo cargo;
  private ParamEscalao escalao;
  private ParamCategoria categoria;
  private ParamCarreira carrPccs;

  private Carreira(Long id,
                   IdentificadorUnico uuid,
                   BigDecimal salario,
                   Integer flgProcessa,
                   String tipoSituacao,
                   Estado estado,
                   String obs,
                   Contrato contrato,
                   Funcionario funcionario,
                   ParamCargo cargo,
                   ParamEscalao escalao,
                   ParamCategoria categoria,
                   ParamCarreira carrPccs) {
    this.id = id;
    this.uuid = uuid;
    this.salario = salario;
    this.flgProcessa = flgProcessa;
    this.tipoSituacao = tipoSituacao;
    this.estado = estado;
    this.obs = obs;
    this.contrato = contrato;
    this.funcionario = funcionario;
    this.cargo = cargo;
    this.escalao = escalao;
    this.categoria = categoria;
    this.carrPccs = carrPccs;
  }

  // Criação de novo domínio
  public static Carreira create(BigDecimal salario,
                                Integer flgProcessa,
                                String tipoSituacao,
                                Estado estado,
                                String obs,
                                Contrato contrato,
                                Funcionario funcionario,
                                ParamCargo cargo,
                                ParamEscalao escalao,
                                ParamCategoria categoria,
                                ParamCarreira carrPccs) {
    return new Carreira(null, IdentificadorUnico.create(), salario, flgProcessa, tipoSituacao,
        estado, obs, contrato, funcionario, cargo, escalao, categoria, carrPccs);
  }

  // Reconstrução a partir da entity
  public static Carreira rebuild(Long id,
                                 UUID uuid,
                                 BigDecimal salario,
                                 Integer flgProcessa,
                                 String tipoSituacao,
                                 Estado estado,
                                 String obs,
                                 Contrato contrato,
                                 Funcionario funcionario,
                                 ParamCargo cargo,
                                 ParamEscalao escalao,
                                 ParamCategoria categoria,
                                 ParamCarreira carrPccs) {
    return new Carreira(id, IdentificadorUnico.from(uuid), salario, flgProcessa, tipoSituacao,
        estado, obs, contrato, funcionario, cargo, escalao, categoria, carrPccs);
  }

  // Atualização parcial
  public void update(BigDecimal salario,
                     Integer flgProcessa,
                     String tipoSituacao,
                     Estado estado,
                     String obs,
                     Contrato contrato,
                     Funcionario funcionario,
                     ParamCargo cargo,
                     ParamEscalao escalao,
                     ParamCategoria categoria,
                     ParamCarreira carrPccs) {
    if (salario != null) this.salario = salario;
    if (flgProcessa != null) this.flgProcessa = flgProcessa;
    if (tipoSituacao != null) this.tipoSituacao = tipoSituacao;
    if (estado != null) this.estado = estado;
    if (obs != null) this.obs = obs;
    if (contrato != null) this.contrato = contrato;
    if (funcionario != null) this.funcionario = funcionario;
    if (cargo != null) this.cargo = cargo;
    if (escalao != null) this.escalao = escalao;
    if (categoria != null) this.categoria = categoria;
    if (carrPccs != null) this.carrPccs = carrPccs;
  }
}
