package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.AssiduidadeListDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaAssiduidadadeDTO;
import cv.inps.rh.assiduidade.application.queries.GetListaMovimentosResumidosQuery;
import cv.inps.rh.assiduidade.infrastructure.persistence.projections.AssiduidadeResumoViewRow;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.repository.AssiduidadeSinteseDiarioEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class MovimentoResumoService {

  private final AssiduidadeSinteseDiarioEntityRepository sinteseRepository;

  @Transactional(readOnly = true)
  public WrapperListaAssiduidadadeDTO getListaMovimentosResumidos(GetListaMovimentosResumidosQuery query) {

    int pageSize = Integer.parseInt(query.getPageSize());
    int pageNumber = Integer.parseInt(query.getPageNumber());

    String colaborador = StringUtils.hasText(query.getColaborador()) ? query.getColaborador() : null;
    var di = StringUtils.hasText(query.getDataInicio()) ? DateFormatter.stringToLocalDate(query.getDataInicio()) : null;
    var df = StringUtils.hasText(query.getDataFim()) ? DateFormatter.stringToLocalDate(query.getDataFim()) : null;
    Estado estado = null;
    if (StringUtils.hasText(query.getEstado())) {
      try {
        estado = Estado.valueOf(query.getEstado());
      } catch (IllegalArgumentException ignored) {
      }
    }

    Pageable pageable = PageRequest
        .of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "ano")
            .and(Sort.by(Sort.Direction.DESC, "mes")));

    Page<AssiduidadeResumoViewRow> page = sinteseRepository.listarResumoMensal(
        colaborador,
        di,
        df,
        estado != null ? estado.getCode() : null,
        query.getDirecao(),
        query.getSeccao(),
        query.getIlha(),
        pageable
    );

    var wrapper = new WrapperListaAssiduidadadeDTO();
    PageMapper.fillPagination(page, wrapper);

    for (var row : page.getContent()) {
      var dto = new AssiduidadeListDTO();
      dto.setId(null);
      dto.setUuid(null);
      dto.setUuidFuncionairio(row.getUuidFuncionairio());
      dto.setNomeColaborador(row.getNomeColaborador());
      dto.setDirecao(row.getDirecao());
      dto.setTotalFalta(row.getTotalFalta() != null ? row.getTotalFalta().intValue() : null);
      dto.setTotalDias(row.getTotalDias() != null ? row.getTotalDias().intValue() : null);
      dto.setTotalHorasTrabalhadas(row.getTotalHorasTrabalhadas() != null ? row.getTotalHorasTrabalhadas().intValue() : null);
     // dto.setTotalHorasAusentes(row.getTotalHorasAusentes() != null ? row.getTotalHorasAusentes().intValue() : null);
      dto.setTotalHorasAusentes(secondsToHHmm( row.getTotalHorasAusentes()));
      dto.setTotalHoraExtra(row.getTotalHoraExtra() != null ? row.getTotalHoraExtra().intValue() : null);
      dto.setTotalHoraAlmoco(row.getTotalHoraAlmoco() != null ? row.getTotalHoraAlmoco().intValue() : null);

      var estadoDiaria = Estado.valueOf(Estado.class, row.getEstado());
      dto.setEstado(estadoDiaria.getCode());
      dto.setEstadoDesc(estadoDiaria.getCode());

      String mesRef = (row.getMes() != null && row.getAno() != null) ? String.format("%02d/%d", row.getMes(), row.getAno()) : null;
      dto.setMesReferencia(mesRef);
      dto.setData(row.getData()!=null ? DateFormatter.localDateToString(row.getData()) : null);
      wrapper.getContent().add(dto);
    }

    return wrapper;
  }


  private String secondsToHHmm(Long totalSeconds) {
    if (totalSeconds == null) return "00:00";

    long hours = totalSeconds / 3600;
    long minutes = (totalSeconds % 3600) / 60;

    return String.format("%02d:%02d", hours, minutes);
  }


}
