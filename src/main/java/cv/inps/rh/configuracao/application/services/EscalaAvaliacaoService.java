package cv.inps.rh.configuracao.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.commands.CreateEscalaAvaliacaoCommand;
import cv.inps.rh.configuracao.infrastructure.mappers.EscalaAvaliacaoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamEscalaAvaliacaoEntityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.http.ResponseEntity;

import java.util.ArrayList;
import java.util.Map;

@Service
public class EscalaAvaliacaoService {

  private final ParamEscalaAvaliacaoEntityRepository repository;
  private final EscalaAvaliacaoMapper mapper;

  public EscalaAvaliacaoService(
      ParamEscalaAvaliacaoEntityRepository repository,
      EscalaAvaliacaoMapper mapper) {
    this.repository = repository;
    this.mapper = mapper;
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> registar(CreateEscalaAvaliacaoCommand command) {

    var request = command.getEscalaavaliacaorequest();
    var rows = request != null ? request.getRow() : null;

    if (CollectionUtils.isEmpty(rows)) {
      throw IgrpResponseStatusException.badRequest("Lista de escala de avaliação não pode estar vazia");
    }

    var uuids = new ArrayList<String>(rows.size());

    for (var row : rows) {
      if (row.getQuantitativaDe() != null
          && row.getQuantitativaAte() != null
          && row.getQuantitativaDe().compareTo(row.getQuantitativaAte()) > 0) {
        throw IgrpResponseStatusException.badRequest("quantitativaDe não pode ser maior que quantitativaAte");
      }

      var entity = mapper.toEntity(row);
      if (entity.getUuid() == null) {
        entity.setUuid(UuidCreator.getTimeOrderedEpoch());
      }
      entity.setEstado(Estado.A);

      repository.save(entity);
      uuids.add(entity.getUuid().toString());
    }

    return ResponseEntity.ok(Map.of(
        "id", uuids.get(0),
        "ids", uuids));
  }
}
