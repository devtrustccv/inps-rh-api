package cv.inps.rh.funcionario.application.service.historicolaboral;

import cv.inps.rh.funcionario.application.dto.HistoricoLaboralResponseDTO;
import cv.inps.rh.funcionario.application.dto.ValidarNovoHistoricoLaboralDTO;
import cv.inps.rh.funcionario.application.dto.WrapperHistLaboralResponseDTO;
import cv.inps.rh.funcionario.application.queries.GetRelacaoLaboralByCarreiraId;
import cv.inps.rh.funcionario.application.queries.GetHistoricoLaboralQuery;
import cv.inps.rh.funcionario.application.queries.GetRelacaoLaboralQuery;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
@Transactional
public class HistoricoLaboralReadService {

  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;

  public WrapperHistLaboralResponseDTO getHistoricoLaboral2(GetHistoricoLaboralQuery query) {
    var pageRequest = PageRequest.of(
        Integer.parseInt(query.getPagina()),
        Integer.parseInt(query.getTamanho()));

    var di = StringUtils.isNotBlank(query.getDataInicio()) ? DateFormatter.stringToLocalDate(query.getDataInicio()) : null;
    var df = StringUtils.isNotBlank(query.getDataFim()) ? DateFormatter.stringToLocalDate(query.getDataFim()) : null;

    var page = tiposRelacionamentoEntityRepository.historicoLaboralViewByFuncionario(
        query.getFuncionarioId(),
        query.getReferencia(),
        query.getTipoSituacao(),
        query.getSituacaoLaboral(),
        di,
        df,
        pageRequest
    );

    var data = page.getContent().stream().map(r -> {
      var dto = new HistoricoLaboralResponseDTO();
      dto.setUltimoMovimento(StringUtils.EMPTY);
      dto.setTipoSituacao(r.getTipoSituacaoDesc());
      dto.setTipoContrato(r.getTipoContratoDesc());
      dto.setVinculo(r.getVinculoDesc());
      dto.setDirecao(r.getDirecaoDesc());
      dto.setSeccao(r.getSeccaoDesc());
      dto.setCarreira(r.getCarreiraDesc());
      dto.setReferenciaEscalao(r.getReferenciaEscalaoDesc());
      dto.setCargo(r.getCargoDesc());
      dto.setSituacaoLaboral(r.getSituacaoLaboralDesc());
      dto.setId(r.getTiprelId());
      dto.setUuid(r.getFuncionarioUuid());

      var dataInicio = r.getDataInicio() != null ? DateFormatter.localDateToString(r.getDataInicio()) : StringUtils.EMPTY;
      var dataFim = r.getDataFim() != null ? DateFormatter.localDateToString(r.getDataFim()) : StringUtils.EMPTY;
      dto.setDataInicioFimCarreira(dataInicio.concat(" / ").concat(dataFim));
      dto.setDataInicioFimContrato(dataInicio.concat(" / ").concat(dataFim));

      dto.setSituacaoAtual(Objects.equals(r.getUltimoVinculo(), 1));
      return dto;
    }).toList();

    var wrapper = new WrapperHistLaboralResponseDTO();
    wrapper.setHistorico(data);
    PageMapper.fillPagination(page, wrapper);
    return wrapper;
  }

