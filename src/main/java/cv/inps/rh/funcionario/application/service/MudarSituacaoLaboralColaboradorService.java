package cv.inps.rh.funcionario.application.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.InativarAtivarColaboradorCommand;
import cv.inps.rh.funcionario.application.constants.SituacaoLaboral;
import cv.inps.rh.funcionario.application.constants.custom.MotivoSituacaoLaboral;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.SituacaoLaboralEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class MudarSituacaoLaboralColaboradorService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final ParamSitLaboralEntityRepository paramSitLaboralEntityRepository;
  private final TiposRelacionamentoEntityRepository tipoRelacionamentoEntityRepository;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final DefPagamentoEntityRepository defPagamentoEntityRepository;
  private final CarreiraEntityRepository carreiraEntityRepository;
  private final MobilidadeEntityRepository mobilidadeEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final ContratoEntityRepository contratoEntityRepository;
  private final SituacaoLaboralEntityRepository situacaoLaboralEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper dadosContratuaisMapper;

  private final ParamSituacaoDetalheEntityRepository paramSituacaoDetalheEntityRepository;

  @Transactional
  public void execute(InativarAtivarColaboradorCommand command) {

    var dto = command.getAtivarinativarcolaborador();

    var paramSituacaoLaboral = paramSitLaboralEntityRepository.getReferenceById(dto.getSituacaoLaboralId());
    var paramSituacaoLaboralDetalhe = paramSituacaoDetalheEntityRepository.getReferenceById(dto.getMotivoId());

    var funcionarioPublicId = IdentificadorUnico.from(command.getId()).valor();

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);

    var tiposRelacionamentoAtual = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());
    tiposRelacionamentoAtual.setDataFim(LocalDate.now());
    tiposRelacionamentoAtual.setEstActAdm(0);

    var situacaoLaboral = new SituacaoLaboralEntity();
    situacaoLaboral.setUuid(UuidCreator.getTimeOrdered());
    situacaoLaboral.setSituacaoLaboralId(paramSituacaoLaboral);
    situacaoLaboral.setMotivoSitLabId(paramSituacaoLaboralDetalhe);
    situacaoLaboral.setContrVinculoId(tiposRelacionamentoAtual.getContrVinculoId());
    situacaoLaboral.setObs(dto.getObservacao());
    situacaoLaboral.setDataInicio(LocalDate.now());
    situacaoLaboral.setEstado(Estado.P);
    situacaoLaboralEntityRepository.save(situacaoLaboral);

    var tipoRelacionamentoNovo = dadosContratuaisMapper.clone(tiposRelacionamentoAtual);
    tipoRelacionamentoNovo.setDataInicio(LocalDate.now());
    tipoRelacionamentoNovo.setEstActAdm(1);
    tipoRelacionamentoNovo.setTipoSituacao("MUDANCA_SITUACAO_LABORAL");
    tipoRelacionamentoNovo.setObs(dto.getObservacao());
    tipoRelacionamentoNovo.setSituacLaboralId(situacaoLaboral);
    tipoRelacionamentoNovo.setReferente("SITUACAO_LABORAL");
    funcionario.getTiposrelacionamentos().add(tipoRelacionamentoNovo);

    var valid = dadosContratuaisMapper.toValidacaoInsert(TipoAcao.UPDATE.name(), Referencia.ESTADO_COLABORADOR.name(), Estado.P);
    valid.setFunId(funcionario);
    valid.setTiprelId(tipoRelacionamentoNovo);
    funcionario.getValidacoes().add(valid);

    funcionario.setEstado(paramSituacaoLaboral.getCodigo().equals(SituacaoLaboral.CESSADO.name()) ? Estado.I :
        paramSituacaoLaboral.getCodigo().equals(SituacaoLaboral.ATIVO.name()) ? Estado.A :
            funcionario.getEstado());

    funcionarioEntityRepository.save(funcionario);

  }
}
