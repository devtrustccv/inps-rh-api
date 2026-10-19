package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.OrdemServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface OrdemServicoEntityRepository extends
    JpaRepository<OrdemServicoEntity, Long>,
    JpaSpecificationExecutor<OrdemServicoEntity>
{

      default OrdemServicoEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"OrdemServicoEntity not found for id: " + id));
      }

      List<OrdemServicoEntity> findByFunId_Uuid(UUID funcionarioUuid);

      Optional<OrdemServicoEntity> findByUuid(UUID uuid);

      @org.springframework.data.jpa.repository.Query("SELECT o.nuOrdem FROM OrdemServicoEntity o WHERE o.createdDate >= :inicio AND o.createdDate < :fim")
      List<String> findNumerosNoPeriodo(@org.springframework.data.repository.query.Param("inicio") java.time.LocalDateTime inicio,
                                        @org.springframework.data.repository.query.Param("fim") java.time.LocalDateTime fim);

      /** Próximo nº de ordem de serviço do ano corrente (sequencial, reinicia a 1 em cada ano). */
      default String proximoNumero() {
        var ano = java.time.Year.now();
        var max = findNumerosNoPeriodo(ano.atDay(1).atStartOfDay(), ano.plusYears(1).atDay(1).atStartOfDay()).stream()
            .filter(n -> n != null && !n.isBlank() && n.trim().chars().allMatch(Character::isDigit))
            .mapToInt(n -> Integer.parseInt(n.trim()))
            .max().orElse(0);
        return String.valueOf(max + 1);
      }

      default OrdemServicoEntity findByUuidOrThrow(UUID uuid) {
          return this.findByUuid(uuid)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "OrdemServicoEntity not found for uuid: " + uuid));
      }

}
