package cv.inps.rh.funcionario.application.service.processodisciplinar;

import cv.inps.rh.funcionario.application.dto.ProcessoDisciplinarResponseDTO;
import cv.inps.rh.shared.application.constants.Domains;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessoDisciplinarEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DomainEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessoDisciplinarEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
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

  public List<ProcessoDisciplinarResponseDTO> getProcessosDisciplinares(String funcionarioId, boolean validacao) {

    var estados = validacao
        ? List.of(Estado.P.name())
        // DC-013: o registo novo fica P (a aguardar validação) e não aparecia na lista do dossiê;
        // C (devolvido para correção) também tem de aparecer para o maker o corrigir.
        : List.of(Estado.A.name(), Estado.I.name(), Estado.P.name(), Estado.C.name());
    var processes = processoDisciplinarEntityRepository.findByFunId_UuidAndEstadoIn(UUID.fromString(funcionarioId), estados);
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
    response.setEstado(obj.getEstado());
    ofNullable(obj.getTiprelId()).ifPresent(r -> response.setVinculoReferente(r.getUuid().toString()));
    response.setNumeroProcesso(obj.getNumProceso());
    response.setEntidade(obj.getEntidade());
    response.setTipoProcesso(obj.getTpProcesso());
    response.setPenaDisciplinar(obj.getPenaDiscp());
    response.setDataInicioPd(DateFormatter.localDateToString(obj.getDateInicPd()));
    response.setDataFimPd(DateFormatter.localDateToString(obj.getDateFimPd()));
    response.setDataInicioPena(DateFormatter.localDateToString(obj.getDateInicPena()));
    response.setDataFimPena(DateFormatter.localDateToString(obj.getDateFimPena()));
    response.setNumeroBO(obj.getNumBo());
    response.setDatPublicacaoBO(DateFormatter.localDateToString(obj.getDataPublBo()));
    response.setNumeroOrdemServico(obj.getNumOrdemServ());
    response.setDataOrdemServico(DateFormatter.localDateToString(obj.getDataOrdemServ()));
    response.setNumeroOfa(obj.getNumOfa());
    response.setDataEmissaoOfa(DateFormatter.localDateToString(obj.getDataEmissOfa()));
    ofNullable(obj.getTiprelId()).ifPresent(r -> {
      ofNullable(r.getMobId()).ifPresent(mob -> {
        ofNullable(mob.getInstidId()).ifPresent(i -> response.setDirecao(i.getNome()));
        ofNullable(mob.getSecaoId()).ifPresent(s -> response.setSeccao(s.getNome()));
      });
      ofNullable(r.getContrVinculoId()).ifPresent(c ->
        ofNullable(c.getVinculoId()).ifPresent(v -> response.setVinculo(v.getNome()))
      );
    });
    return response;
  }

  public ProcessoDisciplinarResponseDTO getProcessoDisciplinarById(String processoDisciplinarId) {

    var obj = processoDisciplinarEntityRepository.findByUuidOrThrow(UUID.fromString(processoDisciplinarId));

    var processType = domainEntityRepository.getActiveDomainByCode(Domains.TP_PROCESSO_DISC.name());

    return mapToResponse(processType, obj);
  }

}
