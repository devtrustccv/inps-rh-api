package cv.inps.rh.funcionario.application.service.processodisciplinar;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.ProcessoDisciplinarRequestDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessoDisciplinarEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessoDisciplinarEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.ValidationUtil;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProcessoDisciplinarWriteService {

  private static final Logger LOGGER = LoggerFactory.getLogger(ProcessoDisciplinarWriteService.class);

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final ProcessoDisciplinarEntityRepository processoDisciplinarEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final FuncionarioRules funcionarioRules;

  public SuccessResponseDTO saveNovoProcessoDisciplinar(String funcionarioId, ProcessoDisciplinarRequestDTO request) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));
    var tiprel = tiposRelacionamentoEntityRepository.findByUuidOrThrow(UUID.fromString(request.getVinculoReferente()));

    var process = new ProcessoDisciplinarEntity();
    process.setTiprelId(tiprel);
    process.setFunId(funcionario);
    process.setUuid(UuidCreator.getTimeOrderedEpoch());
    populateEntity(request, process);
    process.setEstado(Estado.P.name());
    process.setFlgCondenacao("N");

    process = processoDisciplinarEntityRepository.save(process);

    var validation = new ValidacaoEntity();
    validation.setTipoAccao(TipoAcao.INSERT.name());
    validation.setReferenciaName(Referencia.PROCESSO_DISCIPLINAR.name());
    validation.setReferenciaId(process.getId());
    // referencia_uuid = uuid do processo — âncora do ciclo CORRIGIR (devolver/reabrir por referenciaUuid).
    validation.setReferenciaUuid(process.getUuid());
    validation.setTiprelId(tiprel);
    validation.setEstado(Estado.P);
    validation.setUuid(UuidCreator.getTimeOrderedEpoch());
    validation.setFunId(funcionario);
    validacaoEntityRepository.save(validation);

    return new SuccessResponseDTO(true, process.getUuid().toString(), "Processo disciplinar registado.", List.of());
  }

  public SuccessResponseDTO updateProcessoDisciplinar(String processoDisciplinarId, ProcessoDisciplinarRequestDTO request) {

    var process = processoDisciplinarEntityRepository.findByUuidOrThrow(UUID.fromString(processoDisciplinarId));

    // CORRIGIR (checker devolve ao maker): processo pendente P -> C e validação P -> C, SEM aplicar
    // payload. O maker corrige e reenvia por este mesmo endpoint com validar=null (C -> P). Âncora =
    // process.uuid.
    if (ValidationUtil.isCorrigir(request.getValidar())) {
      if (!Estado.P.name().equals(process.getEstado())
          || funcionarioRules.getValidacaoPendenteByReferenciaUuid(process.getUuid(), TipoAcao.INSERT, Referencia.PROCESSO_DISCIPLINAR).isEmpty()) {
        throw IgrpResponseStatusException.badRequest("Só é possível devolver para correção um processo disciplinar pendente de validação.");
      }
      funcionarioRules.devolverParaCorrecao(process.getUuid(), Estado.P, Referencia.PROCESSO_DISCIPLINAR);
      process.setEstado(Estado.C.name());
      processoDisciplinarEntityRepository.save(process);
      LOGGER.info("[CORRIGIR] PROCESSO_DISCIPLINAR devolvido para correção (processo={}).", process.getUuid());
      return new SuccessResponseDTO(true, process.getUuid().toString(),
          "Processo disciplinar devolvido para correção.", List.of());
    }

    // Guard: processo em correção não pode ser validado antes de reenviado pelo maker.
    if (Estado.C.name().equals(process.getEstado()) && request.getValidar() != null) {
      throw IgrpResponseStatusException.badRequest(
          "Processo disciplinar em correção: não pode ser validado. Corrija e reenvie primeiro.");
    }

    process.setTiprelId(tiposRelacionamentoEntityRepository.findByUuidOrThrow(UUID.fromString(request.getVinculoReferente())));
    populateEntity(request, process);

    // Maker reenvia a correção (C -> P): edições aplicadas acima; reabre para validação.
    if (Estado.C.name().equals(process.getEstado())) {
      process.setEstado(Estado.P.name());
      funcionarioRules.reabrirParaValidacao(process.getUuid(), Referencia.PROCESSO_DISCIPLINAR);
      processoDisciplinarEntityRepository.save(process);
      return new SuccessResponseDTO(true, process.getUuid().toString(),
          "Processo disciplinar corrigido e reenviado para validação.", List.of());
    }

    if (request.getValidar() != null && Estado.P.name().equals(process.getEstado())) {
      var novoEstado = "S".equals(request.getValidar()) ? Estado.A : Estado.I;
      process.setEstado(novoEstado.name());

      funcionarioRules.getValidacaoPendente(process.getFunId().getUuid(), TipoAcao.INSERT, Referencia.PROCESSO_DISCIPLINAR)
          .ifPresent(v -> {
            v.setEstado(novoEstado);
            validacaoEntityRepository.save(v);
          });
    }

    processoDisciplinarEntityRepository.save(process);

    return new SuccessResponseDTO(true, process.getUuid().toString(), "Processo disciplinar actualizado.", List.of());
  }

  private void populateEntity(ProcessoDisciplinarRequestDTO request, ProcessoDisciplinarEntity process) {
    process.setNumProceso(ValidationUtil.trimToNull(request.getNumeroProcesso()));
    process.setEntidade(ValidationUtil.trimToNull(request.getEntidade()));
    process.setTpProcesso(ValidationUtil.trimToNull(request.getTipoProcesso()));
    process.setPenaDiscp(ValidationUtil.trimToNull(request.getPenaDisciplinar()));
    process.setDateInicPd(DateFormatter.stringToLocalDate(request.getDataInicioPd()));
    process.setDateFimPd(DateFormatter.stringToLocalDate(request.getDataFimPd()));
    process.setDateInicPena(DateFormatter.stringToLocalDate(request.getDataInicioPena()));
    process.setDateFimPena(DateFormatter.stringToLocalDate(request.getDataFimPena()));
    process.setDataOrdemServ(DateFormatter.stringToLocalDate(request.getDataOrdemServico()));
    process.setNumOrdemServ(ValidationUtil.trimToNull(request.getNumeroOrdemServico()));
  }

  public SuccessResponseDTO deleteProcessoDisciplinar(String processoDisciplinarId) {
    var process = processoDisciplinarEntityRepository.findByUuidOrThrow(UUID.fromString(processoDisciplinarId));
    process.setEstado(Estado.E.name());
    processoDisciplinarEntityRepository.save(process);

    return new SuccessResponseDTO(true, process.getUuid().toString(), "Processo disciplinar eliminado.", List.of());
  }
}
