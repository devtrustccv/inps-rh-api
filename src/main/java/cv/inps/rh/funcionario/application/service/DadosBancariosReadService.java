package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.DadosBancariosRespDTO;
import cv.inps.rh.funcionario.application.queries.GetDadosBancariosQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosBancariosMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.DadosBancariosEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DadosBancariosReadService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final DadosBancariosEntityRepository dadosBancariosEntityRepository;
  private final DadosBancariosMapper dadosBancariosMapper;

  @Transactional(readOnly = true)
  public List<DadosBancariosRespDTO> getDadosBancarios(GetDadosBancariosQuery query) {
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(IdentificadorUnico.from(query.getIdFuncionario()).valor());

    // Lista normal mostra A + P + I (tudo menos eliminados). validacao=true (so P) e para o
    // ecra de validacao no get de detalhes do funcionario.
    var estados = query.isValidacao() ? List.of(Estado.P) : List.of(Estado.A, Estado.P, Estado.I);

    var dadosBancarios  = dadosBancariosEntityRepository.findByFuncionarioIdAndEstados(
        funcionario.getUuid(), estados
    );
    return dadosBancariosMapper.toDadosBancariosRespDTOList(dadosBancarios);
  }
}
