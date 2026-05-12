package cv.inps.rh.processamento.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.processamento.application.dto.SubsidioResponseNatalDTO;
import cv.inps.rh.processamento.domain.models.SubsidioNatalStatus;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.SubsidioNatalEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SubsidioNatalEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Types;
import java.util.UUID;

@AllArgsConstructor
@Service
public class SubsidioNatalService {

  private final DataSource dataSource;
  private final SubsidioNatalEntityRepository subsidioNatalEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;

  public void activateInactivate(Long subsidioId, Long ano, String funId, SubsidioNatalStatus status, SubsidioResponseNatalDTO data) {
    switch (status) {
      case ATIVAR -> {

        var uuid = UuidCreator.getTimeOrderedEpoch();

        var fun = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funId));

        var subs = new SubsidioNatalEntity();
        subs.setFun(fun);
        subs.setAnoReferente(ano);
        subs.setValorSalarioBase(safeValueOf(data.salario()));
        subs.setMesTrab(data.mesesTrabalho());
        subs.setPercSalario(safeValueOf(data.percSalario()));
        subs.setFaltas(safeValueOf(data.faltas()));
        subs.setPercFalta(safeValueOf(data.percFalta()));
        subs.setValorSubsidio(safeValueOf(data.valorSubsidio()));
        subs.setChequeBrinde(safeValueOf(data.valorChequeBrinde()));
        subs.setPrendaNatal(safeValueOf(data.valorPrendaNatal()));
        subs.setReferenciaId(1L); // TODO 12/05/2026 22:03 fix this later;
        subs.setEstado(Estado.P.name());
        subs.setUuid(uuid.toString());
        var saved = subsidioNatalEntityRepository.save(subs);

        var validation = new ValidacaoEntity();
        validation.setTipoAccao("SUBSIDIO_NATAL");
        validation.setReferenciaName("RH_T_SUBSIDIO_NATAL");
        validation.setReferenciaId(saved.getId());
        validation.setFunId(fun);
        validation.setTiprelId(funcionarioRules.getTipoRelacionamentoAtual(fun.getUuid()));
        validation.setEstado(Estado.A);
        validation.setUuid(uuid);
        validacaoEntityRepository.save(validation);
      }
      case INATIVAR -> {
        var obj = subsidioNatalEntityRepository.findByIdOrThrow(subsidioId);
        obj.setEstado(status.getCode());
        subsidioNatalEntityRepository.save(obj);
      }
    }
  }

  private Long safeValueOf(String val) {
    try {
      return Long.parseLong(val);
    } catch (Exception _) {
      return null;
    }
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