  public WrapperHistLaboralResponseDTO getHistoricoLaboral(GetHistoricoLaboralQuery query) {

    var pageRequest = PageRequest.of(
        Integer.parseInt(query.getPagina()),
        Integer.parseInt(query.getTamanho()));

    var page = tiposRelacionamentoEntityRepository.findByFunId_UuidAndEstado(
        UUID.fromString(query.getFuncionarioId()),
        Estado.A,
        pageRequest);

    var data = page.stream()
        .map(obj -> {
          var response = new HistoricoLaboralResponseDTO();
          response.setUltimoMovimento(DateFormatter.localDateToString(obj.getUltProc()));

          ofNullable(obj.getTipoSituacao()).ifPresent(response::setTipoSituacao);
          ofNullable(obj.getContrVinculoId().getTpContratoId().getNome())
              .ifPresent(response::setTipoContrato);
          ofNullable(obj.getContrVinculoId().getVinculoId())
              .map(ParamVinculoEntity::getNome)
              .ifPresent(response::setVinculo);
          ofNullable(obj.getSeccaoId()).map(SecaoEntity::getInstId)
              .map(InstituicaoEntity::getNome)
              .ifPresent(response::setDirecao);
          ofNullable(obj.getSeccaoId()).map(SecaoEntity::getNome)
              .ifPresent(response::setSeccao);
          ofNullable(obj.getCarreiraId()).map(CarreiraEntity::getCarrPccsId)
              .map(ParamCarreiraEntity::getNome)
              .ifPresent(response::setCarreira);
          ofNullable(obj.getEscalaoId()).map(ParamEscalaoEntity::getEscalao)
              .ifPresent(response::setReferenciaEscalao);
          ofNullable(obj.getCargoId()).map(ParamCargoEntity::getNome)
              .ifPresent(response::setCargo);
          ofNullable(obj.getSituacLaboralId())
              .map(SituacaoLaboralEntity::getSituacaoLaboralId)
              .map(ParamSituacaoEntity::getNome)
              .ifPresent(response::setSituacaoLaboral);

          response.setId(obj.getId());
          response.setUuid(obj.getFunId().getUuid().toString());

          var dataInicioContrato = obj.getContrVinculoId().getDataInicio() != null
              ? DateFormatter.localDateToString(
                  obj.getContrVinculoId().getDataInicio())
              : StringUtils.EMPTY;
          var dataFimContrato = obj.getContrVinculoId().getDataFim() != null
              ? DateFormatter.localDateToString(
                  obj.getContrVinculoId().getDataFim())
              : StringUtils.EMPTY;

          var dataInicioCarreira = obj.getCarreiraId() != null
              ? DateFormatter.localDateToString(
                  obj.getCarreiraId().getDataInicio())
              : StringUtils.EMPTY;

          var dataFimCarreira = obj.getCarreiraId() != null
              ? DateFormatter.localDateToString(
                  obj.getCarreiraId().getDataFim())
              : StringUtils.EMPTY;

          response.setDataInicioFimContrato(
              dataInicioContrato.concat(" / ").concat(dataFimContrato));

          response.setDataInicioFimCarreira(dataInicioCarreira.concat(" / ").concat(dataFimCarreira));

          response.setSituacaoAtual(Objects.equals(obj.getEstActAdm(), 1));

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

  public WrapperHistLaboralResponseDTO getRelacaoLaboral(GetRelacaoLaboralQuery query) {
    var rows = tiposRelacionamentoEntityRepository.relacaoLaboralFromViewByFuncionario(query.getFuncionarioId());

    var data = rows.stream().map(r -> {
      var dto = new HistoricoLaboralResponseDTO();
      dto.setTipoContrato(r.getContratoDesc());
      dto.setVinculo(r.getVinculoDesc());
      dto.setDirecao(r.getDirecaoDesc());
      dto.setSeccao(r.getSeccaoDesc());
      dto.setCarreira(r.getCarreiraDesc());
      dto.setReferenciaEscalao(r.getEscalaoDesc());
      dto.setDataInicioFimCarreira(r.getDataCarreira());
      dto.setDataInicioFimContrato(r.getDataContrato());
      dto.setCargo(r.getCargoDesc());
      dto.setSituacaoLaboral(r.getSituacaoLaboralDesc());
      return dto;
    }).toList();

    var wrapper = new WrapperHistLaboralResponseDTO();
    wrapper.setHistorico(data);
    wrapper.setPageNumber(0);
    wrapper.setPageSize(data.size());
    wrapper.setTotalElements((long) data.size());
    wrapper.setTotalPages(1);
    wrapper.setFirst(true);
    wrapper.setLast(true);
    return wrapper;
  }

  public ValidarNovoHistoricoLaboralDTO getRelacaoLaboralById(GetRelacaoLaboralByCarreiraId query) {
    var entity = tiposRelacionamentoEntityRepository.findByUuid(UUID.fromString(query.getCarreiraId()))
        .orElseThrow(() -> IgrpResponseStatusException
            .notFound("Histórico Laboral não encontrado"));

    var dto = new ValidarNovoHistoricoLaboralDTO();

    var contrato = entity.getContrVinculoId();
    if (contrato != null) {
      dto.setContrato(contrato.getTpContratoId() != null ? contrato.getTpContratoId().getNome()
          : null);
      dto.setVinculo(contrato.getVinculoId() != null ? contrato.getVinculoId().getNome() : null);
    }

    dto.setSalario(entity.getSalario() != null
        ? entity.getSalario()
        : (entity.getCarreiraId() != null ? entity.getCarreiraId().getSalario() : null));

    var mob = entity.getMobId();
    if (mob != null) {
      dto.setTipoMobilidade(mob.getTipoSituacao());
      dto.setDataInicioMobilidade(mob.getDataInicio());
      dto.setDataFimMobilidade(mob.getDataFim());
    }

    var inst = entity.getInstitId();
    if (inst != null)
      dto.setDirecao(inst.getId());
    var sec = entity.getSeccaoId();
    if (sec != null)
      dto.setSecao(sec.getId());
    var lt = entity.getLocTrabId();
    if (lt != null) {
      dto.setLocalTrabalho(lt.getId());
      dto.setPais(lt.getPaisId() != null ? lt.getPaisId().getNome() : null);
      dto.setIlha(lt.getIlhaId() != null ? lt.getIlhaId().getId() : null);
    }

    var car = entity.getCarreiraId();
    if (car != null) {
      dto.setTipoAlteracaoCarreira(car.getTipoSituacao());
      dto.setDataInicioCarreira(car.getDataInicio());
      dto.setDataFimCarreira(car.getDataFim());
    }
    var carrPcc = entity.getCarrPccId();
    if (carrPcc != null)
      dto.setCarreira(carrPcc.getId());
    var cat = entity.getCategoriaId();
    if (cat != null)
      dto.setCategoria(cat.getId());
    var esc = entity.getEscalaoId();
    if (esc != null)
      dto.setEscalao(esc.getId());
    var cargo = entity.getCargoId();
    if (cargo != null)
      dto.setCargo(cargo.getId());

    var sit = entity.getSituacLaboralId();
    if (sit != null) {
      var paramSit = sit.getSituacaoLaboralId();
      dto.setSituacaoLaboral(paramSit != null ? paramSit.getId() : null);
      dto.setMotivo(sit.getMotivoSitLabId() != null ? String.valueOf(sit.getMotivoSitLabId().getId())
          : sit.getMotivoSitLab());
      dto.setDataInicioSituacao(sit.getDataInicio());
      dto.setDataFimSituacao(sit.getDataFim());
      dto.setObservacao(sit.getObs());
      dto.setSituacaoAtual(paramSit != null ? paramSit.getNome() : null);
    }

    return dto;
  }


}
