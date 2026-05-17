package cv.inps.rh.processamento.domain.service.processamentosalarial;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.processamento.application.dto.AumentoSalarialRequestDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.AumentoSalarialEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AumentoSalarialEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamPccsEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AumentoSalarialWriteService {

  private final AumentoSalarialEntityRepository aumentoSalarialEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final ParamPccsEntityRepository paramPccsEntityRepository;
  private final DataSource dataSource;

  public void saveAumentoSalarial(AumentoSalarialRequestDTO request) {

    var pccs = paramPccsEntityRepository.findFirstByEstadoOrderByDataInicioDesc(Estado.A).orElseThrow();

    var uuid = UuidCreator.getTimeOrderedEpoch();

    var salaryIncrease = new AumentoSalarialEntity();
    salaryIncrease.setDescricao(request.getDesignacao());
    salaryIncrease.setMotivo(request.getMotivo());
    salaryIncrease.setDataReferente(request.getDataReferente());
    salaryIncrease.setPercentagem(request.getPercentagem());
    salaryIncrease.setEstado(Estado.P.name());
    salaryIncrease.setUuid(uuid.toString());
    salaryIncrease.setPccs(pccs);
    var saved = aumentoSalarialEntityRepository.save(salaryIncrease);

    var validation = new ValidacaoEntity();
    validation.setTipoAccao("INSERT");
    validation.setReferenciaName("AUMENTO_SALARIAL");
    validation.setReferenciaId(saved.getId());
    validation.setReferenciaUuid(uuid);
    validation.setEstado(Estado.P);
    validation.setUuid(UuidCreator.getTimeOrderedEpoch());
    validacaoEntityRepository.save(validation);
  }

  public void validar(String salaryIncreaseId) {

    var salaryIncrease = aumentoSalarialEntityRepository.findByUuid(salaryIncreaseId).orElseThrow();

    var call = new SimpleJdbcCall(dataSource)
        .withoutProcedureColumnMetaDataAccess()
        .withCatalogName("PKG_AUMENTO_SALARIAL")
        .withProcedureName("SIMULAR_AUMENTO")
        .declareParameters(
            new SqlParameter("P_AUMENTO_SAL_ID", Types.VARCHAR),
            new SqlParameter("P_MOTIVO", Types.VARCHAR),
            new SqlParameter("P_DATA_REFERENTE", Types.DATE),
            new SqlParameter("PERCENTAGEM", Types.NUMERIC),
            new SqlParameter("P_USER_ID", Types.NUMERIC),
            new SqlParameter("P_USER_NAME", Types.VARCHAR)
        );

    call.execute(Map.of(
        "P_AUMENTO_SAL_ID", salaryIncrease.getId(),
        "P_MOTIVO", salaryIncrease.getMotivo(),
        "P_DATA_REFERENTE", java.sql.Date.valueOf(salaryIncrease.getDataReferente()),
        "PERCENTAGEM", salaryIncrease.getPercentagem(),
        "P_USER_ID", "0", // TODO 17/05/2026 11:14 current user
        "P_USER_NAME", "demo" // TODO 17/05/2026 11:14 current user name
    ));
  }

}
