package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.processamento.application.dto.AumentoSalarialRequestDTO;
import cv.inps.rh.shared.config.ApplicationAuditorAware;
import cv.inps.rh.shared.infrastructure.persistence.repository.AumentoSalarialEntityRepository;
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
  private final DataSource dataSource;
  private final ApplicationAuditorAware applicationAuditorAware;

  public void saveAumentoSalarial(AumentoSalarialRequestDTO request) {

    var call = new SimpleJdbcCall(dataSource)
        .withoutProcedureColumnMetaDataAccess()
        .withCatalogName("RH_PK_AUMENTO_SALARIAL_DB")
        .withProcedureName("SIMULAR_AUMENTO")
        .declareParameters(
            new SqlParameter("P_DESCRICAO", Types.VARCHAR),
            new SqlParameter("P_MOTIVO", Types.VARCHAR),
            new SqlParameter("P_DATA_REFERENTE", Types.DATE),
            new SqlParameter("PERCENTAGEM", Types.NUMERIC),
            new SqlParameter("P_USER_ID", Types.NUMERIC),
            new SqlParameter("P_USER_NAME", Types.VARCHAR)
        );

    call.execute(Map.of(
        "P_DESCRICAO", request.getDesignacao(),
        "P_MOTIVO", request.getMotivo(),
        "P_DATA_REFERENTE", java.sql.Date.valueOf(request.getDataReferente()),
        "PERCENTAGEM", request.getPercentagem(),
        "P_USER_ID", "0", // TODO 17/05/2026 11:14 current user
        "P_USER_NAME", applicationAuditorAware.getCurrentSubjectName()
    ));
  }

  public void updateAumentoSalarial(String salaryIncreaseId, AumentoSalarialRequestDTO request) {

    var salaryIncrease = aumentoSalarialEntityRepository.findByUuid(salaryIncreaseId).orElseThrow();
    salaryIncrease.setDescricao(request.getDesignacao());
    salaryIncrease.setMotivo(request.getMotivo());
    salaryIncrease.setDataReferente(request.getDataReferente());
    salaryIncrease.setPercentagem(request.getPercentagem());
    aumentoSalarialEntityRepository.save(salaryIncrease);
  }

  public void validar(String salaryIncreaseId, String estadoValidacao) {

    var salaryIncrease = aumentoSalarialEntityRepository.findByUuid(salaryIncreaseId).orElseThrow();

    var call = new SimpleJdbcCall(dataSource)
        .withoutProcedureColumnMetaDataAccess()
        .withCatalogName("RH_PK_AUMENTO_SALARIAL_DB")
        .withProcedureName("REGISTAR_AUMENTO")
        .declareParameters(
            new SqlParameter("P_ESTADO_VALIDACAO", Types.VARCHAR),
            new SqlParameter("P_ID_AUMENTO", Types.VARCHAR),
            new SqlParameter("P_USER_ID", Types.NUMERIC),
            new SqlParameter("P_USER_NAME", Types.VARCHAR)
        );

    call.execute(Map.of(
        "P_ESTADO_VALIDACAO", estadoValidacao,
        "P_ID_AUMENTO", salaryIncrease.getId(),
        "P_USER_ID", "0", // TODO 17/05/2026 11:14 current user
        "P_USER_NAME", applicationAuditorAware.getCurrentSubjectName()
    ));
  }

}
