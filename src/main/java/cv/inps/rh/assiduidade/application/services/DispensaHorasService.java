package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.HorasDispensaStatusDTO;
import cv.inps.rh.shared.infrastructure.persistence.repository.AssiduidadeParametroEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DispensaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DispensaHorasService {

    private final DispensaEntityRepository dispensaRepository;
    private final AssiduidadeParametroEntityRepository parametroRepository;
    private final FuncionarioEntityRepository funcionarioRepository;

    public HorasDispensaStatusDTO getHorasStatus(UUID funcionarioUuid, LocalDate dataReferencia) {

      var inicioMes = dataReferencia.withDayOfMonth(1);
      var fimMes =dataReferencia.withDayOfMonth(dataReferencia.lengthOfMonth());

        return new HorasDispensaStatusDTO();
    }
}
