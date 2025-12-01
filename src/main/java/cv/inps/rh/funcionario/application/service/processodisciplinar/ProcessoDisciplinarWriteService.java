package cv.inps.rh.funcionario.application.service.processodisciplinar;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.ProcessoDisciplinarRequestDTO;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessoDisciplinarEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessoDisciplinarEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProcessoDisciplinarWriteService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final ProcessoDisciplinarEntityRepository processoDisciplinarEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;

  public UUID saveNovoProcessoDisciplinar(String funcionarioId, ProcessoDisciplinarRequestDTO request) {

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funcionarioId));

    var process = new ProcessoDisciplinarEntity();
    process.setTiprelId(tiposRelacionamentoEntityRepository.findByUuidOrThrow(UUID.fromString(request.getVinculoReferente())));
    process.setFunId(funcionario);
    process.setUuid(UuidCreator.getTimeOrderedEpoch());
    populateEntity(request, process);

    return processoDisciplinarEntityRepository.save(process).getUuid();
  }


  public void updateProcessoDisciplinar(String processoDisciplinarId, ProcessoDisciplinarRequestDTO request) {

    var process = processoDisciplinarEntityRepository.findByUuidOrThrow(UUID.fromString(processoDisciplinarId));

    process.setTiprelId(tiposRelacionamentoEntityRepository.findByUuidOrThrow(UUID.fromString(request.getVinculoReferente())));
    populateEntity(request, process);

    processoDisciplinarEntityRepository.save(process);
  }

  private void populateEntity(ProcessoDisciplinarRequestDTO request, ProcessoDisciplinarEntity process) {
    process.setNumProceso(request.getNumeroProcesso());
    process.setEntidade(request.getEntidade());
    process.setTpProcesso(request.getTipoProcesso());
    process.setPenaDiscp(request.getPenaDisciplinar());
    process.setNumBo(request.getNumeroBO());
    process.setEstado(request.getEstadoProcesso());
    process.setDateInicPd(DateFormatter.stringToLocalDate(request.getDataInicioPd()));
    process.setDateFimPd(DateFormatter.stringToLocalDate(request.getDataFimPd()));
    process.setDateInicPena(DateFormatter.stringToLocalDate(request.getDataInicioPena()));
    process.setDateFimPena(DateFormatter.stringToLocalDate(request.getDataFimPena()));
    process.setDataPublBo(DateFormatter.stringToLocalDate(request.getDatPublicacaoBO()));
    process.setDataOrdemServ(DateFormatter.stringToLocalDate(request.getDataOrdemServico()));
    process.setDataEmissOfa(DateFormatter.stringToLocalDate(request.getDataEmissaoOfa()));
    process.setNumOrdemServ(request.getNumeroOrdemServico());
    process.setNumOfa(request.getNumeroOfa());
  }

  public void deleteProcessoDisciplinar(String processoDisciplinarId) {
    var process = processoDisciplinarEntityRepository.findByUuidOrThrow(UUID.fromString(processoDisciplinarId));
    process.setEstado(Estado.E.name());
    processoDisciplinarEntityRepository.save(process);
  }
}
