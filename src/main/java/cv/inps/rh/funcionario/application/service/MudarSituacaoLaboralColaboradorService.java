package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.InativarAtivarColaboradorCommand;
import cv.inps.rh.funcionario.application.dto.AtivarInativarColaboradorDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.FuncionarioMapper;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamSitLaboralEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MudarSituacaoLaboralColaboradorService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final ParamSitLaboralEntityRepository paramSitLaboralEntityRepository;
  private final FuncionarioMapper funcionarioMapper;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper dadosContratuaisMapper;

  @Transactional
  public AtivarInativarColaboradorDTO execute(InativarAtivarColaboradorCommand command) {

    throw new UnsupportedOperationException("Operacao nao suportada.");

    /*var dto = command.getAtivarinativarcolaborador();
    var funcionarioPublicId = IdentificadorUnico.from(command.getId()).getValor();

    var funcionario = funcionarioEntityRepository.findByUuid(funcionarioPublicId)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("funcionario nao encontrado com id" + command.getId()));

    var tipoRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario);
    tipoRelacionamentoAtual.setEstActAdm(0);
    tipoRelacionamentoAtual.setDataFim(LocalDate.now());

    if (tipoRelacionamentoAtual == null) {
      throw IgrpResponseStatusException.badRequest("funcionario nao possui tipo de relacionamento ativo");
    }

    var tipoRelacionamentoNovo = dadosContratuaisMapper.clone(tipoRelacionamentoAtual);
    tipoRelacionamentoNovo.setEstActAdm(1);
    tipoRelacionamentoNovo.setDataInicio(LocalDate.now());
    tipoRelacionamentoNovo.setEstado(Estado.P);
    tipoRelacionamentoNovo.setTipoSituacao("SITUACAO_LABORAL");
    tipoRelacionamentoNovo.setReferente("MUDANCA_SITUACAO_LAB ");


    var sl = dadosContratuaisMapper.toSituacaoLaboralInicial(dc, param, Estado.P);
    sl.setFunId(funcionario);
    funcionario.setSituacoesLaborais(java.util.List.of(sl));


    funcionarioEntityRepository.save(funcionario);

    return dto;*/
  }
}
