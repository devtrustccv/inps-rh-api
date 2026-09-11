package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AssiduidadeParametroEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface AssiduidadeParametroEntityRepository extends
    JpaRepository<AssiduidadeParametroEntity, Long>,
    JpaSpecificationExecutor<AssiduidadeParametroEntity> {

  default AssiduidadeParametroEntity findByIdOrThrow(Long id) {
    return this.findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "AssiduidadeParametroEntity not found for id: " + id));
  }

  List<AssiduidadeParametroEntity> findAllByEstado(String estado);

  @Query("SELECT a.tDispensa FROM AssiduidadeParametroEntity a WHERE a.estado = 'A' AND a.dtFim IS NULL")
  Optional<String> findActiveTDispensa();

  /**
   * A parametrização de assiduidade em vigor.
   *
   * <p>Mesmo critério do {@link #findActiveTDispensa}: {@code ESTADO='A'} <b>e</b> {@code DT_FIM}
   * nulo. A tabela é historiada — as versões antigas ficam com data de fim —, por isso filtrar só
   * pelo estado devolve lista e obriga a escolher uma à sorte. Era o que os leitores da jornada e
   * das percentagens de hora extra faziam ({@code findAllByEstado('A').getFirst()}, sem
   * ordenação): com duas linhas activas, o valor da falta e as horas trabalhadas da síntese podiam
   * sair de parametrizações diferentes.
   */
  @Query("SELECT a FROM AssiduidadeParametroEntity a WHERE a.estado = 'A' AND a.dtFim IS NULL")
  Optional<AssiduidadeParametroEntity> findActiveParametro();

}
