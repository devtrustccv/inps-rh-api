package cv.inps.rh.processamento.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.processamento.application.dto.SubsidioFeriasDetalheDTO;
import cv.inps.rh.processamento.application.dto.SubsidioFeriasResponseDTO;
import cv.inps.rh.processamento.application.dto.SubsidioResponseNatalDTO;
import cv.inps.rh.processamento.domain.models.SubsidioNatalStatus;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.SubsidioNatalEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SubsidioNatalEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.AllArgsConstructor;
import oracle.jdbc.OracleTypes;
import oracle.jdbc.internal.OracleCallableStatement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.Date;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class SubsidioService {

  private final JdbcTemplate jdbcTemplate;
  private final SubsidioNatalEntityRepository subsidioNatalEntityRepository;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioRules funcionarioRules;

  public void activateInactivateSubsidioNatal(Long subsidioId, Long ano, String funId, SubsidioNatalStatus status, SubsidioResponseNatalDTO data) {
    switch (status) {
      case ATIVAR -> {

        final SubsidioNatalEntity subs;

        if (subsidioId != null) {
          subs = subsidioNatalEntityRepository.findByIdOrThrow(subsidioId);
        } else {
          var fun = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(funId));
          subs = new SubsidioNatalEntity();
          subs.setFun(fun);
          subs.setEstado(Estado.P.name());
          subs.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
          subs.setReferenciaId(fun.getId());
        }
        subs.setAnoReferente(ano);
        subs.setValorSalarioBase(safeValueOf(data.salario()));
        subs.setMesTrab(data.mesesTrabalho());
        subs.setPercSalario(safeValueOf(data.percSalario()));
        subs.setFaltas(safeValueOf(data.faltas()));
        subs.setPercFalta(safeValueOf(data.percFalta()));
        subs.setValorSubsidio(safeValueOf(data.valorSubsidio()));
        subs.setChequeBrinde(safeValueOf(data.valorChequeBrinde()));
        subs.setPrendaNatal(safeValueOf(data.valorPrendaNatal()));

        var saved = subsidioNatalEntityRepository.save(subs);

        if (subsidioId == null) {
          var fun = subs.getFun();
          var validation = new ValidacaoEntity();
          validation.setTipoAccao("SUBSIDIO_NATAL");
          validation.setReferenciaName("RH_T_SUBSIDIO_NATAL");
          validation.setReferenciaId(saved.getId());
          validation.setFunId(fun);
          validation.setTiprelId(funcionarioRules.getTipoRelacionamentoAtual(fun.getUuid()));
          validation.setEstado(Estado.A);
          validation.setUuid(UuidCreator.getTimeOrderedEpoch());
          validacaoEntityRepository.save(validation);
        }
      }
      case INATIVAR -> subsidioNatalEntityRepository.updateEstadoById(subsidioId, status.getCode());
    }
  }

  private Long safeValueOf(String val) {
    try {
      return Long.parseLong(val);
    } catch (Exception _) {
      return null;
    }
  }

  public List<SubsidioResponseNatalDTO> getDataSubsidioNatal(Long direcaoId, Long funId, Double valorCBrinde, Long anoProcessamento) {

    var list = new ArrayList<SubsidioResponseNatalDTO>();

    var sql = """
        BEGIN
            RH_PK_SUBSISIO_NATAL_F_DB.LOAD_LIST(
                ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?
            );
        END;
        """;

    jdbcTemplate.execute((Connection con) -> {

      var cs = con.prepareCall(sql).unwrap(OracleCallableStatement.class);

      cs.setObject(1, direcaoId);
      cs.setObject(2, funId);
      cs.setObject(3, valorCBrinde);
      cs.setObject(4, anoProcessamento);

      cs.registerIndexTableOutParameter(5, 1000, Types.VARCHAR, 4000);
      cs.registerIndexTableOutParameter(6, 1000, Types.VARCHAR, 4000);
      cs.registerIndexTableOutParameter(7, 1000, Types.VARCHAR, 4000);
      cs.registerIndexTableOutParameter(8, 1000, Types.VARCHAR, 4000);
      cs.registerIndexTableOutParameter(9, 1000, Types.VARCHAR, 4000);
      cs.registerIndexTableOutParameter(10, 1000, Types.VARCHAR, 4000);
      cs.registerIndexTableOutParameter(11, 1000, Types.VARCHAR, 4000);
      cs.registerIndexTableOutParameter(12, 1000, Types.VARCHAR, 4000);
      cs.registerIndexTableOutParameter(13, 1000, Types.VARCHAR, 4000);
      cs.registerIndexTableOutParameter(14, 1000, Types.VARCHAR, 4000);
      cs.registerIndexTableOutParameter(15, 1000, Types.VARCHAR, 4000);

      cs.execute();

      var nomes = (String[]) cs.getPlsqlIndexTable(5);
      var salarios = (String[]) cs.getPlsqlIndexTable(6);
      var meses = (String[]) cs.getPlsqlIndexTable(7);
      var percSalario = (String[]) cs.getPlsqlIndexTable(8);
      var faltas = (String[]) cs.getPlsqlIndexTable(9);
      var percFalta = (String[]) cs.getPlsqlIndexTable(10);
      var subsidios = (String[]) cs.getPlsqlIndexTable(11);
      var brindes = (String[]) cs.getPlsqlIndexTable(12);
      var prendas = (String[]) cs.getPlsqlIndexTable(13);
      var estados = (String[]) cs.getPlsqlIndexTable(14);
      var ids = (String[]) cs.getPlsqlIndexTable(15);

      if (nomes != null) {
        for (int i = 0; i < nomes.length; i++) {
          var responseRow = new SubsidioResponseNatalDTO(
              nomes[i],
              salarios[i],
              meses[i],
              percSalario[i],
              faltas[i],
              percFalta[i],
              subsidios[i],
              brindes[i],
              prendas[i],
              estados[i],
              ids[i]
          );
          list.add(responseRow);
        }
      }

      return null;
    });

    return list;
  }

  /**
   * Converte o Object[] devolvido por getPlsqlIndexTable para String[].
   * Evita ClassCastException quando o Oracle devolve Object[].
   */
  private static String[] toStringArray(Object raw) {
    if (raw == null) return new String[0];
    Object[] objects = (Object[]) raw;
    String[] result = new String[objects.length];
    for (int i = 0; i < objects.length; i++) {
      result[i] = objects[i] != null ? objects[i].toString() : null;
    }
    return result;
  }

  /**
   * Acesso seguro a um array — retorna null se o índice estiver fora dos limites.
   */
  private static String safeGet(String[] arr, int index) {
    if (arr == null || index >= arr.length) return null;
    return arr[index];
  }

  /**
   * Chama a procedure RH_PK_SUBSISIO_NATAL_F_DB.SAL_BASE_DET e retorna
   * a lista de funcionários com os respectivos detalhes de escalão.
   * <p>
   * Mapeamento de parâmetros (31 no total):
   * <p>
   * --- IN ---
   * 1: P_data_proc
   * 2: p_ano_referente
   * 3: p_direcao
   * 4: P_FUN_ID
   * <p>
   * --- OUT PAI (owa.vc_arr) ---
   * 5: P_FUN_NOME
   * 6: P_FUN_ID_PAI
   * 7: P_ANO_REFERENTE2
   * 8: P_ESTADO
   * 9: p_valor_subsidio
   * 10: p_dias_total
   * 11: p_mes_total
   * 12: p_desc_sal_base
   * 13: p_valor_sal_base
   * 14: p_desc_subsidio
   * 15: P_total_remun
   * 16: P_DIAS_FERIA
   * 17: p_id
   * <p>
   * --- OUT FILHO (owa.vc_arr) ---
   * 18: P_NUM
   * 19: p_data_inicio
   * 20: p_data_fim
   * 21: p_escalao
   * 22: p_ESCALAO_DESC
   * 23: p_valor_escalao
   * 24: p_meses
   * 25: p_dias
   * 26: p_meses_valor
   * 27: p_dias_valor
   * 28: p_sum_dias_meses
   * 29: P_FUN_ID_FILHO
   */
  public List<SubsidioFeriasResponseDTO> getSubsidioFeriasData(
      Date dataProcessamento,
      Integer anoReferente,
      Long direcaoId,
      Long funId) {

    var list = new ArrayList<SubsidioFeriasResponseDTO>();

    var sql = """
        BEGIN
            RH_PK_SUBSISIO_NATAL_F_DB.SAL_BASE_DET(
                ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?
            );
        END;
        """;

    jdbcTemplate.execute((java.sql.Connection con) -> {

      var cs = con.prepareCall(sql).unwrap(OracleCallableStatement.class);

      // ── Parâmetros IN ──────────────────────────────────────────────
      cs.setDate(1, dataProcessamento);
      cs.setObject(2, anoReferente);
      cs.setObject(3, direcaoId);
      cs.setObject(4, funId);

      // ── Parâmetros OUT PAI ─────────────────────────────────────────
      cs.registerIndexTableOutParameter(5, 1000, OracleTypes.VARCHAR, 4000); // P_FUN_NOME
      cs.registerIndexTableOutParameter(6, 1000, OracleTypes.VARCHAR, 4000); // P_FUN_ID_PAI
      cs.registerIndexTableOutParameter(7, 1000, OracleTypes.VARCHAR, 4000); // P_ANO_REFERENTE2
      cs.registerIndexTableOutParameter(8, 1000, OracleTypes.VARCHAR, 4000); // P_ESTADO
      cs.registerIndexTableOutParameter(9, 1000, OracleTypes.VARCHAR, 4000); // p_valor_subsidio
      cs.registerIndexTableOutParameter(10, 1000, OracleTypes.VARCHAR, 4000); // p_dias_total
      cs.registerIndexTableOutParameter(11, 1000, OracleTypes.VARCHAR, 4000); // p_mes_total
      cs.registerIndexTableOutParameter(12, 1000, OracleTypes.VARCHAR, 4000); // p_desc_sal_base
      cs.registerIndexTableOutParameter(13, 1000, OracleTypes.VARCHAR, 4000); // p_valor_sal_base
      cs.registerIndexTableOutParameter(14, 1000, OracleTypes.VARCHAR, 4000); // p_desc_subsidio
      cs.registerIndexTableOutParameter(15, 1000, OracleTypes.VARCHAR, 4000); // P_total_remun
      cs.registerIndexTableOutParameter(16, 1000, OracleTypes.VARCHAR, 4000); // P_DIAS_FERIA
      cs.registerIndexTableOutParameter(17, 1000, OracleTypes.VARCHAR, 4000); // p_id

      // ── Parâmetros OUT FILHO ───────────────────────────────────────
      cs.registerIndexTableOutParameter(18, 1000, OracleTypes.VARCHAR, 4000); // P_NUM
      cs.registerIndexTableOutParameter(19, 1000, OracleTypes.VARCHAR, 4000); // p_data_inicio
      cs.registerIndexTableOutParameter(20, 1000, OracleTypes.VARCHAR, 4000); // p_data_fim
      cs.registerIndexTableOutParameter(21, 1000, OracleTypes.VARCHAR, 4000); // p_escalao
      cs.registerIndexTableOutParameter(22, 1000, OracleTypes.VARCHAR, 4000); // p_ESCALAO_DESC
      cs.registerIndexTableOutParameter(23, 1000, OracleTypes.VARCHAR, 4000); // p_valor_escalao
      cs.registerIndexTableOutParameter(24, 1000, OracleTypes.VARCHAR, 4000); // p_meses
      cs.registerIndexTableOutParameter(25, 1000, OracleTypes.VARCHAR, 4000); // p_dias
      cs.registerIndexTableOutParameter(26, 1000, OracleTypes.VARCHAR, 4000); // p_meses_valor
      cs.registerIndexTableOutParameter(27, 1000, OracleTypes.VARCHAR, 4000); // p_dias_valor
      cs.registerIndexTableOutParameter(28, 1000, OracleTypes.VARCHAR, 4000); // p_sum_dias_meses
      cs.registerIndexTableOutParameter(29, 1000, OracleTypes.VARCHAR, 4000); // P_FUN_ID_FILHO

      cs.execute();

      // ── Leitura dos arrays PAI ─────────────────────────────────────
      String[] funNome = toStringArray(cs.getPlsqlIndexTable(5));
      String[] funIdPai = toStringArray(cs.getPlsqlIndexTable(6));
      String[] anoRef = toStringArray(cs.getPlsqlIndexTable(7));
      String[] estado = toStringArray(cs.getPlsqlIndexTable(8));
      String[] valorSubs = toStringArray(cs.getPlsqlIndexTable(9));
      String[] diasTotal = toStringArray(cs.getPlsqlIndexTable(10));
      String[] mesTotal = toStringArray(cs.getPlsqlIndexTable(11));
      String[] descSalBase = toStringArray(cs.getPlsqlIndexTable(12));
      String[] valorSalBase = toStringArray(cs.getPlsqlIndexTable(13));
      String[] descSubs = toStringArray(cs.getPlsqlIndexTable(14));
      String[] totalRemun = toStringArray(cs.getPlsqlIndexTable(15));
      String[] diasFeria = toStringArray(cs.getPlsqlIndexTable(16));
      String[] idPai = toStringArray(cs.getPlsqlIndexTable(17));

      // ── Leitura dos arrays FILHO ───────────────────────────────────
      String[] numeros = toStringArray(cs.getPlsqlIndexTable(18));
      String[] dataInicio = toStringArray(cs.getPlsqlIndexTable(19));
      String[] dataFim = toStringArray(cs.getPlsqlIndexTable(20));
      String[] escalao = toStringArray(cs.getPlsqlIndexTable(21));
      String[] escalaoDesc = toStringArray(cs.getPlsqlIndexTable(22));
      String[] valorEscalao = toStringArray(cs.getPlsqlIndexTable(23));
      String[] meses = toStringArray(cs.getPlsqlIndexTable(24));
      String[] dias = toStringArray(cs.getPlsqlIndexTable(25));
      String[] mesesValor = toStringArray(cs.getPlsqlIndexTable(26));
      String[] diasValor = toStringArray(cs.getPlsqlIndexTable(27));
      String[] sumDiaMes = toStringArray(cs.getPlsqlIndexTable(28));
      String[] funIdFilho = toStringArray(cs.getPlsqlIndexTable(29));

      // ── Montagem dos DTOs ──────────────────────────────────────────
      // A procedure usa v_num_PAI para indexar o pai e v_num para indexar
      // o filho. Os filhos são associados ao pai pelo P_FUN_ID_FILHO que
      // corresponde ao P_FUN_ID_PAI do respectivo pai.
      if (funNome.length == 0) {
        return null;
      }

      for (int i = 0; i < funNome.length; i++) {

        final String currentFunId = funIdPai[i];

        // Recolhe todos os filhos cujo FUN_ID_FILHO == FUN_ID_PAI actual
        var detalhes = new ArrayList<SubsidioFeriasDetalheDTO>();
        for (int j = 0; j < numeros.length; j++) {
          boolean pertenceAoPai = currentFunId != null
                                  && currentFunId.equals(safeGet(funIdFilho, j));
          if (pertenceAoPai) {
            detalhes.add(new SubsidioFeriasDetalheDTO(
                safeGet(numeros, j),
                safeGet(dataInicio, j),
                safeGet(dataFim, j),
                safeGet(escalao, j),
                safeGet(escalaoDesc, j),
                safeGet(valorEscalao, j),
                safeGet(meses, j),
                safeGet(dias, j),
                safeGet(mesesValor, j),
                safeGet(diasValor, j),
                safeGet(sumDiaMes, j),
                null, // p_id filho é sempre null na procedure
                safeGet(funIdFilho, j)
            ));
          }
        }

        list.add(new SubsidioFeriasResponseDTO(
            safeGet(funNome, i),
            safeGet(funIdPai, i),
            safeGet(anoRef, i),
            safeGet(estado, i),
            safeGet(valorSubs, i),
            safeGet(diasTotal, i),
            safeGet(mesTotal, i),
            safeGet(descSalBase, i),
            safeGet(valorSalBase, i),
            safeGet(descSubs, i),
            safeGet(totalRemun, i),
            safeGet(diasFeria, i),
            safeGet(idPai, i),
            detalhes
        ));
      }

      return null;
    });

    return list;
  }
}
