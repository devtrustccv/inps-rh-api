package cv.inps.rh.funcionario.application.service.processodisciplinar;

import cv.inps.rh.funcionario.application.dto.ProcessoDisciplinarResponseDTO;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.application.constants.Domains;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessoDisciplinarEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessoDisciplinarEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProcessoDisciplinarReadService {

  private final ProcessoDisciplinarEntityRepository processoDisciplinarEntityRepository;
  private final DomainEntityRepository domainEntityRepository;

  public List<ProcessoDisciplinarResponseDTO> getProcessosDisciplinares(String funcionarioId) {

    var processes = processoDisciplinarEntityRepository.findByFunId_UuidAndEstadoNot(UUID.fromString(funcionarioId), Estado.E.name());
    if (processes.isEmpty())
      return List.of();

    var processType = domainEntityRepository.getActiveDomainByCode(Domains.TP_PROCESSO_DISC.name());

    return processes
        .stream()
        .map(obj -> mapToResponse(processType, obj))
        .toList();
  }

  private ProcessoDisciplinarResponseDTO mapToResponse(Map<String, String> processType, ProcessoDisciplinarEntity obj) {
    var response = new ProcessoDisciplinarResponseDTO();
    response.setProcessoDisciplinarId(obj.getUuid().toString());
    response.setProcessoDisciplinar(processType.get(obj.getTpProcesso()));
    response.setDataInicio(DateFormatter.localDateToString(obj.getDateInicPd()));
    response.setDataFim(DateFormatter.localDateToString(obj.getDateFimPd()));
    ofNullable(obj.getTiprelId()).ifPresent(r -> {
      ofNullable(r.getMobId().getInstidId()).ifPresent(i -> response.setDirecao(i.getNome()));
      ofNullable(r.getMobId().getSecaoId()).ifPresent(s -> response.setSeccao(s.getNome()));
      ofNullable(r.getContrVinculoId().getVinculoId()).ifPresent(v -> response.setVinculo(v.getNome()));
    });
    return response;
  }

  public ProcessoDisciplinarResponseDTO getProcessoDisciplinarById(String processoDisciplinarId) {

    var obj = processoDisciplinarEntityRepository.findByUuidOrThrow(UUID.fromString(processoDisciplinarId));

    var processType = domainEntityRepository.getActiveDomainByCode(Domains.TP_PROCESSO_DISC.name());

    return mapToResponse(processType, obj);
  }

}
