package cv.inps.rh.funcionario.application.service;

import cv.igrp.framework.core.domain.Command;
import cv.inps.rh.funcionario.application.commands.RegistarSubstituicaoCommand;
import cv.inps.rh.funcionario.application.commands.ValidarSubstituicaoCommand;
import cv.inps.rh.funcionario.application.dto.SubstituicaoDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.OrdemServicoEntity;
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

  @Transactional
  public SubstituicaoDTO registrar(RegistarSubstituicaoCommand command) {

    var dto = command.getSubstituicao();

    var idFuncionarioSubstituto = IdentificadorUnico.from(dto.getColaboradorSubstituto()).getValor();
    var funcionarioSubstituto = funcionarioEntityRepository.findByUuid(idFuncionarioSubstituto).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Funcionário não encontrado:: "+idFuncionarioSubstituto)
    );

    var idFuncionarioSubstituido = IdentificadorUnico.from(command.getIdFuncionario()).getValor();
    var funcionarioSubstituido = funcionarioEntityRepository.findByUuid(idFuncionarioSubstituido).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Funcionário não encontrado:: "+idFuncionarioSubstituido)
    );


    var substituicao = new SubstituicaoEntity();
    substituicao.setTiprelIdDe(funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituido));
    substituicao.setTiprelIdPara(funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituto));
    substituicao.setDataInicio(dto.getDataInicio());
    substituicao.setDataFim(dto.getDataFim());
    substituicao.setObs(dto.getObs());
    substituicao.setUuid(IdentificadorUnico.create().getValor());
    substituicao.setEstado(Estado.P);
    substituicaoEntityRepository.save(substituicao);

    var validacao = dadosContratuaisMapper.toValidacaoInsert("INSERT","SUBSTITUICAO", Estado.P);
    validacao.setFunId(funcionarioSubstituido);
    validacao.setTiprelId(substituicao.getTiprelIdDe());
    validacao.setFunId(funcionarioSubstituido);
    validacao.setReferenciaId(substituicao.getId());

    funcionarioSubstituido.getValidacoes().add(validacao);

    funcionarioEntityRepository.save(funcionarioSubstituido);

    return dto;

  }

  public SubstituicaoDTO validar(ValidarSubstituicaoCommand command) {
    var dto = command.getSubstituicao();

    var idSusbtituicao = IdentificadorUnico.from(command.getSubstituicaoId()).getValor();

    var idFuncionarioSubstituto = IdentificadorUnico.from(dto.getColaboradorSubstituto()).getValor();
    var funcionarioSubstituto = funcionarioEntityRepository.findByUuid(idFuncionarioSubstituto).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Funcionário não encontrado:: "+idFuncionarioSubstituto)
    );

    var substituicao = substituicaoEntityRepository.findByUuid(idSusbtituicao).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Substituição não encontrada:: "+idSusbtituicao)
    );
    substituicao.setDataInicio(dto.getDataInicio());
    substituicao.setDataFim(dto.getDataFim());
    substituicao.setObs(dto.getObs());
    substituicao.setTiprelIdPara(funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituto));


    var idFuncionarioSubstituido = IdentificadorUnico.from(command.getId()).getValor();
    var funcionarioSubstituido = funcionarioEntityRepository.findByUuid(idFuncionarioSubstituido).orElseThrow(
        () -> IgrpResponseStatusException.badRequest("Funcionário não encontrado:: "+idFuncionarioSubstituido)
    );

    if(dto.getValidar()!=null) {
      var estado = dto.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;

      if(estado.equals(Estado.A)){
        OrdemServicoEntity ordemServicoEntity = new OrdemServicoEntity();
        ordemServicoEntity.setFunId(funcionarioSubstituido);
        ordemServicoEntity.setTiprelId(funcionarioRules.getTipoRelacionamentoAtual(funcionarioSubstituido) );
        ordemServicoEntity.setReferente("SUBSTITUICAO");
        ordemServicoEntity.setDescricao("Substituicao");
        ordemServicoEntity.setNuOrdem("1"); // todo fix later
        ordemServicoEntity.setEstado(Estado.A);
        funcionarioSubstituido.getOrdemServicos().add(ordemServicoEntity);

      }

      substituicao.setEstado(estado);

      funcionarioSubstituido.getValidacoes().stream()
          .filter(v -> v.getEstado() == Estado.P)
          .filter(v -> "SUBSTITUICAO".equals(v.getReferenciaName()) && "INSERT".equals(v.getTipoAccao()))
          .findFirst()
          .ifPresent(v -> v.setEstado(estado));

    }


    substituicaoEntityRepository.save(substituicao);
    funcionarioEntityRepository.save(funcionarioSubstituido);

    return dto;
  }
}
