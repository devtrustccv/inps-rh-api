package cv.inps.rh.assiduidade.application.queries;

import cv.inps.rh.shared.infrastructure.persistence.entity.FeriasEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AnoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriasEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriasGozadasEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import cv.igrp.framework.core.domain.QueryHandler;
import cv.igrp.framework.stereotype.IgrpQueryHandler;
import org.springframework.context.event.EventListener;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
public class GetFuncioarioSaldoFeriasQueryHandler
    implements QueryHandler<GetFuncioarioSaldoFeriasQuery, ResponseEntity<Map<String, ?>>> {

  private static final Logger LOGGER = LoggerFactory.getLogger(GetFuncioarioSaldoFeriasQueryHandler.class);

  private final FeriasEntityRepository feriasEntityRepository;
  private final FeriasGozadasEntityRepository feriasGozadasEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final AnoEntityRepository anoEntityRepository;

  public GetFuncioarioSaldoFeriasQueryHandler(FeriasEntityRepository feriasEntityRepository, FeriasGozadasEntityRepository feriasGozadasEntityRepository,
                                              FuncionarioEntityRepository funcionarioEntityRepository,
                                              AnoEntityRepository anoEntityRepository) {
    this.feriasEntityRepository = feriasEntityRepository;
    this.feriasGozadasEntityRepository = feriasGozadasEntityRepository;
    this.funcionarioEntityRepository = funcionarioEntityRepository;
    this.anoEntityRepository = anoEntityRepository;
  }

  @IgrpQueryHandler
  public ResponseEntity<Map<String, ?>> handle(GetFuncioarioSaldoFeriasQuery query) {

    LOGGER.debug("GetFuncioarioSaldoFeriasQuery: {}", query);

    final var funcionario = funcionarioEntityRepository
        .findByUuid(UUID.fromString(query.getFuncionarioId()))
        .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

    int saldo;
    if (query.getAno() != null) {
      final var anoEntity = anoEntityRepository.findByAno(String.valueOf(query.getAno()))
          .orElseThrow(() -> new RuntimeException("Ano não encontrado"));

      // Calcula o saldo para o ano específico
      final var direitoAnual = feriasEntityRepository
          .findByFunId_UuidAndAnoId(funcionario.getUuid(), anoEntity)
          .map(FeriasEntity::getNumDia)
          .orElse(0);

      final var gozadoAnual = feriasGozadasEntityRepository.sumNumDiaByFuncionarioIdAndAno(funcionario.getUuid(),
          anoEntity.getId());

      saldo = direitoAnual - gozadoAnual;
    } else {
      // Calcula o saldo total acumulado
      final var direitoTotal = feriasEntityRepository.sumNumDiaByFuncionarioId(funcionario.getUuid());
      final var gozadoTotal = feriasGozadasEntityRepository.sumNumDiaByFuncionarioId(funcionario.getUuid());

      saldo = direitoTotal - gozadoTotal;
    }

    final Map<String, Object> response = Map.of(
        "funcionarioUuid", query.getFuncionarioId(),
        "anoReferencia", query.getAno(),
        "saldo", saldo);

    return ResponseEntity.ok(response);
  }

}
