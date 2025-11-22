package cv.inps.rh.funcionario.application.service.processodisciplinar;

import cv.inps.rh.funcionario.application.dto.ProcessoDisciplinarResponseDTO;
import cv.inps.rh.funcionario.infrastructure.utils.DateFormatter;
import cv.inps.rh.shared.application.constants.Domains;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessoDisciplinarEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProcessoDisciplinarReadService {

  private final ProcessoDisciplinarEntityRepository processoDisciplinarEntityRepository;
  private final DomainEntityRepository domainEntityRepository;

  public List<ProcessoDisciplinarResponseDTO> getProcessosDisciplinares(String funcionarioId) {

    var processes = processoDisciplinarEntityRepository.findByFunId_Uuid(UUID.fromString(funcionarioId));
    if (processes.isEmpty()) return List.of();

    var processType = domainEntityRepository.getActiveDomainByCode(Domains.TP_PROCESSO_DISC.name());

    return processes
        .stream()
        .map(obj -> {
          var response = new ProcessoDisciplinarResponseDTO();
          response.setProcessoDisciplinarId(obj.getUuid().toString());
          response.setProcessoDisciplinar(processType.get(obj.getTpProcesso()));
          response.setDataInicio(DateFormatter.localDateToString(obj.getDateInicPd()));
          response.setDataFim(DateFormatter.localDateToString(obj.getDateFimPd()));
          ofNullable(obj.getTiprelId()).ifPresent(r -> {
            ofNullable(r.getInstitId()).ifPresent(i -> response.setDirecao(i.getNome()));
            ofNullable(r.getSeccaoId()).ifPresent(s -> response.setSeccao(s.getNome()));
            ofNullable(r.getVinculoId()).ifPresent(v -> response.setVinculo(v.getNome()));
          });
          return response;
        })
        .toList();
  }

}
