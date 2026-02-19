package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.shared.infrastructure.persistence.entity.FeriasEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AnoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriasEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriasGozadasEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SaldoFeriaService {

  private final FeriasEntityRepository feriasEntityRepository;
  private final FeriasGozadasEntityRepository feriasGozadasEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final AnoEntityRepository anoEntityRepository;


  public int getSaldo(UUID funcionarioId, Integer ano){

    final var funcionario = funcionarioEntityRepository
        .findByUuid(funcionarioId)
        .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

    int saldo;
    if (ano != null) {
      final var anoEntity = anoEntityRepository.findByAno(String.valueOf(ano))
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

    return saldo;

  }
}
