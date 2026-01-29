package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.AssiduidadeListDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaAssiduidadadeDTO;
import cv.inps.rh.assiduidade.application.queries.GetListaMovimentosResumidosQuery;
import cv.inps.rh.assiduidade.infrastructure.persistence.projections.AssiduidadeResumoViewRow;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.repository.AssiduidadeSinteseDiarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.VResumoAssiduidadeEntityRepository;
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

  private final VResumoAssiduidadeEntityRepository vResumoAssiduidadeEntityRepository;

  @Transactional(readOnly = true)
  public WrapperListaAssiduidadadeDTO getListaMovimentosResumidos(GetListaMovimentosResumidosQuery query) {

    int pageSize = Integer.parseInt(query.getPageSize());
    int pageNumber = Integer.parseInt(query.getPageNumber());


    return null;

  }





}
