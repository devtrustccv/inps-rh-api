package cv.inps.rh.processamento.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.services.model.WrapperListDTO;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.processamento.application.dto.SubsidioFeriaDetalheFullDTO;
import cv.inps.rh.processamento.application.dto.SubsidioResponseNatalDTO;
import cv.inps.rh.processamento.domain.models.SubsidioNatalStatus;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.ApplicationAuditorAware;
import cv.inps.rh.shared.infrastructure.persistence.entity.SubsidioNatalEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ValidacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.PageMapper;
import lombok.AllArgsConstructor;
import oracle.jdbc.internal.OracleCallableStatement;
import org.springframework.data.domain.PageRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.Types;
import java.time.Year;
import java.time.ZoneId;
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
  private final VSubsidioFeriaEntityRepository vsubsidioFeriaEntityRepository;
  private final SubsidioFeriaEntityRepository subsidioFeriaEntityRepository;
  private final VSubsidioFeriasDetailEntityRepository subsidioFeriasDetailEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final ApplicationAuditorAware applicationAuditorAware;

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

  @Transactional(readOnly = true)
  public WrapperListDTO getSubsidioFeriasData(Integer anoReferente, Long direcaoId, Long funId, Integer page, Integer size) {

    var data = vsubsidioFeriaEntityRepository.findSubsidioFeriasByAno(
        anoReferente != null ? anoReferente : Year.now(ZoneId.systemDefault()).getValue(),
        direcaoId,
        funId,
        PageRequest.of(page, size)
    );

    var response = new WrapperListDTO();
    PageMapper.fillPagination(data, response);
    response.setContent(data.getContent());
    return response;
  }

  public void alterarEstadoSubsisioferias(Long subsidioId) {
    var subsidioFeria = subsidioFeriaEntityRepository.findById(subsidioId).orElseThrow();
    subsidioFeria.setFlgAtivoInactivo(Estado.A.name().equals(subsidioFeria.getFlgAtivoInactivo()) ? Estado.I.name() : Estado.A.name());
    subsidioFeriaEntityRepository.save(subsidioFeria);
  }

  public void validarSubsidioFerias(Long subsidioId) {
    var subsidioFeria = subsidioFeriaEntityRepository.findById(subsidioId).orElseThrow();
    subsidioFeria.setEstado(Estado.A.name());
    subsidioFeriaEntityRepository.save(subsidioFeria);
  }

  public void calcularSubsidioferias(Integer ano) {
    vsubsidioFeriaEntityRepository.calcularDiasFeria(
        ano.toString(),
        1L, // TODO 06/09/2026 13:30 remove this from procedure
        applicationAuditorAware.getCurrentSubjectName()
    );
  }

  public SubsidioFeriaDetalheFullDTO getDetalhesSubsidio(Integer ano, Long funId) {

    var rows = subsidioFeriasDetailEntityRepository.getDetails(funId, ano);

    var response = new SubsidioFeriaDetalheFullDTO();
    response.setRows(rows);

    long totalRemuneracao = 0L;

    for (var row : rows) {
      totalRemuneracao = totalRemuneracao + row.valorEscalaotempo();
    }

    response.setTotalRemuneracao(totalRemuneracao);
    response.setSalarioBaseCalculo(null);
    response.setValorSubsidio(null);
    return response;
  }
}
