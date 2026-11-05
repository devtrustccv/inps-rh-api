package cv.inps.rh.processamento.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.services.model.WrapperListDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.processamento.application.dto.SubsidioFeriaDetalheFullDTO;
import cv.inps.rh.processamento.application.dto.SubsidioNatalEditarRequestDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.springframework.util.StringUtils;
import cv.inps.rh.processamento.application.dto.SubsidioResponseNatalDTO;
import cv.inps.rh.processamento.domain.models.SubsidioNatalStatus;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.ApplicationAuditorAware;
import cv.inps.rh.shared.infrastructure.persistence.entity.SubsidioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.NumberUtils;
import cv.inps.rh.shared.util.PageMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.Year;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@AllArgsConstructor
@Service
public class SubsidioService {

  /** FLG_ATIVO_INACTIVO de um subsídio já processado: só pode ser consultado. */
  private static final String FLG_PROCESSADO = "PR";

  private final SubsidioEntityRepository subsidioEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final VSubsidioFeriaEntityRepository vsubsidioFeriaEntityRepository;
  private final VSubsidioNatalEntityRepository vsubsidioNatalEntityRepository;
  private final VSubsidioFeriasDetailEntityRepository subsidioFeriasDetailEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final ApplicationAuditorAware applicationAuditorAware;

