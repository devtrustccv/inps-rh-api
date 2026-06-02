package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.queries.ImportarDadosPicagemQuery;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;

/**
 * Invoca INPSRH.IMPORT_DADOS_CONTR_ACESSO para importar dados do relógio de ponto
 * para RH_MOVIMENTOS, conforme spec:
 *
 *   1. Verificar se há dados já processados no período (processado=1)
 *   2. Se não houver → apagar dados não processados e importar via procedure
 */
@Service
@RequiredArgsConstructor
public class PicagemImportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PicagemImportService.class);
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public Map<String, Object> importarDados(ImportarDadosPicagemQuery query) {
        LocalDate inicio = LocalDate.parse(query.getDataInicio());
        LocalDate fim    = LocalDate.parse(query.getDataFim());

        if (fim.isBefore(inicio))
            throw IgrpResponseStatusException.badRequest("Data fim não pode ser anterior à data início");

        String dataInicioFmt = inicio.format(FMT);
        String dataFimFmt    = fim.format(FMT);

        // Bloco PL/SQL conforme spec (secção "Importar Dados"):
        //   1. Conta processados no período
        //   2. Se 0 → DELETE não processados + chama IMPORT_DADOS_CONTR_ACESSO
        String plsql = """
                DECLARE
                  v_count_processado NUMBER := 0;
                BEGIN
                  SELECT COUNT(id)
                  INTO   v_count_processado
                  FROM   rh_movimentos
                  WHERE  TRUNC(dt_movimento) BETWEEN TRUNC(TO_DATE(?, 'YYYY-MM-DD'))
                                                 AND TRUNC(TO_DATE(?, 'YYYY-MM-DD'))
                  AND    processado = 1;

                  IF v_count_processado = 0 THEN
                    DELETE FROM rh_movimentos
                    WHERE  TRUNC(dt_movimento) BETWEEN TRUNC(TO_DATE(?, 'YYYY-MM-DD'))
                                                   AND TRUNC(TO_DATE(?, 'YYYY-MM-DD'));

                    INPSRH.IMPORT_DADOS_CONTR_ACESSO(
                        p_data_inicio => ?,
                        p_data_fim    => TO_CHAR(TO_DATE(?, 'YYYY-MM-DD') + 1, 'YYYY-MM-DD')
                    );
                  END IF;
                EXCEPTION WHEN OTHERS THEN
                  NULL;
                END;
                """;

        try {
            jdbcTemplate.execute((java.sql.Connection conn) -> {
                try (java.sql.PreparedStatement ps = conn.prepareStatement(plsql)) {
                    // Params: inicio(1), fim(2), inicio(3), fim(4), inicio(5), fim(6)
                    ps.setString(1, dataInicioFmt);
                    ps.setString(2, dataFimFmt);
                    ps.setString(3, dataInicioFmt);
                    ps.setString(4, dataFimFmt);
                    ps.setString(5, dataInicioFmt);
                    ps.setString(6, dataFimFmt);
                    ps.execute();
                }
                return null;
            });
            LOGGER.info("Importação de picagens executada: {} a {}", dataInicioFmt, dataFimFmt);
        } catch (Exception e) {
            LOGGER.error("Erro na importação de picagens: {}", e.getMessage());
            throw IgrpResponseStatusException.badRequest("Erro ao importar dados de picagem: " + e.getMessage());
        }

        return Map.of(
                "dataInicio", dataInicioFmt,
                "dataFim", dataFimFmt,
                "status", "importado"
        );
    }
}
