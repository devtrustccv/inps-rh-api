package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.SubstituicaoSumaryDTO;
import cv.inps.rh.funcionario.application.queries.ListaSubstituicaoQuery;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.SubstituicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.SubstituicaoEntityRepository;
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
  public List<SubstituicaoSumaryDTO> listar(ListaSubstituicaoQuery query) {

    int page = Integer.parseInt(query.getPageNumber());
    int size = Integer.parseInt(query.getPageSize());

    Pageable pageable = PageRequest.of(page, size);

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).valor();


    return substituicaoEntityRepository
        .findBySubstituidoTiprelId_FunId_Uuid(idFuncionario, pageable)
        .map(this::toDto)
        .getContent();
  }


  private SubstituicaoSumaryDTO toDto(SubstituicaoEntity e) {

    var dto = new SubstituicaoSumaryDTO();

    dto.setId(e.getId());
    dto.setUuidSubstituicao(e.getUuid() != null ? e.getUuid().toString() : null);
    dto.setEstado(e.getEstado() != null ? e.getEstado().name() : null);
    dto.setEstadoDesc(e.getEstado() != null ? e.getEstado().getDescription() : null);

    dto.setColaboradorSustituido(
        e.getSubstitutoTiprelId() != null ? e.getSubstitutoTiprelId().getFunId().getNome() : null
    );

    dto.setColaboradorSustituto(
        e.getSubstituidoTiprelId() != null ? e.getSubstituidoTiprelId().getFunId().getNome(): null
    );

    dto.setCargo(
        e.getSubstitutoTiprelId() != null ? e.getSubstitutoTiprelId().getCargoId().getNome() : null
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
