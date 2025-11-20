package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import cv.inps.rh.funcionario.application.dto.MobilidadeListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListMobilidadeDTO;
import cv.inps.rh.funcionario.application.queries.GetListMobilidadesQuery;
import cv.inps.rh.funcionario.application.queries.GetMobilidadeByIdQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.MobilidadeMapper;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.MobilidadeEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MobilidadeReadService {

  private final MobilidadeEntityRepository mobilidadeEntityRepository;
  private final MobilidadeMapper mobilidadeMapper;

  public WrapperListMobilidadeDTO getListMobilidade(GetListMobilidadesQuery query) {

    int pageNumber = query.getPageNumber() != null ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = query.getPageSize() != null ? Integer.parseInt(query.getPageSize()) : 50;
    int startRow = pageNumber * pageSize + 1;
    int endRow = (pageNumber + 1) * pageSize;

    var dataInicio = StringUtils.hasText(query.getDataInicio()) ? DateFormatter.stringToLocalDate(query.getDataInicio()) : null;
    var dataFim = StringUtils.hasText(query.getDataFim()) ? DateFormatter.stringToLocalDate(query.getDataFim()) : null;

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).toString();

    var mobilidades = mobilidadeEntityRepository.findAllMobilidades(
        idFuncionario,
        query.getTipoMobilidade(),
        dataInicio,
        dataFim,
        startRow,
        endRow
    );

    long totalElements = mobilidades.isEmpty() ? 0 : mobilidades.getFirst().getTotalCount();

    List<MobilidadeListDTO> content = mobilidades.stream()
        .map(mobilidadeMapper::mobilidadeListDTO)
        .toList();

    var wrapper = new WrapperListMobilidadeDTO();
    wrapper.setContent(content);
    wrapper.setPageNumber(pageNumber);
    wrapper.setPageSize(pageSize);
    wrapper.setTotalElements(totalElements);
    wrapper.setTotalPages((int) Math.ceil((double) totalElements / pageSize));
    wrapper.setFirst(pageNumber == 0);
    wrapper.setLast(pageNumber + 1 >= wrapper.getTotalPages());

    return wrapper;
  }

  public MobilidadeDTO getMobilidade(GetMobilidadeByIdQuery query) {

    IdentificadorUnico id = IdentificadorUnico.from(query.getId());
    var mobilidade = mobilidadeEntityRepository.findByUuid(id.getValor()).orElseThrow(
        () -> IgrpResponseStatusException.notFound("mobilidade nao encontrada com id"+query.getId())
    );

    return mobilidadeMapper.mobilidadeDTO(mobilidade);
  }
}
