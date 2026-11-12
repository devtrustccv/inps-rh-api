package cv.inps.rh.missaoservico.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.missaoservico.application.dto.ParamAjudaCustoDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoColaboradorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamAjudaCustoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamAjudaCustoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamCarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Parametrização da ajuda de custo (RH_T_PARAM_AJUDA_CUSTO) e regra do valor diário na missão.
 *
 * <p>Regra: o valor diário base de TODOS os colaboradores da missão é o do colaborador com o cargo
 * superior — o da carreira com o valor parametrizado mais alto. Missão nacional usa o Valor AC
 * Nacional; estrangeira o Valor AC Estrangeiro. Só contam parametrizações activas em vigor na data
 * de início da missão.</p>
 */
@Service
@RequiredArgsConstructor
public class ParamAjudaCustoService {

  private static final Integer DESTINO_NACIONAL = 1;
  private static final String ESTADO_ATIVO = "A";

  private final ParamAjudaCustoEntityRepository paramAjudaCustoRepository;
  private final ParamCarreiraEntityRepository paramCarreiraRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoRepository;

  // ---------------------------------------------------------------------------------------------
  // Configuração (ecrã Outras Configurações → Missão)
  // ---------------------------------------------------------------------------------------------

  @Transactional(readOnly = true)
  public ResponseEntity<List<ParamAjudaCustoDTO>> listar() {
    var out = paramAjudaCustoRepository.findAllParaConfiguracao().stream().map(this::toDto).toList();
    return ResponseEntity.ok(out);
  }

  @Transactional
  public ResponseEntity<String> guardar(List<ParamAjudaCustoDTO> linhas) {
    var entities = new ArrayList<ParamAjudaCustoEntity>();
    for (var row : linhas == null ? List.<ParamAjudaCustoDTO>of() : linhas) {
      if (row == null || row.getCarreiraId() == null)
        continue;
      if (row.getValorNacional() != null && row.getValorNacional().signum() < 0
          || row.getValorEstrangeiro() != null && row.getValorEstrangeiro().signum() < 0)
        throw IgrpResponseStatusException.badRequest("Os valores da ajuda de custo não podem ser negativos.");

      var inicio = row.getDataInicio() != null ? row.getDataInicio() : LocalDate.now();
      if (row.getDataFim() != null && row.getDataFim().isBefore(inicio))
        throw IgrpResponseStatusException.badRequest("A Data Fim não pode ser anterior à Data Início.");

      final ParamAjudaCustoEntity entity;
      if (StringUtils.hasText(row.getId())) {
        entity = paramAjudaCustoRepository.findByUuidOrThrow(row.getId());
      } else {
        entity = new ParamAjudaCustoEntity();
        entity.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
      }
      entity.setCarrPccs(paramCarreiraRepository.findByIdOrThrow(row.getCarreiraId()));
      entity.setValorNacional(row.getValorNacional());
      entity.setValorEstrangeiro(row.getValorEstrangeiro());
      entity.setDataInicio(inicio);
      entity.setDataFim(row.getDataFim());
      entity.setEstado(StringUtils.hasText(row.getEstado()) ? row.getEstado() : ESTADO_ATIVO);
      entities.add(entity);
    }
    validarSemSobreposicao(entities);
    paramAjudaCustoRepository.saveAll(entities);
    return ResponseEntity.ok().build();
  }

  /** Uma carreira não pode ter duas parametrizações activas com períodos sobrepostos. */
  private void validarSemSobreposicao(List<ParamAjudaCustoEntity> alteradas) {
    var todas = new HashMap<String, ParamAjudaCustoEntity>();
    paramAjudaCustoRepository.findAllParaConfiguracao().forEach(p -> todas.put(p.getUuid(), p));
    alteradas.forEach(p -> todas.put(p.getUuid(), p));
    var ativas = todas.values().stream().filter(p -> ESTADO_ATIVO.equals(p.getEstado())).toList();
    for (int i = 0; i < ativas.size(); i++) {
      for (int j = i + 1; j < ativas.size(); j++) {
        var a = ativas.get(i);
        var b = ativas.get(j);
        if (Objects.equals(a.getCarrPccs().getId(), b.getCarrPccs().getId()) && sobrepoe(a, b)) {
          throw IgrpResponseStatusException.badRequest("A carreira '" + a.getCarrPccs().getNome()
              + "' tem mais do que uma configuração activa no mesmo período.");
        }
      }
    }
  }

