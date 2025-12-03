package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.DadosPessoaisRespDTO;
import cv.inps.rh.funcionario.application.queries.GetDadosPessoaisQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.FuncionarioMapper;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DadosPessoaisReadService {

  private final FuncionarioMapper funcionarioMapper;
  private final FuncionarioEntityRepository funcionarioEntityRepository;

  @Transactional(readOnly = true)
  public DadosPessoaisRespDTO getDadosPessoais(GetDadosPessoaisQuery query) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(IdentificadorUnico.from(query.getIdFuncionario()).valor());
    return funcionarioMapper.toDadosPessoaisRespDTO(funcionario);

  }
}
