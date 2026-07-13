package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.RenumeracaoListDTO;
import cv.inps.rh.funcionario.application.dto.SubsidioReqDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.mappers.TipoMovimentoMapper;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoMovimentoEntity;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DefinicaoRemuneracaoMapper {

  private final ContratoMapper contratoMapper;
  private final TipoMovimentoMapper tipoMovimentoMapper;
  private final EntityManager entityManager;


  public RenumeracaoListDTO toDTO(DefinicaoRemuneracaoEntity entity) {
    if (entity == null) return null;

    RenumeracaoListDTO dto = new RenumeracaoListDTO();
    dto.setId(entity.getId());
    dto.setUuid(entity.getUuid().toString());
    dto.setEstado(entity.getEstado() != null ? entity.getEstado().name() : null);
    dto.setEstadoDesc(entity.getEstado() != null ? entity.getEstado().getDescription() : null);
    dto.setMovimento(entity.getTmId() != null ? entity.getTmId().getDescricao() : null);
    dto.setValor((entity.getValor() != null ? entity.getValor() : BigDecimal.ZERO).toPlainString());
    dto.setPercentagem((entity.getPercentagem() != null ? entity.getPercentagem() : BigDecimal.ZERO).toPlainString());
    dto.setObs(entity.getObs());
    dto.setUltimoPRoc(entity.getDataUltimoProc() != null ? DateFormatter.localDateToString(entity.getDataUltimoProc()) : null);
    dto.setDataInicio(entity.getDataInicio() != null ? DateFormatter.localDateToString(entity.getDataInicio()) : null);
    dto.setDataFim(entity.getDataFim() != null ? DateFormatter.localDateToString(entity.getDataFim()) : null);

    return dto;
  }

  public java.util.List<DefinicaoRemuneracaoEntity> syncRemuneracoes(List<DefinicaoRemuneracaoEntity> existingList,
                                                                      List<SubsidioReqDTO> newList,
                                                                      FuncionarioEntity fun) {
    if (CollectionUtils.isEmpty(newList)) return existingList;
    for (SubsidioReqDTO dto : newList) {
      DefinicaoRemuneracaoEntity found = null;
      if (dto.getId() != null) {
        for (DefinicaoRemuneracaoEntity e : existingList) {
          if (java.util.Objects.equals(e.getId(), dto.getId())) { found = e; break; }
        }
      }
      if (found != null) {
        ValidationUtil.validateValorNaoNegativo(dto.getValor());
        ValidationUtil.validatePercentagem(dto.getPercentagem());
        found.setTmId(ValidationUtil.ref(entityManager, TipoMovimentoEntity.class, dto.getTipoSubsidioId()));
        found.setPercentagem(dto.getPercentagem() != null ? dto.getPercentagem() : BigDecimal.ZERO);
        found.setValor(dto.getValor() != null ? dto.getValor() : BigDecimal.ZERO);
        found.setObs(dto.getObservacoes());
      } else {
        // Novo subsídio: usar toDefinicaoRemuneracao para garantir funId + uuid + datas (evita
        // ORA-01400 por FUN_ID null na inserção).
        existingList.add(toDefinicaoRemuneracao(dto, fun, Estado.P));
      }
    }
    for (DefinicaoRemuneracaoEntity existing : existingList) {
      boolean stillExists = newList.stream()
          .anyMatch(dto -> java.util.Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists && existing.getEstado() != Estado.E && existing.getEstado() != Estado.I) {
        existing.setEstado(Estado.E);
      }
    }
    verificarTipoMovimentoDuplicado(existingList);
    return existingList;
  }

  private void verificarTipoMovimentoDuplicado(List<DefinicaoRemuneracaoEntity> list) {
    var seen = new HashSet<Long>();
    for (var e : list) {
      if (e.getEstado() != Estado.A && e.getEstado() != Estado.P) continue;
      if (e.getTmId() != null && !seen.add(e.getTmId().getId())) {
        var estadoDesc = e.getEstado() == Estado.P ? "pendente de validação" : "activo";
        throw IgrpResponseStatusException.conflict(
            "O tipo de movimento '" + e.getTmId().getDescricao() + "' já se encontra " + estadoDesc + " nas remunerações.");
      }
    }
  }


  public DefinicaoRemuneracaoEntity toDefinicaoRemuneracao(SubsidioReqDTO s, FuncionarioEntity fun, Estado estado) {
    if (s == null) return null;
    ValidationUtil.validateValorNaoNegativo(s.getValor());
    ValidationUtil.validatePercentagem(s.getPercentagem());
    var de = new DefinicaoRemuneracaoEntity();
    de.setPercentagem(s.getPercentagem() != null ? s.getPercentagem() : BigDecimal.ZERO);
    de.setValor(s.getValor() != null ? s.getValor() : BigDecimal.ZERO);
    de.setEstado(estado);
    de.setObs(s.getObservacoes());
    de.setDataInicio(LocalDate.now());
    de.setDataFim(LocalDate.now());
    de.setTmId(ValidationUtil.ref(entityManager, TipoMovimentoEntity.class, s.getTipoSubsidioId()));
    de.setFunId(fun);
    de.setUuid(UuidCreator.getTimeOrderedEpoch());
    return de;
  }

  public DefinicaoRemuneracaoEntity createRenumeracao(BigDecimal valor, TipoMovimentoEntity tmId,
                                                      LocalDate dataInicio, LocalDate dataFim, FuncionarioEntity fun,String moeda) {
    var de = new DefinicaoRemuneracaoEntity();
    de.setPercentagem(BigDecimal.ZERO);
    de.setValor(valor != null ? valor : BigDecimal.ZERO);
    de.setEstado(Estado.P);
    de.setObs(null);
    de.setDataInicio(dataInicio);
    de.setDataFim(dataFim);
    de.setTmId(tmId);
    de.setFunId(fun);
    de.setMoeda(ValidationUtil.trimToNull(moeda));
    de.setUuid(UuidCreator.getTimeOrderedEpoch());
    return de;
  }


}
