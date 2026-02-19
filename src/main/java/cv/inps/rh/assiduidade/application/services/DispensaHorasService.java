package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.HorasDispensaStatusDTO;
import cv.inps.rh.shared.infrastructure.persistence.repository.AssiduidadeParametroEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DispensaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.util.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DispensaHorasService {

    private final DispensaEntityRepository dispensaRepository;
    private final AssiduidadeParametroEntityRepository assiduidadeParametroEntityRepository;

    public HorasDispensaStatusDTO getHorasStatus(UUID funcionarioUuid, LocalDate dataReferencia) {

      var inicioMes = dataReferencia.withDayOfMonth(1);
      var fimMes =dataReferencia.withDayOfMonth(dataReferencia.lengthOfMonth());

      var listaMes = dispensaRepository.findAllByPedidoId_FunId_UuidAndDataBetween(
          funcionarioUuid, inicioMes, fimMes);
      int totalMin = 0;
      for (var d : listaMes) {
        var minsItem = TimeUtils.diffMinutes(d.getHoraInicio(), d.getHoraFim());
        if (minsItem != null)
          totalMin += minsItem;
      }

      var totalHorasUsadas =  TimeUtils.formatMinutesToHHmm(totalMin);
      String totalHorasDisponiveis = assiduidadeParametroEntityRepository
          .findActiveTDispensa()
          .orElse("00:00");


      var horasDispensaStatus = new HorasDispensaStatusDTO();
      horasDispensaStatus.setHorasUsadas(totalHorasUsadas);
      horasDispensaStatus.setHorasDisponiveis(totalHorasDisponiveis);

      return horasDispensaStatus;
    }
}
