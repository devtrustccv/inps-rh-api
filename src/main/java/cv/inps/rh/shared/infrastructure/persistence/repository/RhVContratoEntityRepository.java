package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.infrastructure.persistence.entity.RhVContratoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RhVContratoEntityRepository extends
    JpaRepository<RhVContratoEntity, Long>,
    JpaSpecificationExecutor<RhVContratoEntity>
{

  /** Linha da vista para uma VERSÃO do contrato (por uuid + versão). Fonte da metadata da versão na
   *  getById: TIPO_SITUACAO (INICIO/RENOVACAO), EST_ACT_ADM (1=atual) e datas por versão. */
  Optional<RhVContratoEntity> findByContratoUuidAndVersao(UUID contratoUuid, Integer versao);

}
