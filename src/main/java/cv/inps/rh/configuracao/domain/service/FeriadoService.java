package cv.inps.rh.configuracao.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.FeriadoDTO;
import cv.inps.rh.configuracao.application.dto.FeriadoListRequestDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FeriadoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriadoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class FeriadoService {

  private final FeriadoEntityRepository feriadoEntityRepository;

  public void save(FeriadoListRequestDTO dto) {

    var ano = dto.getAnoReferente();

    var distinctDates = dto.getFeriados().stream()
        .map(FeriadoDTO::getDataFeriado)
        .distinct()
        .toList();
    if (distinctDates.size() != dto.getFeriados().size())
      throw IgrpResponseStatusException.badRequest("Não é possível cadastrar feriados com datas iguais");

    var existing = feriadoEntityRepository.findAllByAnoReferenteAndEstado(ano, Estado.A);

    var existingMap = existing.stream().collect(Collectors.toMap(FeriadoEntity::getUuid, f -> f));

    var toSave = new ArrayList<FeriadoEntity>();

    var receivedUuids = new HashSet<String>();

    for (var obj : dto.getFeriados()) {

      if (obj.getDataFeriado().getYear() != dto.getAnoReferente())
        throw IgrpResponseStatusException.badRequest("O ano referente deve ser igual ao ano da data do feriado.");

      final FeriadoEntity holiday;
      if (StringUtils.hasText(obj.getIdFeriado())) {
        receivedUuids.add(obj.getIdFeriado());
        holiday = existingMap.get(obj.getIdFeriado());
        if (holiday == null)
          throw IgrpResponseStatusException.badRequest("Feriado com id " + obj.getIdFeriado() + " não encontrado.");
      } else {
        holiday = new FeriadoEntity();
        holiday.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
        holiday.setEstado(Estado.A);
      }

      holiday.setAnoReferente(dto.getAnoReferente());
      holiday.setDescricao(obj.getDescricao());
      holiday.setData(obj.getDataFeriado());
      toSave.add(holiday);
    }

    var toDelete = existing.stream()
        .filter(f -> !receivedUuids.contains(f.getUuid()))
        .toList();
    if (!toDelete.isEmpty())
      feriadoEntityRepository.deleteAll(toDelete);

    feriadoEntityRepository.saveAll(toSave);
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
      dto.setDataFeriado(obj.getData());
      response.add(dto);
    }

    return new FeriadoListRequestDTO(year, response);
  }
}

