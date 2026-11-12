package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.queries.GetExportDireitoFeriasQuery;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Exportar Direito (documento FERIAS do analista, 03/11/2026): uma linha por colaborador com direito
 * de férias no ano. É o mesmo modelo que o RH preenche e volta a entrar em "Importar Mapa"
 * ({@link MapaFeriaExcelImportService} lê as colunas pelo nome do cabeçalho).
 *
 * <ul>
 *   <li>Colocação (direção, secção, ilha, município, cargo) do vínculo activo, pela
 *       {@code RH_V_RELACAO_LABORAL} — a mesma fonte dos outros ecrãs.</li>
 *   <li>DATA_ADMISSAO = início do primeiro contrato.</li>
 *   <li>DIAS_REMANESCENTE = direito − gozado dos anos anteriores (nunca negativo);
 *       TOTAL_DIAS_FERIAS = direito do ano (soma das linhas do ano) + remanescente.</li>
 *   <li>Períodos 1 e 2 pré-preenchidos com o mapa activo do ano, se existir.</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class FeriaExcelExportService {

    private final JdbcTemplate jdbcTemplate;

    static final String[] HEADERS = {
        "NUMERO", "ID", "NOME_COLABORADOR", "DATA_ADMISSAO", "DIRECAO", "SECAO", "ILHA", "MUNICIPIO",
        "CARGO", "DIAS_REMANESCENTE", "TOTAL_DIAS_FERIAS", "DATA_INICIO_1", "DATA_FIM_1",
        "DATA_INICIO2", "DATA_FIM2"
    };

    private static final String SQL = """
        WITH dir AS (
            SELECT f.FUN_ID, SUM(NVL(f.NUM_DIA, 0)) DIREITO_ANO
              FROM RH_T_FERIAS f JOIN RH_T_ANO a ON a.ID = f.ANO_ID
             WHERE f.ESTADO = 'A' AND a.ANO = ?
             GROUP BY f.FUN_ID),
        dir_ant AS (
            SELECT f.FUN_ID, SUM(NVL(f.NUM_DIA, 0)) D
              FROM RH_T_FERIAS f JOIN RH_T_ANO a ON a.ID = f.ANO_ID
             WHERE f.ESTADO = 'A' AND TO_NUMBER(a.ANO) < ?
             GROUP BY f.FUN_ID),
        goz_ant AS (
            SELECT g.FUN_ID, SUM(NVL(g.NUM_DIA, 0)) G
              FROM RH_T_FERIAS_GOZADAS g JOIN RH_T_ANO a ON a.ID = g.ANO_ID
             WHERE g.ESTADO = 'A' AND TO_NUMBER(a.ANO) < ?
             GROUP BY g.FUN_ID),
        rl AS (
            SELECT FUN_ID, MAX(DIRECAO_ID) DIRECAO_ID, MAX(DIRECAO_DESC) DIRECAO, MAX(SECCAO_DESC) SECAO,
                   MAX(LOCAL_TRAB_ILHA) ILHA, MAX(CONCELHO_UPS) MUNICIPIO_ID, MAX(CARGO_DESC) CARGO
              FROM RH_V_RELACAO_LABORAL
             WHERE EST_ACT_ADM = 1
             GROUP BY FUN_ID),
        adm AS (
            SELECT FUN_ID, MIN(DATA_INICIO) DATA_ADMISSAO
              FROM RH_T_CONTRATO_VINCULO
             WHERE ESTADO <> 'E'
             GROUP BY FUN_ID),
        mapa AS (
            SELECT m.FUN_ID, m.DATA_INICIO, m.DATA_FIM,
                   ROW_NUMBER() OVER (PARTITION BY m.FUN_ID ORDER BY m.DATA_INICIO, m.ID) RN
              FROM RH_T_FERIAS_MAPA m JOIN RH_T_ANO a ON a.ID = m.ANO_ID
             WHERE m.ESTADO = 'A' AND a.ANO = ?)
        SELECT fu.ID, fu.NOME, adm.DATA_ADMISSAO, rl.DIRECAO, rl.SECAO, rl.ILHA, gm.NOME MUNICIPIO, rl.CARGO,
               GREATEST(NVL(dir_ant.D, 0) - NVL(goz_ant.G, 0), 0) REMANESCENTE,
               dir.DIREITO_ANO + GREATEST(NVL(dir_ant.D, 0) - NVL(goz_ant.G, 0), 0) TOTAL,
               m1.DATA_INICIO INICIO1, m1.DATA_FIM FIM1, m2.DATA_INICIO INICIO2, m2.DATA_FIM FIM2
          FROM dir
          JOIN RH_T_FUNCIONARIOS fu ON fu.ID = dir.FUN_ID
          LEFT JOIN dir_ant ON dir_ant.FUN_ID = dir.FUN_ID
          LEFT JOIN goz_ant ON goz_ant.FUN_ID = dir.FUN_ID
          LEFT JOIN rl ON rl.FUN_ID = dir.FUN_ID
          -- a vista dá o id do concelho da UPS; o nome está na geografia
          LEFT JOIN SIPSGLOBAL.GLB_T_GEOGRAFIA gm ON gm.ID = rl.MUNICIPIO_ID
          LEFT JOIN adm ON adm.FUN_ID = dir.FUN_ID
          LEFT JOIN mapa m1 ON m1.FUN_ID = dir.FUN_ID AND m1.RN = 1
          LEFT JOIN mapa m2 ON m2.FUN_ID = dir.FUN_ID AND m2.RN = 2
        """;

    @Transactional(readOnly = true)
    public byte[] exportDireitoFerias(GetExportDireitoFeriasQuery query) {
        int ano = query.getAnoReferente() != null ? query.getAnoReferente() : LocalDate.now().getYear();
        var direcao = query.getDirecaoId();
        var params = new ArrayList<Object>(List.of(String.valueOf(ano), ano, ano, String.valueOf(ano)));
        var sql = SQL;
        if (direcao != null) {
            sql += " WHERE rl.DIRECAO_ID = ?";
            params.add(direcao);
        }
        sql += " ORDER BY rl.DIRECAO, fu.NOME";

        List<Linha> dados = jdbcTemplate.query(sql, (rs, i) -> new Linha(
                rs.getLong("ID"), rs.getString("NOME"), data(rs.getDate("DATA_ADMISSAO")),
                rs.getString("DIRECAO"), rs.getString("SECAO"), rs.getString("ILHA"), rs.getString("MUNICIPIO"),
                rs.getString("CARGO"), rs.getInt("REMANESCENTE"), rs.getInt("TOTAL"),
                data(rs.getDate("INICIO1")), data(rs.getDate("FIM1")),
                data(rs.getDate("INICIO2")), data(rs.getDate("FIM2"))),
            params.toArray());

        return gerarExcel(new ArrayList<>(dados));
    }

    private static LocalDate data(Date d) {
        return d != null ? d.toLocalDate() : null;
    }

    record Linha(long id, String nome, LocalDate admissao, String direcao, String secao, String ilha,
                 String municipio, String cargo, int remanescente, int total,
                 LocalDate inicio1, LocalDate fim1, LocalDate inicio2, LocalDate fim2) {
    }

    static byte[] gerarExcel(List<Linha> dados) {
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Direito Ferias");

            CellStyle headerStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            CellStyle dataStyle = wb.createCellStyle();
            dataStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd/mm/yyyy"));

            Row header = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Linha l : dados) {
                Row row = sheet.createRow(rowIdx);
                row.createCell(0).setCellValue(rowIdx);
                row.createCell(1).setCellValue(l.id());
                row.createCell(2).setCellValue(texto(l.nome()));
                data(row, 3, l.admissao(), dataStyle);
                row.createCell(4).setCellValue(texto(l.direcao()));
                row.createCell(5).setCellValue(texto(l.secao()));
                row.createCell(6).setCellValue(texto(l.ilha()));
                row.createCell(7).setCellValue(texto(l.municipio()));
                row.createCell(8).setCellValue(texto(l.cargo()));
                row.createCell(9).setCellValue(l.remanescente());
                row.createCell(10).setCellValue(l.total());
                data(row, 11, l.inicio1(), dataStyle);
                data(row, 12, l.fim1(), dataStyle);
                data(row, 13, l.inicio2(), dataStyle);
                data(row, 14, l.fim2(), dataStyle);
                rowIdx++;
            }

            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel de direito de férias", e);
        }
    }

    private static String texto(String s) {
        return s != null ? s : "";
    }

    /** Data como data do Excel (dd/mm/aaaa); célula vazia sem valor, para o RH preencher. */
    private static void data(Row row, int col, LocalDate valor, CellStyle estilo) {
        Cell cell = row.createCell(col);
        cell.setCellStyle(estilo);
        if (valor != null)
            cell.setCellValue(valor);
    }
}
