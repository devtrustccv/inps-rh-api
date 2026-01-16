package cv.inps.rh.funcionario.application.service.historicolaboral;

import cv.inps.rh.funcionario.application.dto.*;
import cv.inps.rh.funcionario.application.queries.GetRelacaoLaboralByCarreiraIdQuery;
import cv.inps.rh.funcionario.application.queries.GetHistoricoLaboralQuery;
import cv.inps.rh.funcionario.application.queries.GetRelacaoLaboralByFunIdQuery;
import cv.inps.rh.funcionario.application.queries.GetRelacaoLaboralQuery;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
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
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;

  public WrapperHistLaboralResponseDTO getHistoricoLaboral2(GetHistoricoLaboralQuery query) {
    var pageRequest = PageRequest.of(
        Integer.parseInt(query.getPagina()),
        Integer.parseInt(query.getTamanho()));

    var di = StringUtils.isNotBlank(query.getDataInicio()) ? DateFormatter.stringToLocalDate(query.getDataInicio())
        : null;
    var df = StringUtils.isNotBlank(query.getDataFim()) ? DateFormatter.stringToLocalDate(query.getDataFim()) : null;

    var page = tiposRelacionamentoEntityRepository.historicoLaboralViewByFuncionario(
        query.getFuncionarioId(),
        query.getReferencia(),
        query.getTipoSituacao(),
        query.getSituacaoLaboral(),
        di,
        df,
        pageRequest);

    var data = page.getContent().stream().map(r -> {
      var dto = new HistoricoLaboralResponseDTO();
      dto.setUltimoMovimento(Objects.equals(r.getUltimoVinculo(), 1));
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
      dto.setUuidFuncionario(r.getFuncionarioUuid());

      var dataInicio = r.getDataInicio() != null ? DateFormatter.localDateToString(r.getDataInicio())
          : StringUtils.EMPTY;
      var dataFim = r.getDataFim() != null ? DateFormatter.localDateToString(r.getDataFim()) : StringUtils.EMPTY;
      dto.setDataInicioFim(dataInicio.concat(" / ").concat(dataFim));

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
          response.setUltimoMovimento(Objects.equals(obj.getEstActAdm(), 1));

          ofNullable(obj.getTipoSituacao()).ifPresent(response::setTipoSituacao);
          ofNullable(obj.getContrVinculoId().getTpContratoId().getNome())
              .ifPresent(response::setTipoContrato);
          ofNullable(obj.getContrVinculoId().getVinculoId())
              .map(ParamVinculoEntity::getNome)
              .ifPresent(response::setVinculo);
          ofNullable(obj.getMobId().getSecaoId()).map(SecaoEntity::getInstId)
              .map(InstituicaoEntity::getNome)
              .ifPresent(response::setDirecao);
          ofNullable(obj.getMobId().getSecaoId()).map(SecaoEntity::getNome)
              .ifPresent(response::setSeccao);
          ofNullable(obj.getCarreiraId()).map(CarreiraEntity::getCarrPccsId)
              .map(ParamCarreiraEntity::getNome)
              .ifPresent(response::setCarreira);
          ofNullable(obj.getCarreiraId().getEscalaoId()).map(ParamEscalaoEntity::getEscalao)
              .ifPresent(response::setReferenciaEscalao);
          ofNullable(obj.getCargoId()).map(ParamCargoEntity::getNome)
              .ifPresent(response::setCargo);
          ofNullable(obj.getSituacLaboralId())
              .map(SituacaoLaboralEntity::getSituacaoLaboralId)
              .map(ParamSituacaoEntity::getNome)
              .ifPresent(response::setSituacaoLaboral);

          response.setId(obj.getId());
          response.setUuid(obj.getFunId().getUuid().toString());

          var dataInicio = obj.getDataInicio() != null
              ? DateFormatter.localDateToString(
                  obj.getDataInicio())
              : StringUtils.EMPTY;

          var dataFim = obj.getDataFim() != null
              ? DateFormatter.localDateToString(
                  obj.getDataFim())
              : StringUtils.EMPTY;

          response.setDataInicioFim(
              dataInicio.concat(" / ").concat(dataFim));

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

  public WrapperRelacaoLaboralSumaryDTO getRelacaoLaboral(GetRelacaoLaboralQuery query) {
    var rows = tiposRelacionamentoEntityRepository.relacaoLaboralFromViewByFuncionario(query.getFuncionarioId());

    var data = rows.stream().map(r -> {
      var dto = new RelacaoLaboralSumaryDTO();
      dto.setCarreiraId(r.getCarreiraId());
      dto.setCarreiraUuid(r.getCarreiraUuid());
      System.out.println("r.getSituacaoAtual():: " + r.getSituacaoAtual());
      dto.setSituacaoAtual(Objects.equals(r.getSituacaoAtual(), 1));
      dto.setVinculo(r.getVinculoDesc());
      dto.setDirecao(r.getDirecaoDesc());
      dto.setSeccao(r.getSeccaoDesc());
      dto.setCarreira(r.getCarreiraDesc());
      dto.setDataInicioFimCarreira(r.getDataCarreira());
      dto.setDataInicioFimContrato(r.getDataContrato());
      dto.setCargo(r.getCargoDesc());
      dto.setSituacaoLaboral(r.getSituacaoLaboralDesc());
      return dto;
    }).toList();

    var wrapper = new WrapperRelacaoLaboralSumaryDTO();
    wrapper.setContent(data);
    wrapper.setPageNumber(0);
    wrapper.setPageSize(data.size());
    wrapper.setTotalElements((long) data.size());
    wrapper.setTotalPages(1);
    wrapper.setFirst(true);
    wrapper.setLast(true);
    return wrapper;
  }

  public RelacaoLaboralDTO getRelacaoLaboralByCarreiraId(GetRelacaoLaboralByCarreiraIdQuery query) {
    var entity = tiposRelacionamentoEntityRepository.findByCarreiraId_uuid(UUID.fromString(query.getCarreiraId()));
    if (entity == null) {
      throw IgrpResponseStatusException.notFound("Histórico Laboral não encontrado");
    }

    var dto = new RelacaoLaboralDTO();

    var contrato = entity.getContrVinculoId();
    if (contrato != null) {
      dto.setContrato(contrato.getTpContratoId() != null ? contrato.getTpContratoId().getNome()
          : null);
      dto.setVinculo(contrato.getVinculoId() != null ? contrato.getVinculoId().getNome() : null);
    }

    dto.setSalario(entity.getSalario() != null
        ? entity.getSalario()
        : (entity.getCarreiraId() != null ? entity.getCarreiraId().getSalario() : null));

    dto.setSituacaoAtual(entity.getEstActAdm() + "");

    var mob = entity.getMobId();
    if (mob != null) {
      dto.setTipoMobilidade(mob.getTipoSituacao());
      dto.setDataInicioMobilidade(mob.getDataInicio());
      dto.setDataFimMobilidade(mob.getDataFim());
    }


    var lt = entity.getLocTrabId();
    if (lt != null) {
      dto.setLocalTrabalho(lt.getId());
      dto.setPais(lt.getPaisId() != null ? lt.getPaisId().getNome() : null);
      dto.setIlha(lt.getIlhaId() != null ? lt.getIlhaId().getNome() : null);
    }

    var car = entity.getCarreiraId();
    if (car != null) {
      dto.setTipoAlteracaoCarreira(car.getTipoSituacao());
      dto.setDataInicioCarreira(car.getDataInicio());
      dto.setDataFimCarreira(car.getDataFim());
    }
    var carrPcc = entity.getCarreiraId()!= null ? entity.getCarreiraId().getCarrPccsId() : null;
    if (carrPcc != null)
      dto.setCarreira(carrPcc.getId());
    var cat =entity.getCarreiraId()!= null ?  entity.getCarreiraId().getCategoriaId() : null;
    if (cat != null)
      dto.setCategoria(cat.getId());
    var esc = entity.getCarreiraId()!= null ? entity.getCarreiraId().getEscalaoId() : null;
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
          : sit.getTipoSituacao());
      dto.setDataInicioSituacao(sit.getDataInicio());
      dto.setDataFimSituacao(sit.getDataFim());
      dto.setObservacao(sit.getObs());
    }

    return dto;
  }


  public RelacaoLaboralDTO getRelacaoLaboralByFunId(GetRelacaoLaboralByFunIdQuery query) {
    var idFunc = IdentificadorUnico.from(query.getIdFuncionario()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(idFunc);

    var atual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var dto = new RelacaoLaboralDTO();

    var contrato = atual.getContrVinculoId();

    if (contrato != null) {
      dto.setContrato(contrato.getTpContratoId() != null ? contrato.getTpContratoId().getNome()
          : null);
      dto.setVinculo(contrato.getVinculoId() != null ? contrato.getVinculoId().getNome() : null);
    }

    dto.setSalario(atual.getSalario() != null
        ? atual.getSalario()
        : (atual.getCarreiraId() != null ? atual.getCarreiraId().getSalario() : null));

    dto.setSituacaoAtual(atual.getEstActAdm() + "");

    var mob = atual.getMobId();
    if (mob != null) {
      dto.setTipoMobilidade(mob.getTipoSituacao());
      dto.setDataInicioMobilidade(mob.getDataInicio());
      dto.setDataFimMobilidade(mob.getDataFim());
    }


    var lt = atual.getLocTrabId();
    if (lt != null) {
      dto.setLocalTrabalho(lt.getId());
      dto.setPais(lt.getPaisId() != null ? lt.getPaisId().getNome() : null);
      dto.setIlha(lt.getIlhaId() != null ? lt.getIlhaId().getNome() : null);
    }

    var car = atual.getCarreiraId();
    if (car != null) {
      dto.setTipoAlteracaoCarreira(car.getTipoSituacao());
      dto.setDataInicioCarreira(car.getDataInicio());
      dto.setDataFimCarreira(car.getDataFim());
    }
    var carrPcc = atual.getCarreiraId()!= null ? atual.getCarreiraId().getCarrPccsId() : null;
    if (carrPcc != null)
      dto.setCarreira(carrPcc.getId());
    var cat =atual.getCarreiraId()!= null ?  atual.getCarreiraId().getCategoriaId() : null;
    if (cat != null)
      dto.setCategoria(cat.getId());
    var esc = atual.getCarreiraId()!= null ? atual.getCarreiraId().getEscalaoId() : null;
    if (esc != null)
      dto.setEscalao(esc.getId());
    var cargo = atual.getCargoId();
    if (cargo != null)
      dto.setCargo(cargo.getId());

    var sit = atual.getSituacLaboralId();
    if (sit != null) {
      var paramSit = sit.getSituacaoLaboralId();
      dto.setSituacaoLaboral(paramSit != null ? paramSit.getId() : null);
      dto.setMotivo(sit.getMotivoSitLabId() != null ? String.valueOf(sit.getMotivoSitLabId().getId())
          : sit.getTipoSituacao());
      dto.setDataInicioSituacao(sit.getDataInicio());
      dto.setDataFimSituacao(sit.getDataFim());
      dto.setObservacao(sit.getObs());
    }

    return dto;
  }

}
