package cv.inps.rh.funcionario.domain.models;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import lombok.Getter;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Getter
public class RegimeTrabalho {

  private final Long id;
  private final IdentificadorUnico uuid;
  private String tipoRegime;
  private String tipoSituacao;
  private LocalDate dataFim;
  private String obs;
  private Estado estado;
  private List<RegimeModalidade> regimeModalidades;

  private Long idFuncionario;
  private UUID uuidFuncionario;

  private Contrato contrato;
  private TiposRelacionamento tiprel;



  // Construtor privado
  private RegimeTrabalho(Long id,
                         IdentificadorUnico uuid,
                         String tipoRegime,
                         String tipoSituacao,
                         LocalDate dataFim,
                         String obs,
                         Estado estado,
                         Contrato contrato,
                         List<RegimeModalidade> regimeModalidades ,
                         Long idFuncionario,
                         UUID uuidFuncionario) {
    this.id = id;
    this.uuid = uuid;
    this.tipoRegime = tipoRegime;
    this.tipoSituacao = tipoSituacao;
    this.dataFim = dataFim;
    this.obs = obs;
    this.estado = estado;
    this.contrato = contrato;
    this.regimeModalidades = regimeModalidades!=null ? regimeModalidades : new ArrayList<>();

    this.idFuncionario = idFuncionario;
    this.uuidFuncionario = uuidFuncionario;
  }

  // Factory para criar novo regime
  public static RegimeTrabalho create(String tipoRegime,
                                      String tipoSituacao,
                                      LocalDate dataFim,
                                      String obs,
                                      Contrato contrato) {
    return new RegimeTrabalho(
        null,
        IdentificadorUnico.create(),
        tipoRegime,
        tipoSituacao,
        dataFim,
        obs,
        Estado.P,
        contrato,
        null,
        null,
        null
    );
  }

  // Rebuild a partir da Entity
  public static RegimeTrabalho rebuild(Long id,
                                       UUID uuid,
                                       String tipoRegime,
                                       String tipoSituacao,
                                       LocalDate dataFim,
                                       String obs,
                                       Estado estado,
                                       Contrato contrato,
                                       List<RegimeModalidade> regimeModalidades,
                                       Long idFuncionario,
                                       UUID uuidFuncionario) {
    return new RegimeTrabalho(
        id,
        IdentificadorUnico.from(uuid),
        tipoRegime,
        tipoSituacao,
        dataFim,
        obs,
        estado,
        contrato,
        regimeModalidades,
        idFuncionario,
        uuidFuncionario
    );
  }

  // Atualização parcial
  public void update(String tipoRegime,
                     String tipoSituacao,
                     LocalDate dataFim,
                     String obs,
                     Contrato contrato) {
    if (tipoRegime != null) this.tipoRegime = tipoRegime;
    if (tipoSituacao != null) this.tipoSituacao = tipoSituacao;
    if (dataFim != null) this.dataFim = dataFim;
    if (obs != null) this.obs = obs;
    if (contrato != null) this.contrato = contrato;
  }

  // Soft delete
  public void eliminar() {
    this.estado = Estado.E;
  }

  public void mudarEstado(Estado estado) {
    this.estado = estado;
  }

  public void syncModalidades(List<RegimeModalidade> novasModalidades) {
    if (novasModalidades == null) return;

    for (RegimeModalidade nova : novasModalidades) {
      addOrUpdateModalidade(nova);
    }

    for (RegimeModalidade existente : regimeModalidades) {
      boolean aindaExiste = novasModalidades.stream()
          .anyMatch(m -> Objects.equals(m.getId(), existente.getId()));
      if (!aindaExiste) {
        existente.eliminar();
      }
    }
  }

  private void addOrUpdateModalidade(RegimeModalidade nova) {
    if (nova == null) return;

    Optional<RegimeModalidade> existenteOpt = findModalidadeById(nova.getId());
    if (existenteOpt.isPresent()) {
      RegimeModalidade existente = existenteOpt.get();
      existente.update(nova.getModalidade(), nova.getDiasSemana(), nova.getNumHoras());
    } else {
      this.regimeModalidades.add(nova);
    }
  }


  private Optional<RegimeModalidade> findModalidadeById(Long id) {
    if (id == null) return Optional.empty();
    return this.regimeModalidades.stream()
        .filter(m -> Objects.equals(m.getId(), id))
        .findFirst();
  }

  public String getDiasSemanaAgrupados() {
    if (regimeModalidades == null || regimeModalidades.isEmpty()) return null;
    return regimeModalidades.stream()
        .map(RegimeModalidade::getDiasSemana)
        .filter(d -> d != null && !d.isBlank())
        .distinct()
        .collect(Collectors.joining(", "));
  }

  /** Soma o total de horas das modalidades */
  public Integer getTotalHoras() {
    if (regimeModalidades == null || regimeModalidades.isEmpty()) return 0;
    return regimeModalidades.stream()
        .filter(r -> r.getNumHoras() != null)
        .mapToInt(RegimeModalidade::getNumHoras)
        .sum();
  }

    public void fechar(LocalDate dataFimContrato) {
      this.dataFim = dataFimContrato;
    }
}
