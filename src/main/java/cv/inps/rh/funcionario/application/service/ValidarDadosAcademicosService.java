package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarDadosAcademicosCommand;
import cv.inps.rh.funcionario.application.dto.ValidarDadosAcademicosDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ValidarDadosAcademicosService {


  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioMapper funcionarioMapper;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper contratuaisEntityMapper;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final HabilitacaoLiterariaMapper habilitacaoLiterariaMapper;
  private final FormacaoFeitaMapper formacaoFeitaMapper;
  private final ExperienciaProfissionalMapper experienciaProfissionalMapper;

  @Transactional
  public ValidarDadosAcademicosDTO executar(ValidarDadosAcademicosCommand command) {

    var dto = command.getValidardadosacademicos();
    var dadosAcademicosProfReqDTO = dto.getDadosAcademicosProf();
    var estadoValidacao = dto.getValidar();

    var funcionarioPublicId = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);

    boolean temPendentes = funcionarioRules.temValidacaoPendente(funcionario, "UPDATE", "DADOS_ACADEMICOS");

    // 1) Se tem pendentes mas não enviou validar → erro
    if (temPendentes && estadoValidacao == null) {
      throw IgrpResponseStatusException.badRequest(
          "Funcionario possui validação pendente de dados pessoais, por favor validar"
      );
    }


    var habilitacoesLiterarias = habilitacaoLiterariaMapper.syncHabilitacoes(funcionario.getHabilitacoesLiterarias(), dadosAcademicosProfReqDTO.getHabilitacoesLiterarias());
    var formacoesFeitas = formacaoFeitaMapper.syncFormacoes(funcionario.getFormacoesFeitas(), dadosAcademicosProfReqDTO.getFormacoesFeitas());
    var experienciasProfissionais = experienciaProfissionalMapper.syncExperiencias(funcionario.getExperienciasProfissionais(), dadosAcademicosProfReqDTO.getExperienciasProfssionais());

    funcionario.setHabilitacoesLiterarias(habilitacoesLiterarias);
    funcionario.setFormacoesFeitas(formacoesFeitas);
    funcionario.setExperienciasProfissionais(experienciasProfissionais);

    if (temPendentes) {

      var novoEstado = (estadoValidacao == EstadoValidacao.SIM)
          ? Estado.A
          : Estado.I;

      mudarEstado(funcionario, novoEstado);

      funcionarioEntityRepository.save(funcionario);
      return dto;
    }

    var tipoRel = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var validacao = contratuaisEntityMapper
        .toValidacaoInsert("UPDATE", "DADOS_ACADEMICOS", Estado.P);

    validacao.setFunId(funcionario);
    validacao.setTiprelId(tipoRel);

    funcionario.setValidacoes(java.util.List.of(validacao));

    var saved = funcionarioEntityRepository.save(funcionario);

    validacaoEntityRepository.findById(validacao.getId())
        .ifPresent(v -> {
          v.setReferenciaId(saved.getId());
          validacaoEntityRepository.save(v);
        });

    return dto;

  }

  private void mudarEstado(FuncionarioEntity funcionarioEntity, Estado novoEstado) {

    var habilitacoes = funcionarioEntity.getHabilitacoesLiterarias();
    if (habilitacoes != null) habilitacoes.forEach(h -> { if (h != null) h.setEstado(novoEstado); });

    var formacoes = funcionarioEntity.getFormacoesFeitas();
    if (formacoes != null) formacoes.forEach(f -> { if (f != null) f.setEstado(novoEstado); });

    var experiencias = funcionarioEntity.getExperienciasProfissionais();
    if (experiencias != null) experiencias.forEach(e -> { if (e != null) e.setEstado(novoEstado); });

    funcionarioEntity.getValidacoes().stream()
        .filter(v -> v.getEstado() == Estado.P)
        .filter(v -> "DADOS_ACADEMICOS".equals(v.getReferenciaName()) && "UPDATE".equals(v.getTipoAccao()))
        .findFirst()
        .ifPresent(v -> v.setEstado(novoEstado));


  }
}
