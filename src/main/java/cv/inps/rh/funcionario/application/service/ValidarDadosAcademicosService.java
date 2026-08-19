package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarDadosAcademicosCommand;
import cv.inps.rh.funcionario.application.rules.ColaboradorValidationRules;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.ExperienciaProfissionalMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.FormacaoFeitaMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.HabilitacaoLiterariaMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ValidarDadosAcademicosService {


  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper contratuaisEntityMapper;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final HabilitacaoLiterariaMapper habilitacaoLiterariaMapper;
  private final FormacaoFeitaMapper formacaoFeitaMapper;
  private final ExperienciaProfissionalMapper experienciaProfissionalMapper;
  private final ColaboradorValidationRules colaboradorValidationRules;

  @Transactional
  public SuccessResponseDTO executar(ValidarDadosAcademicosCommand command) {

    var dto = command.getValidardadosacademicos();
    var dadosAcademicosProfReqDTO = dto.getDadosAcademicosProf();
    //var estadoValidacao = dto.getValidar();

    var funcionarioPublicId = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);

    //boolean temPendentes = funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.UPDATE, Referencia.DADOS_ACADEMICOS);

    // 1) Se tem pendentes mas não enviou validar → erro
    /*if (temPendentes && estadoValidacao == null) {
      throw IgrpResponseStatusException.badRequest(
          "Funcionario possui validação pendente de dados academicos, por favor validar"
      );
    }*/


    colaboradorValidationRules.validarHabilitacoesLiterarias(dadosAcademicosProfReqDTO.getHabilitacoesLiterarias());

    var habilitacoesLiterarias = habilitacaoLiterariaMapper
        .syncHabilitacoes(funcionario.getHabilitacoesLiterarias(),
        dadosAcademicosProfReqDTO.getHabilitacoesLiterarias(), funcionario, Estado.A);

    var formacoesFeitas = formacaoFeitaMapper
        .syncFormacoes(funcionario.getFormacoesFeitas(),
        dadosAcademicosProfReqDTO.getFormacoesFeitas(), funcionario, Estado.A);

    var experienciasProfissionais = experienciaProfissionalMapper
        .syncExperiencias(funcionario.getExperienciasProfissionais(),
        dadosAcademicosProfReqDTO.getExperienciasProfssionais(), funcionario, Estado.A);

    funcionario.setHabilitacoesLiterarias(habilitacoesLiterarias);
    funcionario.setFormacoesFeitas(formacoesFeitas);
    funcionario.setExperienciasProfissionais(experienciasProfissionais);

   /* if (temPendentes) {

      var novoEstado = (estadoValidacao == EstadoValidacao.SIM)
          ? Estado.A
          : Estado.I;

      mudarEstado(funcionario, novoEstado);

      funcionarioEntityRepository.save(funcionario);
      return dto;
    }*/

   /* var tipoRel = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

    var validacao = contratuaisEntityMapper
        .toValidacaoInsert(TipoAcao.UPDATE.name(), Referencia.DADOS_ACADEMICOS.name(), Estado.P);

    validacao.setFunId(funcionario);
    validacao.setTiprelId(tipoRel);

    funcionario.getValidacoes().add(validacao);*/

    funcionarioEntityRepository.saveAndFlush(funcionario);

    /*validacaoEntityRepository
        .findByFunId_UuidAndEstadoAndTipoAccaoAndReferenciaName(
            funcionario.getUuid(),
            Estado.P,
            TipoAcao.UPDATE.name(),
            Referencia.DADOS_ACADEMICOS.name()
        )
        .ifPresent(v -> {
          v.setReferenciaId(saved.getId());
          validacaoEntityRepository.save(v);
        });*/

    return new SuccessResponseDTO(true, funcionario.getUuid().toString(), "Dados academicos actualizados.", List.of());

  }

  /*private void mudarEstado(FuncionarioEntity funcionarioEntity, Estado novoEstado) {

    var habilitacoes = funcionarioEntity.getHabilitacoesLiterarias();
    if (habilitacoes != null) {
      habilitacoes.stream()
          .filter(h -> h != null && h.getEstado() == Estado.P)
          .forEach(h -> h.setEstado(novoEstado));
    }

    var formacoes = funcionarioEntity.getFormacoesFeitas();
    if (formacoes != null) {
      formacoes.stream()
          .filter(f -> f != null && f.getEstado() == Estado.P)
          .forEach(f -> f.setEstado(novoEstado));
    }

    var experiencias = funcionarioEntity.getExperienciasProfissionais();
    if (experiencias != null) {
      experiencias.stream()
          .filter(e -> e != null && e.getEstado() == Estado.P)
          .forEach(e -> e.setEstado(novoEstado));
    }

    funcionarioRules.getValidacaoPendente(funcionarioEntity.getUuid(), TipoAcao.UPDATE, Referencia.DADOS_ACADEMICOS)
        .ifPresent(v -> v.setEstado(novoEstado));

  }*/
}
