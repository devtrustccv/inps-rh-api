package cv.inps.rh.processamento.domain.service;

import cv.inps.rh.processamento.application.dto.SubsidioResponseNatalDTO;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Types;

@Service
public class SubsidioNatalService {

  private final DataSource dataSource;

  public SubsidioNatalService(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  public SubsidioResponseNatalDTO getData(Long direcaoId, Long funId, Double valorCBrinde, Long anoProcessamento) {

    var result = new SimpleJdbcCall(dataSource)
        .withoutProcedureColumnMetaDataAccess()
        .withCatalogName("RH_PK_SUBSISIO_NATAL_F_DB")
        .withProcedureName("load_list")
        .declareParameters(

            new SqlParameter("P_DIRECAO_ID", Types.NUMERIC),
            new SqlParameter("P_FUN_ID", Types.NUMERIC),
            new SqlParameter("p_VALOR_C_BRINDE", Types.NUMERIC),
            new SqlParameter("p_ano_processamento", Types.NUMERIC),

            new SqlOutParameter("p_ls_nome", Types.VARCHAR),
            new SqlOutParameter("p_ls_salario", Types.VARCHAR),
            new SqlOutParameter("p_ls_meses_trabalho", Types.VARCHAR),
            new SqlOutParameter("p_ls_perc_salario", Types.VARCHAR),
            new SqlOutParameter("p_ls_faltas", Types.VARCHAR),
            new SqlOutParameter("p_ls_perc_falta", Types.VARCHAR),
            new SqlOutParameter("p_ls_valor_subsidio", Types.VARCHAR),
            new SqlOutParameter("p_ls_valor_cheque_brid", Types.VARCHAR),
            new SqlOutParameter("p_ls_valor_prenda_natal", Types.VARCHAR),
            new SqlOutParameter("P_ESTADO", Types.VARCHAR)
        )
        .execute(
            new MapSqlParameterSource()
                .addValue("P_DIRECAO_ID", direcaoId)
                .addValue("P_FUN_ID", funId)
                .addValue("p_VALOR_C_BRINDE", valorCBrinde)
                .addValue("p_ano_processamento", anoProcessamento)
        );

    return new SubsidioResponseNatalDTO(
        str(result.get("p_ls_nome")),
        str(result.get("p_ls_salario")),
        str(result.get("p_ls_meses_trabalho")),
        str(result.get("p_ls_perc_salario")),
        str(result.get("p_ls_faltas")),
        str(result.get("p_ls_perc_falta")),
        str(result.get("p_ls_valor_subsidio")),
        str(result.get("p_ls_valor_cheque_brid")),
        str(result.get("p_ls_valor_prenda_natal")),
        str(result.get("P_ESTADO"))
    );
  }

  private String str(Object value) {
    return value != null ? value.toString() : null;
  }
}
