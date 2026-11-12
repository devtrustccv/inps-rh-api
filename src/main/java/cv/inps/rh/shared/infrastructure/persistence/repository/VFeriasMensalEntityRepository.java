package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.VFeriasMensalEntity;
import cv.inps.rh.shared.infrastructure.persistence.projections.FeriasResumoAnoProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.UUID;


@Repository
public interface VFeriasMensalEntityRepository extends
    JpaRepository<VFeriasMensalEntity, Long>,
    JpaSpecificationExecutor<VFeriasMensalEntity>
{

      default VFeriasMensalEntity findByIdOrThrow(Long id) {
          return this.findById(id)
          .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,"VFeriasMensalEntity not found for id: " + id));
      }

      /**
       * Lista de Férias agrupada por funcionário e ano. A view tem uma linha por registo de direito
       * (RH_T_FERIAS) e o mesmo id para todos os registos do funcionário no ano, pelo que lida como
       * entidade repetia a mesma linha. Aqui soma-se o direito do ano; os restantes totais já são
       * por funcionário/ano na view (o direito acumulado inclui todos os registos do ano).
       */
      @Query(value = "SELECT v.uuidFuncionario AS uuidFuncionario, v.ano AS ano,"
          + " MAX(v.nomeColaborador) AS nomeColaborador, MAX(v.direcao) AS direcao, MAX(v.secao) AS secao,"
          + " MAX(v.vinculo) AS vinculo, MAX(v.categoria) AS categoria,"
          + " MAX(v.totalDireito) AS totalDireito, SUM(v.totalDireitoAno) AS totalDireitoAno,"
          + " MAX(v.totalPlaneado) AS totalPlaneado, MAX(v.totalGozado) AS totalGozado"
          + " FROM VFeriasMensalEntity v" + FILTRO_LISTA
          + " GROUP BY v.uuidFuncionario, v.ano"
          + " ORDER BY v.ano DESC, MAX(v.nomeColaborador)",
          countQuery = "SELECT COUNT(DISTINCT v.id) FROM VFeriasMensalEntity v" + FILTRO_LISTA)
      Page<FeriasResumoAnoProjection> findResumoPorAno(
          @Param("ano") Integer ano,
          @Param("direcaoId") Long direcaoId,
          @Param("secaoId") Long secaoId,
          @Param("nome") String nome,
          @Param("funcionario") UUID funcionario,
          Pageable pageable);

      String FILTRO_LISTA = " WHERE (:ano IS NULL OR v.ano = :ano)"
          + " AND (:direcaoId IS NULL OR v.direcaoId = :direcaoId)"
          + " AND (:secaoId IS NULL OR v.secaoId = :secaoId)"
          + " AND (:nome IS NULL OR LOWER(v.nomeColaborador) LIKE :nome)"
          + " AND (:funcionario IS NULL OR v.uuidFuncionario = :funcionario)";

}
