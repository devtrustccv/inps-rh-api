package cv.inps.rh.funcionario.application.service.historicolaboral;

import cv.inps.rh.funcionario.application.dto.HistoricoLaboralResponseDTO;
import cv.inps.rh.funcionario.application.dto.ValidarNovoHistoricoLaboralDTO;
import cv.inps.rh.funcionario.application.dto.WrapperHistLaboralResponseDTO;
import cv.inps.rh.funcionario.application.queries.GetHistoricoLaboralByIdQuery;
import cv.inps.rh.funcionario.application.queries.GetHistoricoLaboralQuery;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
@Transactional
public class HistoricoLaboralReadService {

  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;

  public WrapperHistLaboralResponseDTO getHistoricoLaboral(GetHistoricoLaboralQuery query) {

    var pageRequest = PageRequest.of(
        Integer.parseInt(query.getPagina()),
        Integer.parseInt(query.getTamanho())
    );

    var page = tiposRelacionamentoEntityRepository.findByFunId_UuidAndEstado(
        UUID.fromString(query.getFuncionarioId()),
        Estado.A,
        pageRequest
    );

    var data = page.stream()
        .map(obj -> {
          var response = new HistoricoLaboralResponseDTO();
          response.setUltimoMovimento(DateFormatter.localDateToString(obj.getUltProc()));

          ofNullable(obj.getTipoSituacao()).ifPresent(response::setTipoSituacao);
          ofNullable(obj.getContrVinculoId().getTpContratoId().getNome()).ifPresent(response::setTipoContrato);
          ofNullable(obj.getContrVinculoId().getVinculoId()).map(ParamVinculoEntity::getNome).ifPresent(response::setVinculo);
          ofNullable(obj.getSeccaoId()).map(SecaoEntity::getInstId).map(InstituicaoEntity::getNome).ifPresent(response::setDirecao);
          ofNullable(obj.getSeccaoId()).map(SecaoEntity::getNome).ifPresent(response::setSeccao);
          ofNullable(obj.getCarreiraId()).map(CarreiraEntity::getCarrPccsId).map(ParamCarreiraEntity::getNome).ifPresent(response::setCarreira);
          ofNullable(obj.getEscalaoId()).map(ParamEscalaoEntity::getEscalao).ifPresent(response::setReferenciaEscalao);
          ofNullable(obj.getCargoId()).map(ParamCargoEntity::getNome).ifPresent(response::setCargo);
          ofNullable(obj.getSituacLaboralId())
              .map(SituacaoLaboralEntity::getSituacaoLaboralId)
              .map(ParamSituacaoEntity::getNome)
              .ifPresent(response::setSituacaoLaboral);

          response.setId(obj.getId());
          response.setUuid(obj.getFunId().getUuid().toString());

          var dataInicioContrato = obj.getContrVinculoId().getDataInicio()!=null ? DateFormatter.localDateToString(obj.getContrVinculoId().getDataInicio())
              : StringUtils.EMPTY;
          var dataFimContrato = obj.getContrVinculoId().getDataFim()!=null ? DateFormatter.localDateToString(obj.getContrVinculoId().getDataFim())
              : StringUtils.EMPTY;

          var dataInicioCarreira = obj.getCarreiraId()!=null ? DateFormatter.localDateToString(obj.getCarreiraId().getDataInicio())
              : StringUtils.EMPTY;
          var dataFimCarreira = obj.getCarreiraId()!=null ? DateFormatter.localDateToString(obj.getCarreiraId().getDataFim())
              : StringUtils.EMPTY;

          response.setDataInicioContratoCarreira(dataInicioContrato.concat(" / ").concat(dataInicioCarreira));
          response.setDataFimContratoCarreira(dataFimContrato.concat(" / ").concat(dataFimCarreira));

          var start = DateFormatter.localDateToString(obj.getDataInicio());
          var end = obj.getDataFim() != null ? " / " + DateFormatter.localDateToString(obj.getDataFim()) : StringUtils.EMPTY;
          response.setDataInicioFim(start + end);
          return response;
        }).toList();

    var wrapper = new WrapperHistLaboralResponseDTO();
    wrapper.setHistorico(data);
    wrapper.setPageNumber(page.getTotalPages());
    wrapper.setPageSize(page.getSize());
    wrapper.setTotalElements(page.getTotalElements());
    wrapper.setTotalPages(page.getTotalPages());
    wrapper.setLast(page.isLast());
    wrapper.setFirst(page.isFirst());

    return wrapper;
  }

  public ValidarNovoHistoricoLaboralDTO getHistoricoLaboralById(GetHistoricoLaboralByIdQuery query) {
    var entity = tiposRelacionamentoEntityRepository.findByUuid(UUID.fromString(query.getHistoricoId()))
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Histórico Laboral não encontrado"));

    return null;
  }


}
