package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.RenumeracaoListDTO;
import cv.inps.rh.funcionario.application.dto.SubsidioReqDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.mappers.TipoMovimentoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoMovimentoEntity;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
public class DefinicaoRemuneracaoMapper {

  private final ContratoMapper contratoMapper;
  private final TipoMovimentoMapper tipoMovimentoMapper;
  private final EntityManager entityManager;


  public RenumeracaoListDTO toDTO(DefinicaoRemuneracaoEntity domain) {
    if (domain == null) return null;

    RenumeracaoListDTO dto = new RenumeracaoListDTO();
    dto.setId(domain.getId());
    dto.setUuid(domain.getUuid().toString());
    dto.setEstado(domain.getEstado() != null ? domain.getEstado().name() : null);
    dto.setEstadoDesc(domain.getEstado() != null ? domain.getEstado().getDescription() : null);
    dto.setMovimento(domain.getTmId() != null ? domain.getTmId().getDescricao() : null);
    dto.setValor(domain.getValor() != null ? domain.getValor().toPlainString() : null);
    dto.setUltimoPRoc(domain.getDataInicio() != null ? domain.getDataInicio().toString() : null);

    return dto;
  }

  public java.util.List<DefinicaoRemuneracaoEntity> syncRemuneracoes(List<DefinicaoRemuneracaoEntity> existingList,
                                                                      List<SubsidioReqDTO> newList) {
    if (newList == null) return existingList;
    for (SubsidioReqDTO dto : newList) {
      DefinicaoRemuneracaoEntity found = null;
      if (dto.getId() != null) {
        for (DefinicaoRemuneracaoEntity e : existingList) {
          if (java.util.Objects.equals(e.getId(), dto.getId())) { found = e; break; }
        }
      }
      if (found != null) {
        if (dto.getTipoSubsidioId() != null) {
          found.setTmId(entityManager.getReference(TipoMovimentoEntity.class, dto.getTipoSubsidioId()));
        }
        found.setPercentagem(dto.getPercentagem());
        found.setValor(dto.getValor());
        found.setObs(dto.getObservacoes());
      } else {
        DefinicaoRemuneracaoEntity novo = new DefinicaoRemuneracaoEntity();
        if (dto.getTipoSubsidioId() != null) {
          novo.setTmId(entityManager.getReference(TipoMovimentoEntity.class, dto.getTipoSubsidioId()));
        }
        novo.setPercentagem(dto.getPercentagem());
        novo.setValor(dto.getValor());
        novo.setObs(dto.getObservacoes());
        novo.setEstado(Estado.P);
        existingList.add(novo);
      }
    }
    for (DefinicaoRemuneracaoEntity existing : existingList) {
      boolean stillExists = newList.stream()
          .anyMatch(dto -> java.util.Objects.equals(dto.getId(), existing.getId()));
      if (!stillExists) {
        existing.setEstado(Estado.E);
      }
    }
    return existingList;
  }


  public DefinicaoRemuneracaoEntity toDefinicaoRemuneracao(SubsidioReqDTO s, FuncionarioEntity fun, Estado estado) {
    if (s == null) return null;
    var de = new DefinicaoRemuneracaoEntity();
    de.setPercentagem(s.getPercentagem());
    de.setValor(s.getValor());
    de.setEstado(estado);
    de.setObs(s.getObservacoes());
    de.setDataInicio(LocalDate.now());
    de.setDataFim(LocalDate.now());
    if (s.getTipoSubsidioId() != null) {
      de.setTmId(entityManager.getReference(TipoMovimentoEntity.class, s.getTipoSubsidioId()));
    }
    de.setFunId(fun);
    de.setUuid(UuidCreator.getTimeOrderedEpoch());
    return de;
  }

  public DefinicaoRemuneracaoEntity createRenumeracao(BigDecimal valor, TipoMovimentoEntity tmId,
                                                      LocalDate dataInicio, LocalDate dataFim, FuncionarioEntity fun) {
    var de = new DefinicaoRemuneracaoEntity();
    de.setPercentagem(null);
    de.setValor(valor);
    de.setEstado(Estado.P);
    de.setObs(null);
    de.setDataInicio(dataInicio);
    de.setDataFim(dataFim);
    de.setTmId(tmId);
    de.setFunId(fun);
    de.setUuid(UuidCreator.getTimeOrderedEpoch());
    return de;
  }


}
