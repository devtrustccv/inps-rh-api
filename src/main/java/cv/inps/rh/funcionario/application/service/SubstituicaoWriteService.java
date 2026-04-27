package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.RegistarSubstituicaoCommand;
import cv.inps.rh.funcionario.application.commands.ValidarSubstituicaoCommand;
import cv.inps.rh.funcionario.application.dto.SubstituicaoDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.service.OrdemServicoWriteService;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.SubstituicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SubstituicaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SubstituicaoWriteService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final SubstituicaoEntityRepository substituicaoEntityRepository;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final FuncionarioRules funcionarioRules;
  private final OrdemServicoWriteService ordemServicoWriteService;

  @Transactional
  public SubstituicaoDTO registrar(RegistarSubstituicaoCommand command) {

    var dto = command.getSubstituicao();

    var idFuncionarioSubstituto = IdentificadorUnico.from(dto.getColaboradorSubstituto()).valor();
    var funcionarioSubstituto = funcionarioEntityRepository.findByUuid(idFuncionarioSubstituto).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Funcionário não encontrado:: "+idFuncionarioSubstituto)
    );

    var idFuncionarioSubstituido = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionarioSubstituido = funcionarioEntityRepository.findByUuid(idFuncionarioSubstituido).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Funcionário não encontrado:: "+idFuncionarioSubstituido)
    );


    var substituicao = new SubstituicaoEntity();
    substituicao.setSubstitutoTiprelId(funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituido.getUuid()));
    substituicao.setSubstituidoTiprelId(funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituto.getUuid()));
    substituicao.setDataInicio(dto.getDataInicio());
    substituicao.setDataFim(dto.getDataFim());
    substituicao.setObs(dto.getObs());
    substituicao.setUuid(IdentificadorUnico.create().valor());
    substituicao.setEstado(Estado.P);
    substituicaoEntityRepository.save(substituicao);

    var validacao = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.INSERT.name(), Referencia.SUBSTITUICAO.name(), Estado.P);
    validacao.setFunId(funcionarioSubstituido);
    validacao.setTiprelId(substituicao.getSubstitutoTiprelId());
    validacao.setFunId(funcionarioSubstituido);
    validacao.setReferenciaId(substituicao.getId());

    funcionarioSubstituido.getValidacoes().add(validacao);

    funcionarioEntityRepository.save(funcionarioSubstituido);

    return dto;

  }

  public SubstituicaoDTO validar(ValidarSubstituicaoCommand command) {
    var dto = command.getSubstituicao();

    var idSusbtituicao = IdentificadorUnico.from(command.getSubstituicaoId()).valor();

    var idFuncionarioSubstituto = IdentificadorUnico.from(dto.getColaboradorSubstituto()).valor();
    var funcionarioSubstituto = funcionarioEntityRepository.findByUuid(idFuncionarioSubstituto).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Funcionário não encontrado:: "+idFuncionarioSubstituto)
    );

    var substituicao = substituicaoEntityRepository.findByUuid(idSusbtituicao).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Substituição não encontrada:: "+idSusbtituicao)
    );
    substituicao.setDataInicio(dto.getDataInicio());
    substituicao.setDataFim(dto.getDataFim());
    substituicao.setObs(dto.getObs());
    substituicao.setSubstituidoTiprelId(funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituto.getUuid()));


    var idFuncionarioSubstituido = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionarioSubstituido = funcionarioEntityRepository.findByUuid(idFuncionarioSubstituido).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Funcionário não encontrado:: "+idFuncionarioSubstituido)
    );

    if(dto.getValidar()!=null) {
      var estado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

      if(estado.equals(Estado.A)){
        ordemServicoWriteService.criar(
            funcionarioSubstituido,
            funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituido.getUuid()),
            dto.getTipoOrdemServico());
      }

      substituicao.setEstado(estado);

      funcionarioRules.getValidacaoPendente(funcionarioSubstituido.getUuid(), TipoAcao.INSERT, Referencia.SUBSTITUICAO)
          .ifPresent(v -> v.setEstado(estado));

    }


    substituicaoEntityRepository.save(substituicao);
    funcionarioEntityRepository.save(funcionarioSubstituido);

    return dto;
  }
}
