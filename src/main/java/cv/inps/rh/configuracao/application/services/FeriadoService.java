package cv.inps.rh.configuracao.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.FeriadoDTO;
import cv.inps.rh.configuracao.application.dto.FeriadoListRequestDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FeriadoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriadoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.GeografiaEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;

@Transactional
@Service
@RequiredArgsConstructor
public class FeriadoService {

  private final FeriadoEntityRepository feriadoEntityRepository;
  private final GeografiaEntityRepository geografiaEntityRepository;

  public void save(FeriadoDTO dto) {

    if (dto.getDataEspecifica() != null && dto.getDataEspecifica().getYear() != dto.getAnoReferente())
      throw IgrpResponseStatusException.badRequest("O ano referente deve ser igual ao ano da data do feriado.");

    var existing = feriadoEntityRepository.findAllByAnoReferenteAndEstado(dto.getAnoReferente(), Estado.A);

    String currentUuid = StringUtils.hasText(dto.getIdFeriado()) ? dto.getIdFeriado() : null;

    boolean duplicateExists = existing.stream()
        .filter(f -> currentUuid == null || !f.getUuid().equals(currentUuid))
        .anyMatch(f -> {
          boolean sameRegion =
              (f.getGeogrId() == null && dto.getGeogrId() == null) ||
                  (f.getGeogrId() != null && dto.getGeogrId() != null && f.getGeogrId().getId().equals(dto.getGeogrId()));

          if ("SIM".equalsIgnoreCase(dto.getFixoAno())) {
            Integer d = dto.getDia();
            Integer m = dto.getMes();
            return sameRegion
                && "SIM".equalsIgnoreCase(f.getFixoAno())
                && d != null && m != null
                && f.getDia() != null && f.getMes() != null
                && f.getDia().equals(d)
                && f.getMes().equals(m);
          } else {
            return sameRegion
                && f.getDataEspecifica() != null
                && dto.getDataEspecifica() != null
                && f.getDataEspecifica().equals(dto.getDataEspecifica());
          }
        });
    if (duplicateExists)
      throw IgrpResponseStatusException.badRequest("Não é possível cadastrar feriados duplicados (mesma data e região)");

    final FeriadoEntity holiday;
    if (StringUtils.hasText(dto.getIdFeriado())) {
      holiday = feriadoEntityRepository.findByUuidOrThrow(dto.getIdFeriado());
    } else {
      holiday = new FeriadoEntity();
      holiday.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
      holiday.setEstado(Estado.A);
    }

    holiday.setAnoReferente(dto.getAnoReferente());
    holiday.setDescricao(dto.getDescricao());
    holiday.setDataEspecifica(dto.getDataEspecifica());
    holiday.setTipoFeriado(dto.getTipoFeriado());
    holiday.setFixoAno(dto.getFixoAno());
    holiday.setDia(dto.getDia());
    holiday.setMes(dto.getMes());

    if (dto.getGeogrId() != null) {
      var geo = geografiaEntityRepository.findById(dto.getGeogrId())
          .orElseThrow(() -> IgrpResponseStatusException.badRequest("Geografia com id " + dto.getGeogrId() + " não encontrada."));
      holiday.setGeogrId(geo);
    } else {
      holiday.setGeogrId(null);
    }

    feriadoEntityRepository.save(holiday);
  }

  public void delete(String idFeriado) {
    var entity = feriadoEntityRepository.findByUuidOrThrow(idFeriado);
    feriadoEntityRepository.delete(entity);
  }


  public FeriadoListRequestDTO getFeriados(String anoReferente) {

    if (!anoReferente.matches("\\d+"))
      throw IgrpResponseStatusException.badRequest("O ano referente deve conter apenas números.");

    var year = Integer.valueOf(anoReferente);

    var data = feriadoEntityRepository.findAllByAnoReferenteAndEstado(year, Estado.A);

    var response = new ArrayList<FeriadoDTO>();

    for (var obj : data) {
      var dto = new FeriadoDTO();
      dto.setIdFeriado(obj.getUuid());
      dto.setDescricao(obj.getDescricao());
      dto.setDataEspecifica(obj.getDataEspecifica());

      dto.setAnoReferente(obj.getAnoReferente());
      dto.setTipoFeriado(obj.getTipoFeriado());
      dto.setFixoAno(obj.getFixoAno());
      dto.setDia(obj.getDia());
      dto.setMes(obj.getMes());
      dto.setEstado(obj.getEstado().name());

      if (obj.getGeogrId() != null) {
          dto.setGeogrId(obj.getGeogrId().getId());
      }

      response.add(dto);
    }

    return new FeriadoListRequestDTO(year, response);
  }
}
