package cv.inps.rh.funcionario.application.service.processodisciplinar;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.ProcessoDisciplinarRequestDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessoDisciplinarEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessoDisciplinarEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProcessoDisciplinarWriteService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final ProcessoDisciplinarEntityRepository processoDisciplinarEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final FuncionarioRules funcionarioRules;

  public UUID saveNovoProcessoDisciplinar(String funcionarioId, ProcessoDisciplinarRequestDTO request) {

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
    validation.setTiprelId(tiprel);
    validation.setEstado(Estado.P);
    validation.setUuid(UuidCreator.getTimeOrderedEpoch());
    validation.setFunId(funcionario);
    validacaoEntityRepository.save(validation);

    return process.getUuid();
  }

  public void updateProcessoDisciplinar(String processoDisciplinarId, ProcessoDisciplinarRequestDTO request) {

    var process = processoDisciplinarEntityRepository.findByUuidOrThrow(UUID.fromString(processoDisciplinarId));
    process.setTiprelId(tiposRelacionamentoEntityRepository.findByUuidOrThrow(UUID.fromString(request.getVinculoReferente())));
    populateEntity(request, process);

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
  }

  private void populateEntity(ProcessoDisciplinarRequestDTO request, ProcessoDisciplinarEntity process) {
    process.setNumProceso(request.getNumeroProcesso());
    process.setEntidade(request.getEntidade());
    process.setTpProcesso(request.getTipoProcesso());
    process.setPenaDiscp(request.getPenaDisciplinar());
    process.setDateInicPd(DateFormatter.stringToLocalDate(request.getDataInicioPd()));
    process.setDateFimPd(DateFormatter.stringToLocalDate(request.getDataFimPd()));
    process.setDateInicPena(DateFormatter.stringToLocalDate(request.getDataInicioPena()));
    process.setDateFimPena(DateFormatter.stringToLocalDate(request.getDataFimPena()));
    process.setDataOrdemServ(DateFormatter.stringToLocalDate(request.getDataOrdemServico()));
    process.setNumOrdemServ(request.getNumeroOrdemServico());
  }

  public void deleteProcessoDisciplinar(String processoDisciplinarId) {
    var process = processoDisciplinarEntityRepository.findByUuidOrThrow(UUID.fromString(processoDisciplinarId));
    process.setEstado(Estado.E.name());
    processoDisciplinarEntityRepository.save(process);
  }
}
