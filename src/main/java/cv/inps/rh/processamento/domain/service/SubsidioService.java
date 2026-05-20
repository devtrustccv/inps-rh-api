package cv.inps.rh.processamento.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
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
import oracle.jdbc.internal.OracleCallableStatement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Connection;
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

  public List<SubsidioFeriasResponseDTO> getSubsidioFeriasData(java.sql.Date dataProcessamento, Integer anoReferente, Long direcaoId, Long funId) {

    var list = new ArrayList<SubsidioFeriasResponseDTO>();

    var sql = """
        BEGIN
            RH_PK_SUBSISIO_NATAL_F_DB.SAL_BASE_DET(
                ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,
                ?, ?, ?, ?, ?, ?, ?, ?
            );
        END;
        """;

    jdbcTemplate.execute((Connection con) -> {

      var cs = con.prepareCall(sql).unwrap(OracleCallableStatement.class);

      cs.setDate(1, dataProcessamento);
      cs.setObject(2, anoReferente);
      cs.setObject(3, direcaoId);
      cs.setObject(4, funId);

      cs.registerOutParameter(5, Types.VARCHAR);
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
      cs.registerIndexTableOutParameter(16, 1000, Types.VARCHAR, 4000);

      cs.registerOutParameter(17, Types.NUMERIC);
      cs.registerOutParameter(18, Types.VARCHAR);
      cs.registerOutParameter(19, Types.NUMERIC);
      cs.registerOutParameter(20, Types.VARCHAR);
      cs.registerOutParameter(21, Types.NUMERIC);
      cs.registerOutParameter(22, Types.VARCHAR);
      cs.registerOutParameter(23, Types.VARCHAR);

      cs.execute();

      var funNome = cs.getString(5);

      var numeros = (String[]) cs.getPlsqlIndexTable(6);
      var dataInicio = (String[]) cs.getPlsqlIndexTable(7);
      var dataFim = (String[]) cs.getPlsqlIndexTable(8);
      var escalao = (String[]) cs.getPlsqlIndexTable(9);
      var valorEscalao = (String[]) cs.getPlsqlIndexTable(10);
      var meses = (String[]) cs.getPlsqlIndexTable(11);
      var dias = (String[]) cs.getPlsqlIndexTable(12);
      var mesesValor = (String[]) cs.getPlsqlIndexTable(13);
      var diasValor = (String[]) cs.getPlsqlIndexTable(14);
      var total = (String[]) cs.getPlsqlIndexTable(15);
      var ids = (String[]) cs.getPlsqlIndexTable(16);

      var totalRemun = cs.getString(17);
      var descSalBase = cs.getString(18);
      var valorSalBase = cs.getString(19);
      var descSubsidio = cs.getString(20);
      var valorSubsidio = cs.getString(21);
      var mesTotal = cs.getString(22);
      var diasTotal = cs.getString(23);

      if (numeros != null) {
        for (int i = 0; i < numeros.length; i++) {

          var row = new SubsidioFeriasResponseDTO(
              funNome,
              numeros[i],
              dataInicio[i],
              dataFim[i],
              escalao[i],
              valorEscalao[i],
              meses[i],
              dias[i],
              mesesValor[i],
              diasValor[i],
              total[i],
              ids[i],
              totalRemun,
              descSalBase,
              valorSalBase,
              descSubsidio,
              valorSubsidio,
              mesTotal,
              diasTotal
          );

          list.add(row);
        }
      }

      return null;
    });

    return list;
  }

}