  private static boolean sobrepoe(ParamAjudaCustoEntity a, ParamAjudaCustoEntity b) {
    var fimA = a.getDataFim() != null ? a.getDataFim() : LocalDate.MAX;
    var fimB = b.getDataFim() != null ? b.getDataFim() : LocalDate.MAX;
    return !a.getDataInicio().isAfter(fimB) && !b.getDataInicio().isAfter(fimA);
  }

  private ParamAjudaCustoDTO toDto(ParamAjudaCustoEntity e) {
    var dto = new ParamAjudaCustoDTO();
    dto.setId(e.getUuid());
    dto.setCarreiraId(e.getCarrPccs() != null ? e.getCarrPccs().getId() : null);
    dto.setCarreiraDesc(e.getCarrPccs() != null ? e.getCarrPccs().getNome() : null);
    dto.setValorNacional(e.getValorNacional());
    dto.setValorEstrangeiro(e.getValorEstrangeiro());
    dto.setDataInicio(e.getDataInicio());
    dto.setDataFim(e.getDataFim());
    dto.setEstado(e.getEstado());
    return dto;
  }

  // ---------------------------------------------------------------------------------------------
  // Regra na missão
  // ---------------------------------------------------------------------------------------------

  /**
   * Valor diário base da ajuda de custo da missão: o da carreira com o valor mais alto entre os
   * colaboradores activos (cargo superior), nacional ou estrangeiro conforme o destino. Devolve
   * {@code null} se nenhum colaborador tiver carreira parametrizada.
   */
  @Transactional(readOnly = true)
  /**
   * D2 (22/10/2026): a ajuda de custo é da função de CADA colaborador (spec: "o valor varia consoante
   * a função que o colaborador desempenha") — já não se aplica a todos o do cargo superior.
   * {@code null} quando a carreira dele não está parametrizada.
   */
  public BigDecimal valorDiarioBaseDoColaborador(MissaoServicoEntity missao, MissaoColaboradorEntity colaborador) {
    return colaborador == null ? null : valorDiarioBase(missao, List.of(colaborador));
  }

  /** O valor mais alto entre as carreiras dos colaboradores (com um só colaborador, o dele). */
  public BigDecimal valorDiarioBase(MissaoServicoEntity missao, List<MissaoColaboradorEntity> colaboradores) {
    if (missao == null || colaboradores == null || colaboradores.isEmpty())
      return null;
    var carreiras = new ArrayList<Long>();
    for (var c : colaboradores) {
      var carreira = carreiraDoColaborador(c);
      if (carreira != null) carreiras.add(carreira);
    }
    if (carreiras.isEmpty())
      return null;

    var data = missao.getDataInicio() != null ? missao.getDataInicio() : LocalDate.now();
    var nacional = DESTINO_NACIONAL.equals(missao.getFlgDestino());
    return paramAjudaCustoRepository.findEmVigor(carreiras, data).stream()
        .map(p -> nacional ? p.getValorNacional() : p.getValorEstrangeiro())
        .filter(Objects::nonNull)
        .max(Comparator.naturalOrder())
        .orElse(null);
  }

  /**
   * Carreira (RH_T_PARAM_CARREIRA) do vínculo actual do colaborador: a da carreira PCCS ou, nos
   * vínculos sem carreira (ex.: Comissão de Serviço), a do escalão gravado no tiprel.
   */
  private Long carreiraDoColaborador(MissaoColaboradorEntity c) {
    var fun = c != null ? c.getFunId() : null;
    if (fun == null || fun.getUuid() == null)
      return null;
    TiposRelacionamentoEntity tr = tiposRelacionamentoRepository.findAtualByFuncionarioUuid(fun.getUuid()).orElse(null);
    if (tr == null)
      return null;
    if (tr.getCarreiraId() != null && tr.getCarreiraId().getCarrPccsId() != null)
      return tr.getCarreiraId().getCarrPccsId().getId();
    var escalao = tr.getCarreiraId() != null ? tr.getCarreiraId().getEscalaoId() : tr.getEscalaoId();
    return escalao != null && escalao.getParamCarrId() != null ? escalao.getParamCarrId().getId() : null;
  }

}
