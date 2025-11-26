package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.AgregadoDependenteRespDTO;
import cv.inps.rh.funcionario.application.queries.GetDadosFamiliaresQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.FamiliarMapper;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FamiliaresReadService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FamiliarMapper familiarMapper;

  @Transactional(readOnly = true)
  public List<AgregadoDependenteRespDTO> getFamiliares(GetDadosFamiliaresQuery query) {
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(IdentificadorUnico.from(query.getIdFuncionario()).getValor());
    return familiarMapper.toAgregadoDependenteRespDTOList(funcionario.getFamiliares());
  }


}