  public void activateInactivateSubsidioNatal(Long subsidioId, Long ano, String funId, SubsidioNatalStatus status, SubsidioResponseNatalDTO data) {
    switch (status) {
      case ATIVAR -> {

        final SubsidioEntity subs;

        if (subsidioId != null) {
          subs = subsidioEntityRepository.findByIdOrThrow(subsidioId);
          garantirNaoProcessado(subs);
        } else {
          var fun = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funId));
          subs = new SubsidioEntity();
          subs.setTipoSubsidio(TipoSubsidio.NATAL.name());
          subs.setFun(fun);
          subs.setEstado(Estado.P.name());
          subs.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          subs.setReferenciaId(fun.getId());
        }
        subs.setAnoReferente(ano);
        subs.setValorSalarioBase(NumberUtils.safeValueOf(data.salario()));
        subs.setMesTrab(NumberUtils.safeValueOf(data.mesesTrabalho()));
        subs.setPercSalario(NumberUtils.safeValueOf(data.percSalario()));
        subs.setFaltas(NumberUtils.safeValueOf(data.faltas()));
        subs.setPercFalta(NumberUtils.safeValueOf(data.percFalta()));
        subs.setValorSubsidio(NumberUtils.safeValueOf(data.valorSubsidio()));
        subs.setValorChequeBrinde(NumberUtils.safeValueOf(data.valorChequeBrinde()));
        subs.setValorPrendaNatal(NumberUtils.safeValueOf(data.valorPrendaNatal()));

        var saved = subsidioEntityRepository.save(subs);

        if (subsidioId == null) {
          var fun = subs.getFun();
          var validation = new ValidacaoEntity();
          validation.setTipoAccao("SUBSIDIO_NATAL");
          validation.setReferenciaName("RH_T_SUBSIDIO_NATAL");
          validation.setReferenciaId(saved.getId());
          validation.setFunId(fun);
          validation.setTiprelId(funcionarioRules.getTipoRelacionamentoAtual(fun.getUuid()));
          validation.setEstado(Estado.A);
          validation.setUuid(UuidCreator.getTimeOrderedEpoch());
          validacaoEntityRepository.save(validation);
        }
      }
      // Inativar é FLG_ATIVO_INACTIVO (o ESTADO é o de validação, P/A): com ESTADO = I o subsídio
      // desaparecia da lista. Mantido por compatibilidade; o ecrã usa subsidio-natal/change-status.
      case INATIVAR -> {
        var subs = subsidioEntityRepository.findByIdOrThrow(subsidioId);
        garantirNaoProcessado(subs);
        subs.setFlgAtivoInactivo(Estado.I.name());
        marcarAlteracao(subs);
        subsidioEntityRepository.save(subs);
      }
    }
  }

  private enum TipoSubsidio {
    FERIA, NATAL
  }

  @Transactional(readOnly = true)
  public WrapperListDTO getDataSubsidioNatal(Long anoProcessamento, Long direcaoId, Long funId, Integer page, Integer size) {

    var data = subsidioEntityRepository.findSubsidioNatal(
        anoProcessamento != null ? anoProcessamento : Year.now(ZoneId.systemDefault()).getValue(),
        direcaoId,
        funId,
        PageRequest.of(page, size)
    );

    var response = new WrapperListDTO();
    PageMapper.fillPagination(data, response);
    response.setContent(data.getContent());
    return response;
  }

  /** Ativar/Inativar em lote: troca FLG_ATIVO_INACTIVO (A ↔ I) de cada subsídio selecionado. */
  @Transactional
  public int alterarEstadoSubsidioNatal(List<Long> subsidioIds) {
    var subsidios = subsidiosNatalEmVigor(subsidioIds);
    subsidios.forEach(s -> {
      s.setFlgAtivoInactivo(Estado.A.name().equals(s.getFlgAtivoInactivo()) ? Estado.I.name() : Estado.A.name());
      marcarAlteracao(s);
    });
    subsidioEntityRepository.saveAll(subsidios);
    return subsidios.size();
  }

  /** Validar em lote: ESTADO P → A. Os já validados ficam como estão. */
  @Transactional
  public int validarSubsidioNatal(List<Long> subsidioIds) {
    var pendentes = subsidiosNatalEmVigor(subsidioIds).stream()
        .filter(s -> Estado.P.name().equals(s.getEstado()))
        .toList();
    if (pendentes.isEmpty())
      throw IgrpResponseStatusException.badRequest("Os subsídios selecionados já estão validados.");
    pendentes.forEach(s -> {
      s.setEstado(Estado.A.name());
      marcarAlteracao(s);
    });
    subsidioEntityRepository.saveAll(pendentes);
    return pendentes.size();
  }

  /**
   * Editar o Valor Subsídio e/ou a Prenda de Natal (spec 3.2.11.1): o registo em vigor passa a E e
   * cria-se um novo com os valores alterados, a Observação (motivo, obrigatório) e ESTADO P — volta
   * a precisar de validação.
   */
  @Transactional
  public Long editarSubsidioNatal(Long subsidioId, SubsidioNatalEditarRequestDTO dto) {
    var anterior = subsidiosNatalEmVigor(List.of(subsidioId)).getFirst();

    var novoValor = dto.getValorSubsidio() != null ? dto.getValorSubsidio() : anterior.getValorSubsidio();
    var novaPrenda = dto.getValorPrendaNatal() != null ? dto.getValorPrendaNatal() : anterior.getValorPrendaNatal();
    if (novoValor < 0 || (novaPrenda != null && novaPrenda < 0))
      throw IgrpResponseStatusException.badRequest("Os valores do subsídio não podem ser negativos.");
    if (Objects.equals(novoValor, anterior.getValorSubsidio())
        && Objects.equals(novaPrenda, anterior.getValorPrendaNatal()))
      throw IgrpResponseStatusException.badRequest(
          "Não alterou o Valor Subsídio nem a Prenda de Natal: não há nada a gravar.");
    if (!StringUtils.hasText(dto.getObservacao()))
      throw IgrpResponseStatusException.badRequest("Indique o motivo da alteração.");
    var motivo = dto.getObservacao().trim();
    if (motivo.length() > 500)
      throw IgrpResponseStatusException.badRequest("O motivo da alteração não pode ter mais de 500 caracteres.");

    anterior.setEstado(Estado.E.name());
    marcarAlteracao(anterior);
    subsidioEntityRepository.saveAndFlush(anterior);

    var novo = new SubsidioEntity();
    novo.setTipoSubsidio(anterior.getTipoSubsidio());
    novo.setFun(anterior.getFun());
    novo.setAnoReferente(anterior.getAnoReferente());
    novo.setEscalaoId(anterior.getEscalaoId());
    novo.setMesTrab(anterior.getMesTrab());
    novo.setDiasTrab(anterior.getDiasTrab());
    novo.setDataInicio(anterior.getDataInicio());
    novo.setDataFim(anterior.getDataFim());
    novo.setValorMes(anterior.getValorMes());
    novo.setValorDia(anterior.getValorDia());
    novo.setSituacao(anterior.getSituacao());
    novo.setFlgAtivoInactivo(anterior.getFlgAtivoInactivo());
    novo.setValorSalarioBase(anterior.getValorSalarioBase());
    novo.setPercSalario(anterior.getPercSalario());
    novo.setFaltas(anterior.getFaltas());
    novo.setPercFalta(anterior.getPercFalta());
    novo.setValorChequeBrinde(anterior.getValorChequeBrinde());
    novo.setReferenciaId(anterior.getId());
    novo.setValorSubsidio(novoValor);
    novo.setValorPrendaNatal(novaPrenda);
    novo.setObservacao(motivo);
    novo.setEstado(Estado.P.name());
    novo.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
    novo.setDataRegisto(LocalDate.now(ZoneId.systemDefault()));
    // Sem id numérico do utilizador autenticado: mantém-se o do registo de origem (o cálculo usa 1).
    novo.setUserRegistoId(anterior.getUserRegistoId());
    novo.setUserRegistoName(utilizadorAtual());
    return subsidioEntityRepository.save(novo).getId();
  }

  /** Subsídios de Natal em vigor (P/A) e ainda não processados no salário; recusa os restantes. */
  private List<SubsidioEntity> subsidiosNatalEmVigor(List<Long> subsidioIds) {
    if (subsidioIds == null || subsidioIds.isEmpty())
      throw IgrpResponseStatusException.badRequest("Selecione pelo menos um subsídio.");
    var subsidios = subsidioEntityRepository.findNatalByIdIn(subsidioIds);
    if (subsidios.size() != new java.util.HashSet<>(subsidioIds).size())
      throw IgrpResponseStatusException.notFound("Subsídio de Natal não encontrado.");
    for (var s : subsidios) {
      if (Estado.E.name().equals(s.getEstado()))
        throw IgrpResponseStatusException.badRequest(
            "O subsídio de " + s.getFun().getNome() + " foi substituído por uma alteração: atualize a lista.");
      garantirNaoProcessado(s);
      if (s.getProcSalId() != null)
        throw IgrpResponseStatusException.badRequest(
            "O subsídio de " + s.getFun().getNome() + " já foi processado no salário e não pode ser alterado.");
    }
    return subsidios;
  }

  private void garantirNaoProcessado(SubsidioEntity s) {
    if (FLG_PROCESSADO.equals(s.getFlgAtivoInactivo()))
      throw IgrpResponseStatusException.badRequest("Subsídio já processado: só pode ser consultado.");
  }

  private void marcarAlteracao(SubsidioEntity s) {
    s.setDataAlteracao(LocalDate.now(ZoneId.systemDefault()));
    s.setUserAlteracaoName(utilizadorAtual());
  }

  private String utilizadorAtual() {
    var nome = applicationAuditorAware.getCurrentSubjectName();
    return StringUtils.hasText(nome) ? nome : "sistema";
  }

  @Transactional(readOnly = true)
  public WrapperListDTO getSubsidioFeriasData(Integer anoReferente, Long direcaoId, Long funId, Integer page, Integer size) {

    var data = vsubsidioFeriaEntityRepository.findSubsidioFeriasByAno(
        anoReferente != null ? anoReferente : Year.now(ZoneId.systemDefault()).getValue(),
        direcaoId,
        funId,
        PageRequest.of(page, size)
    );

    var response = new WrapperListDTO();
    PageMapper.fillPagination(data, response);
    response.setContent(data.getContent());
    return response;
  }

  public void alterarEstadoSubsisioferias(Long subsidioId) {
    var subsidioFeria = subsidioEntityRepository.findById(subsidioId).orElseThrow();
    garantirNaoProcessado(subsidioFeria);
    subsidioFeria.setFlgAtivoInactivo(Estado.A.name().equals(subsidioFeria.getFlgAtivoInactivo()) ? Estado.I.name() : Estado.A.name());
    subsidioEntityRepository.save(subsidioFeria);
  }

  public void validarSubsidioFerias(Long subsidioId) {
    var subsidioFeria = subsidioEntityRepository.findById(subsidioId).orElseThrow();
    garantirNaoProcessado(subsidioFeria);
    subsidioFeria.setEstado(Estado.A.name());
    subsidioEntityRepository.save(subsidioFeria);
  }

  public void calcularSubsidioferias(Integer ano) {
    vsubsidioFeriaEntityRepository.calcularDiasFeria(
        ano.toString(),
        1L, // TODO 06/09/2026 13:30 remove this from procedure
        applicationAuditorAware.getCurrentSubjectName()
    );
  }

  public SubsidioFeriaDetalheFullDTO getDetalhesSubsidio(Integer ano, Long funId) {

    var rows = subsidioFeriasDetailEntityRepository.getDetails(funId, ano);
    if (rows.isEmpty())
      return new SubsidioFeriaDetalheFullDTO();

    var decimalFormat = NumberUtils.spaceDecimalFormat();

    long totalRemuneracao = 0L;

    for (var row : rows) {
      totalRemuneracao = totalRemuneracao + row.getValorEscalaotempoBD();
      row.setValorEscalaotempo(decimalFormat.format(row.getValorEscalaotempoBD()));
      row.setValorMes(decimalFormat.format(row.getValorMesBD()));
      row.setValorEscalao(decimalFormat.format(row.getValorEscalaoBD()));
    }

    var salarioBaseCalculo = BigDecimal.valueOf(totalRemuneracao)
        .multiply(BigDecimal.valueOf(30))
        .divide(
            BigDecimal.valueOf(360),
            0,
            RoundingMode.HALF_UP
        )
        .longValue();

    var response = new SubsidioFeriaDetalheFullDTO();
    response.setRows(rows);
    response.setTotalRemuneracao(decimalFormat.format(totalRemuneracao));
    response.setSalarioBaseCalculo(decimalFormat.format(salarioBaseCalculo));
    response.setValorSubsidio(null);
    return response;
  }
}
