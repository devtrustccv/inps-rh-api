package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarDadosBancariosCommand;
import cv.inps.rh.funcionario.application.dto.ValidarDadosBancariosDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosBancariosMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.FuncionarioMapper;
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
public class ValidarDadosBancariosService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioMapper funcionarioMapper;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper contratuaisEntityMapper;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final DadosBancariosMapper dadosBancariosMapper;

  @Transactional
  public ValidarDadosBancariosDTO executar(ValidarDadosBancariosCommand command) {

    var dto = command.getValidardadosbancarios();
    var dadosBancariosReqDTO = dto.getDadosBancarios();
    var estadoValidacao = dto.getValidar();

    var funcionarioPublicId = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);

    boolean temPendentes = funcionarioRules.temValidacaoPendente(funcionario, "UPDATE", "DADOS_BANCARIOS");

    // 1) Se tem pendentes mas não enviou validar → erro
    if (temPendentes && estadoValidacao == null) {
      throw IgrpResponseStatusException.badRequest(
          "Funcionario possui validação pendente de dados pessoais, por favor validar"
      );
    }

    var dadosBancarios = dadosBancariosMapper.syncBancarios(funcionario.getDadosBancarios(), dadosBancariosReqDTO);

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
        .toValidacaoInsert("UPDATE", "DADOS_BANCARIOS", Estado.P);

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
    if (funcionarioEntity == null) return;

    var bancarios = funcionarioEntity.getDadosBancarios();
    if (bancarios != null) bancarios.forEach(b -> { if (b != null) b.setEstado(novoEstado); });

    funcionarioEntity.getValidacoes().stream()
        .filter(v -> v.getEstado() == Estado.P)
        .filter(v -> "DADOS_BANCARIOS".equals(v.getReferenciaName()) && "UPDATE".equals(v.getTipoAccao()))
        .findFirst()
        .ifPresent(v -> v.setEstado(novoEstado));
  }
}
