package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.RegistarSubstituicaoCommand;
import cv.inps.rh.funcionario.application.dto.SubstituicaoDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
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
}
