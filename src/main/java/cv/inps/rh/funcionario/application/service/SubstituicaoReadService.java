package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.SubstituicaoDetalheDTO;
import cv.inps.rh.funcionario.application.dto.SubstituicaoSumaryDTO;
import cv.inps.rh.funcionario.application.dto.WrapperSubstituicaoSumaryDTO;
import cv.inps.rh.shared.util.PageMapper;
import cv.inps.rh.funcionario.application.queries.GetSubstituicaoByIdQuery;
import cv.inps.rh.funcionario.application.queries.ListaSubstituicaoQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.SubstituicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.SubstituicaoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubstituicaoReadService {

  private final SubstituicaoEntityRepository substituicaoEntityRepository;


  @Transactional(readOnly = true)
  public WrapperSubstituicaoSumaryDTO listar(ListaSubstituicaoQuery query) {

    int page = Integer.parseInt(query.getPageNumber());
    int size = Integer.parseInt(query.getPageSize());

    Pageable pageable = PageRequest.of(page, size);

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).valor();

    // Inversão (UX): o dossiê mostra as substituições onde o funcionário é o SUBSTITUTO (quem
    // substitui). Inclui pendentes (A/P/I).
    var estados = List.of(Estado.A, Estado.P, Estado.I);

    var pageResult = substituicaoEntityRepository
        .findBySubstitutoTiprelId_FunId_Uuid_AndEstadoIn(idFuncionario, estados, pageable)
        .map(this::toDto);

    var wrapper = new WrapperSubstituicaoSumaryDTO();
    wrapper.setContent(pageResult.getContent());
    PageMapper.fillPagination(pageResult, wrapper);
    return wrapper;
  }


  @Transactional(readOnly = true)
  public SubstituicaoDetalheDTO getById(GetSubstituicaoByIdQuery query) {

    var uuid = IdentificadorUnico.from(query.getSubstituicaoId()).valor();
    var e = substituicaoEntityRepository.findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Substituição não encontrada: " + query.getSubstituicaoId()));

    var dto = new SubstituicaoDetalheDTO();
    dto.setId(e.getId());
    dto.setUuidSubstituicao(e.getUuid() != null ? e.getUuid().toString() : null);
    dto.setEstado(e.getEstado() != null ? e.getEstado().name() : null);
    dto.setEstadoDesc(e.getEstado() != null ? e.getEstado().getDescription() : null);

    // substitutoTiprelId = o substituto (o colaborador escolhido para substituir)
    var substituto = e.getSubstitutoTiprelId();
    if (substituto != null && substituto.getFunId() != null) {
      var f = substituto.getFunId();
      dto.setColaboradorSubstitutoId(f.getId());
      dto.setColaboradorSubstitutoUuid(f.getUuid() != null ? f.getUuid().toString() : null);
      dto.setColaboradorSubstitutoNome(f.getNome());
    }

    // substituidoTiprelId = o colaborador substituído (o que está a ser substituído)
    var substituido = e.getSubstituidoTiprelId();
    if (substituido != null && substituido.getFunId() != null) {
      var f = substituido.getFunId();
      dto.setColaboradorSubstituidoId(f.getId());
      dto.setColaboradorSubstituidoUuid(f.getUuid() != null ? f.getUuid().toString() : null);
      dto.setColaboradorSubstituidoNome(f.getNome());
    }
    // Cargo da posição substituída
    if (substituido != null && substituido.getCargoId() != null) {
      dto.setCargo(substituido.getCargoId().getNome());
    }

    dto.setMotivoSubstituicao(e.getMotivo());
    dto.setDataInicio(e.getDataInicio() != null ? DateFormatter.localDateToString(e.getDataInicio()) : null);
    dto.setDataFim(e.getDataFim() != null ? DateFormatter.localDateToString(e.getDataFim()) : null);
    dto.setObs(e.getObs());

    return dto;
  }


  private SubstituicaoSumaryDTO toDto(SubstituicaoEntity e) {

    var dto = new SubstituicaoSumaryDTO();

    dto.setId(e.getId());
    dto.setUuidSubstituicao(e.getUuid() != null ? e.getUuid().toString() : null);
    dto.setEstado(e.getEstado() != null ? e.getEstado().name() : null);
    dto.setEstadoDesc(e.getEstado() != null ? e.getEstado().getDescription() : null);

    dto.setColaboradorSustituido(
        e.getSubstituidoTiprelId() != null ? e.getSubstituidoTiprelId().getFunId().getNome() : null
    );

    dto.setColaboradorSustituto(
        e.getSubstitutoTiprelId() != null ? e.getSubstitutoTiprelId().getFunId().getNome() : null
    );

    dto.setCargo(
        e.getSubstituidoTiprelId() != null && e.getSubstituidoTiprelId().getCargoId() != null
            ? e.getSubstituidoTiprelId().getCargoId().getNome() : null
    );

    dto.setDataInicio(
        e.getDataInicio() != null ? DateFormatter.localDateToString(e.getDataInicio()) : null
    );

    dto.setDataFim(
        e.getDataFim() != null ?  DateFormatter.localDateToString(e.getDataFim()) : null
    );

    dto.setMotivo(e.getMotivo());
    dto.setObs(e.getObs());


    return dto;
  }
}
