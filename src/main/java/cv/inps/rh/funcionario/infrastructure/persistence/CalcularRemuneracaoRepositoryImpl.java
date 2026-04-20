package cv.inps.rh.funcionario.infrastructure.persistence;

import cv.inps.rh.funcionario.application.dto.CalcularRemuneracaoRequestDTO;
import cv.inps.rh.funcionario.application.dto.DescontoItemDTO;
import cv.inps.rh.funcionario.application.dto.SubsidioItemDTO;
import cv.inps.rh.funcionario.domain.repository.ICalcularRemuneracaoRepository;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.Types;
import java.time.LocalDate;
import java.util.List;

@Repository
public class CalcularRemuneracaoRepositoryImpl implements ICalcularRemuneracaoRepository {

  private static final Logger LOGGER = LoggerFactory.getLogger(CalcularRemuneracaoRepositoryImpl.class);

  private static final String PACKAGE = "RH_PROCESSAMENTO_SALARIAL_DB";
  private static final String PROCEDURE = "CalcularDesAtual";

  @PersistenceContext
  private EntityManager entityManager;

  @Override
  public BigDecimal[] calcularDesAtual(CalcularRemuneracaoRequestDTO request) {
    List<SubsidioItemDTO> subsidios = request.getSubsidios();
    List<DescontoItemDTO> descontos = request.getDescontos();

    String sql = buildAnonymousBlock(subsidios.size(), descontos.size());

    return entityManager.unwrap(Session.class).doReturningWork(connection -> {
      try (CallableStatement cs = connection.prepareCall(sql)) {
        int idx = 1;

        for (SubsidioItemDTO s : subsidios) {
          cs.setString(idx++, String.valueOf(s.getTmId()));
          cs.setString(idx++, s.getValor() != null ? s.getValor().toPlainString() : null);
        }

        for (DescontoItemDTO d : descontos) {
          cs.setString(idx++, String.valueOf(d.getTmId()));
          cs.setString(idx++, d.getValor() != null ? d.getValor().toPlainString() : null);
        }

        cs.setBigDecimal(idx++, request.getSalario());
        cs.setString(idx++, request.getMoeda());
        cs.setDate(idx++, Date.valueOf(LocalDate.parse(request.getDataInicio())));

        int idxTotalRemun = idx++;
        int idxTotalPag = idx;
        cs.registerOutParameter(idxTotalRemun, Types.NUMERIC);
        cs.registerOutParameter(idxTotalPag, Types.NUMERIC);

        cs.execute();

        BigDecimal totalRemun = cs.getBigDecimal(idxTotalRemun);
        BigDecimal totalPag = cs.getBigDecimal(idxTotalPag);

        LOGGER.info("{}.{} => p_total_remun={}, P_total_pagamentos={}", PACKAGE, PROCEDURE, totalRemun, totalPag);

        return new BigDecimal[]{
            totalRemun != null ? totalRemun : BigDecimal.ZERO,
            totalPag != null ? totalPag : BigDecimal.ZERO
        };

      } catch (Exception e) {
        LOGGER.error("Erro ao chamar {}.{}: {}", PACKAGE, PROCEDURE, e.getMessage(), e);
        throw IgrpResponseStatusException.internalServerError("Erro ao calcular remuneração");
      }
    });
  }

  private String buildAnonymousBlock(int numSubsidios, int numDescontos) {
    StringBuilder sb = new StringBuilder();
    sb.append("DECLARE\n");
    sb.append("  v_sub_ids  OWA.vc_arr;\n");
    sb.append("  v_sub_vals OWA.vc_arr;\n");
    sb.append("  v_des_ids  OWA.vc_arr;\n");
    sb.append("  v_des_vals OWA.vc_arr;\n");
    sb.append("  v_tipo     OWA.vc_arr;\n");
    sb.append("  v_remun    NUMBER;\n");
    sb.append("  v_pag      NUMBER;\n");
    sb.append("BEGIN\n");

    for (int i = 1; i <= numSubsidios; i++) {
      sb.append("  v_sub_ids(").append(i).append(") := ?;\n");
      sb.append("  v_sub_vals(").append(i).append(") := ?;\n");
    }

    for (int i = 1; i <= numDescontos; i++) {
      sb.append("  v_des_ids(").append(i).append(") := ?;\n");
      sb.append("  v_des_vals(").append(i).append(") := ?;\n");
    }

    int tipoCount = Math.max(numSubsidios, numDescontos);
    for (int i = 1; i <= tipoCount; i++) {
      sb.append("  v_tipo(").append(i).append(") := 'SAL';\n");
    }

    sb.append("  ").append(PACKAGE).append(".").append(PROCEDURE).append("(\n");
    sb.append("    p_tm_id_subsidio   => v_sub_ids,\n");
    sb.append("    p_valor_subsidio   => v_sub_vals,\n");
    sb.append("    p_tm_id_desconto   => v_des_ids,\n");
    sb.append("    p_valor_desconto   => v_des_vals,\n");
    sb.append("    p_tipo_remuneracao => v_tipo,\n");
    sb.append("    p_valor_base       => ?,\n");
    sb.append("    P_moeda            => ?,\n");
    sb.append("    p_data_de          => TO_DATE(?, 'YYYY-MM-DD'),\n");
    sb.append("    p_total_remun      => v_remun,\n");
    sb.append("    P_total_pagamentos => v_pag\n");
    sb.append("  );\n");
    sb.append("  ? := v_remun;\n");
    sb.append("  ? := v_pag;\n");
    sb.append("END;");

    return sb.toString();
  }

}
