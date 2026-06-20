package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.EncargosDescontosReqDTO;
import cv.inps.rh.funcionario.application.dto.PagamentosDescontoListDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.mappers.TipoMovimentoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoMovimentoEntity;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DefPagamentoMapper {

  private final ContratoMapper contratoMapper;
  private final TiposRelacionamentoMapper tiprelMapper;
  private final TipoMovimentoMapper tipoMovimentoMapper;
  private final EntityManager entityManager;


  public PagamentosDescontoListDTO toDTO(DefPagamentoEntity defPagamentoEntity) {
    if (defPagamentoEntity == null) return null;

    var dto = new PagamentosDescontoListDTO();
    dto.setId(defPagamentoEntity.getId() != null ? defPagamentoEntity.getId().toString() : null);
    dto.setUuid(defPagamentoEntity.getUuid() != null ? defPagamentoEntity.getUuid().toString() : null);
    dto.setMovimento(defPagamentoEntity.getTmId() != null ? defPagamentoEntity.getTmId().getDescricao() : null);
    dto.setValor((defPagamentoEntity.getValor() != null ? defPagamentoEntity.getValor() : BigDecimal.ZERO).toPlainString());
    dto.setPercentagem((defPagamentoEntity.getPercentagem() != null ? defPagamentoEntity.getPercentagem() : BigDecimal.ZERO).toPlainString());
    dto.setObs(defPagamentoEntity.getObs());
    dto.setEstado(defPagamentoEntity.getEstado() != null ? defPagamentoEntity.getEstado().name() : null);
    dto.setEstadoDesc(defPagamentoEntity.getEstado() != null ? defPagamentoEntity.getEstado().getDescription() : null);
    dto.setDataInicio(defPagamentoEntity.getDataInicio() != null ? DateFormatter.localDateToString(defPagamentoEntity.getDataInicio()) : null);
    dto.setDataFim(defPagamentoEntity.getDataFim() != null ? DateFormatter.localDateToString(defPagamentoEntity.getDataFim()) : null);
    dto.setUltimoProc(defPagamentoEntity.getDataUltimoProc() != null ? DateFormatter.localDateToString(defPagamentoEntity.getDataUltimoProc()) : null);

    return dto;
  }



  public List<DefPagamentoEntity> syncPagamentos(
      java.util.List<DefPagamentoEntity> existingList,
      java.util.List<cv.inps.rh.funcionario.application.dto.EncargosDescontosReqDTO> newList) {
    if (org.springframework.util.CollectionUtils.isEmpty(newList)) return existingList;
    for (cv.inps.rh.funcionario.application.dto.EncargosDescontosReqDTO dto : newList) {
      DefPagamentoEntity found = null;
      if (dto.getId() != null) {
        for (DefPagamentoEntity e : existingList) {
          if (java.util.Objects.equals(e.getId(), dto.getId())) { found = e; break; }
        }
      }
      if (found != null) {
        ValidationUtil.validateValorNaoNegativo(dto.getValor());
        ValidationUtil.validateIntervaloData(dto.getDataInicio(), dto.getDataFim());
        found.setTmId(ValidationUtil.ref(entityManager, TipoMovimentoEntity.class, dto.getTipoEncargoId()));
        found.setValor(dto.getValor() != null ? dto.getValor() : BigDecimal.ZERO);
        found.setDataInicio(dto.getDataInicio());
        found.setDataFim(dto.getDataFim());
        found.setObs(dto.getObservacoes());
      } else {
        DefPagamentoEntity novo = new DefPagamentoEntity();
        novo.setTmId(ValidationUtil.ref(entityManager, TipoMovimentoEntity.class, dto.getTipoEncargoId()));
        novo.setValor(dto.getValor() != null ? dto.getValor() : BigDecimal.ZERO);
        novo.setDataInicio(dto.getDataInicio());
        novo.setDataFim(dto.getDataFim());
        novo.setObs(dto.getObservacoes());
        novo.setEstado(Estado.P);
        existingList.add(novo);
      }
    }
    for (DefPagamentoEntity existing : existingList) {
      boolean stillExists = newList.stream()
          .anyMatch(dto -> java.util.Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists && existing.getEstado() != Estado.E && existing.getEstado() != Estado.I) {
        existing.setEstado(Estado.E);
      }
    }
    return existingList;
  }


  public DefPagamentoEntity toDefPagamento(EncargosDescontosReqDTO e, FuncionarioEntity fun, Estado estado) {
    if (e == null) return null;
    ValidationUtil.validateValorNaoNegativo(e.getValor());
    ValidationUtil.validateIntervaloData(e.getDataInicio(), e.getDataFim());
    var dp = new DefPagamentoEntity();
    dp.setTmId(ValidationUtil.ref(entityManager, TipoMovimentoEntity.class, e.getTipoEncargoId()));
    dp.setValor(e.getValor() != null ? e.getValor() : BigDecimal.ZERO);
    dp.setDataInicio(e.getDataInicio());
    dp.setDataFim(e.getDataFim());
    dp.setObs(e.getObservacoes());
    dp.setEstado(estado);
    dp.setUuid(UuidCreator.getTimeOrderedEpoch());
    dp.setFunId(fun);
    return dp;
  }

  public DefPagamentoEntity createPagamento(BigDecimal valor, BigDecimal percentagem, TipoMovimentoEntity tmId, LocalDate dataInicio, LocalDate dataFim, FuncionarioEntity fun) {
    var dp = new DefPagamentoEntity();
    dp.setValor(valor != null ? valor : BigDecimal.ZERO);
    dp.setPercentagem(percentagem != null ? percentagem : BigDecimal.ZERO);
    dp.setDataInicio(dataInicio);
    dp.setDataFim(dataFim);
    dp.setEstado(Estado.P);
    dp.setObs(null);
    dp.setTmId(tmId);
    dp.setFunId(fun);
    dp.setUuid(UuidCreator.getTimeOrderedEpoch());
    return dp;
  }

}
