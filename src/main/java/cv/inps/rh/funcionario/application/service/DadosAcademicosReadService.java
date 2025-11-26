package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.DadosAcademicosProfResponseDTO;
import cv.inps.rh.funcionario.application.dto.DadosPessoaisRespDTO;
import cv.inps.rh.funcionario.application.queries.GetDadosAcademicosQuery;
import cv.inps.rh.funcionario.application.queries.GetDadosPessoaisQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.ExperienciaProfissionalMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.FamiliarMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.FormacaoFeitaMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.HabilitacaoLiterariaMapper;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DadosAcademicosReadService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final HabilitacaoLiterariaMapper habilitacaoLiterariaMapper;
  private final FormacaoFeitaMapper formacaoFeitaMapper;
  private final ExperienciaProfissionalMapper experienciaProfissionalMapper;


  @Transactional(readOnly = true)
  public DadosAcademicosProfResponseDTO getDadosAcademicos(GetDadosAcademicosQuery query) {
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(IdentificadorUnico.from(query.getIdFuncionario()).getValor());

    var dadosAcademicosProfResponseDTO = new DadosAcademicosProfResponseDTO();
    dadosAcademicosProfResponseDTO.setHabilitacoesLiterarias(habilitacaoLiterariaMapper.toHabilitacaoLiterariaRespDTOList(funcionario.getHabilitacoesLiterarias()));
    dadosAcademicosProfResponseDTO.setFormacoesFeitas(formacaoFeitaMapper.toFormacaoFeitaRespDTOList(funcionario.getFormacoesFeitas()));
    dadosAcademicosProfResponseDTO.setExperienciasProfssionais(experienciaProfissionalMapper.toExperienciaProfissionalRespDTOList(funcionario.getExperienciasProfissionais()));
    return dadosAcademicosProfResponseDTO;

  }
}
