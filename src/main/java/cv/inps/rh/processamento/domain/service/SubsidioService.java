package cv.inps.rh.processamento.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.services.model.WrapperListDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.processamento.application.dto.SubsidioFeriaDetalheFullDTO;
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
import java.time.Year;
import java.time.ZoneId;
import java.util.UUID;

@AllArgsConstructor
@Service
public class SubsidioService {

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
      case INATIVAR -> subsidioEntityRepository.updateEstadoById(subsidioId, status.getCode());
    }
  }

  private enum TipoSubsidio {
    FERIA, NATAL
  }

  @Transactional(readOnly = true)
  public WrapperListDTO getDataSubsidioNatal(Long anoProcessamento, Long direcaoId, Long funId, Integer page, Integer size) {

    var data = vsubsidioNatalEntityRepository.findSubsidioNatalByAno(
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
    subsidioFeria.setFlgAtivoInactivo(Estado.A.name().equals(subsidioFeria.getFlgAtivoInactivo()) ? Estado.I.name() : Estado.A.name());
    subsidioEntityRepository.save(subsidioFeria);
  }

  public void validarSubsidioFerias(Long subsidioId) {
    var subsidioFeria = subsidioEntityRepository.findById(subsidioId).orElseThrow();
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
