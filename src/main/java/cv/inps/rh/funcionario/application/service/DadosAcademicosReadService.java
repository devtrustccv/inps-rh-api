package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.DadosAcademicosProfResponseDTO;
import cv.inps.rh.funcionario.application.queries.GetDadosAcademicosQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.ExperienciaProfissionalMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.FormacaoFeitaMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.HabilitacaoLiterariaMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.ExperienciaProfEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FormacaoFeitaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.HabilitacaoLiterariaEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DadosAcademicosReadService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final HabilitacaoLiterariaEntityRepository habilitacaoLiterariaEntityRepository;
  private final FormacaoFeitaEntityRepository formacaoFeitaEntityRepository;
  private final ExperienciaProfEntityRepository experienciaProfEntityRepository;

  private final HabilitacaoLiterariaMapper habilitacaoLiterariaMapper;
  private final FormacaoFeitaMapper formacaoFeitaMapper;
  private final ExperienciaProfissionalMapper experienciaProfissionalMapper;


  @Transactional(readOnly = true)
  public DadosAcademicosProfResponseDTO getDadosAcademicos(GetDadosAcademicosQuery query) {
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(IdentificadorUnico.from(query.getIdFuncionario()).valor());

    var estados = query.isValidacao() ? List.of(Estado.P) : List.of(Estado.A, Estado.I);

    var habilitacaoesLiterarias = habilitacaoLiterariaEntityRepository
        .findByFuncionarioIdAndEstados(funcionario.getUuid(), estados);

    var formacoesFeitas = formacaoFeitaEntityRepository.findByFuncionarioIdAndEstados(funcionario.getUuid(), estados);

    var experienciasProfissionais = experienciaProfEntityRepository.findByFuncionarioIdAndEstados(funcionario.getUuid(), estados);

    var dadosAcademicosProfResponseDTO = new DadosAcademicosProfResponseDTO();
    dadosAcademicosProfResponseDTO.setHabilitacoesLiterarias(habilitacaoLiterariaMapper.toHabilitacaoLiterariaRespDTOList(habilitacaoesLiterarias));
    dadosAcademicosProfResponseDTO.setFormacoesFeitas(formacaoFeitaMapper.toFormacaoFeitaRespDTOList(formacoesFeitas));
    dadosAcademicosProfResponseDTO.setExperienciasProfssionais(experienciaProfissionalMapper.toExperienciaProfissionalRespDTOList(experienciasProfissionais));
    return dadosAcademicosProfResponseDTO;

  }
}
