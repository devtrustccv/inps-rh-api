package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.queries.GetDetalheMapaFeriaQuery;
import cv.inps.rh.assiduidade.application.queries.GetExportarMapaFeriaQuery;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class MapaFeriaExcelExportService {

    private final MapaFeriaReadService mapaFeriaReadService;

    @Transactional(readOnly = true)
    public byte[] exportarMapaFeria(GetExportarMapaFeriaQuery query) {
        var detalhe = mapaFeriaReadService.getDetalheMapaFeria(
            new GetDetalheMapaFeriaQuery(query.getAno(), query.getDirecao()));

        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            CellStyle headerStyle = criarEstiloCabecalho(wb);

            // Folha 1 — Férias Agendadas
            Sheet sheetAgendadas = wb.createSheet("Ferias Agendadas");
            Row h1 = sheetAgendadas.createRow(0);
            String[] colsAgendadas = {"NOME_COLABORADOR", "TOTAL_DIREITO", "TOTAL_DIREITO_ANO", "DATA_INICIO", "DATA_FIM"};
            for (int i = 0; i < colsAgendadas.length; i++) {
                Cell c = h1.createCell(i);
                c.setCellValue(colsAgendadas[i]);
                c.setCellStyle(headerStyle);
            }
            int rowIdx = 1;
            for (var item : detalhe.getFeriasAgendadas()) {
                Row row = sheetAgendadas.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getNomeColaborador() != null ? item.getNomeColaborador() : "");
                row.createCell(1).setCellValue(item.getTotalDireito() != null ? item.getTotalDireito() : 0);
                row.createCell(2).setCellValue(item.getTotalDireitoPorAno() != null ? item.getTotalDireitoPorAno() : 0);
                row.createCell(3).setCellValue(item.getDataInicio() != null ? item.getDataInicio().toString() : "");
                row.createCell(4).setCellValue(item.getDataFim() != null ? item.getDataFim().toString() : "");
            }
            for (int i = 0; i < colsAgendadas.length; i++) sheetAgendadas.autoSizeColumn(i);

            // Folha 2 — Férias por Agendar
            Sheet sheetPorAgendar = wb.createSheet("Ferias por Agendar");
            Row h2 = sheetPorAgendar.createRow(0);
            String[] colsPorAgendar = {"NOME_COLABORADOR", "TOTAL_DIREITO", "TOTAL_DIREITO_ANO"};
            for (int i = 0; i < colsPorAgendar.length; i++) {
                Cell c = h2.createCell(i);
                c.setCellValue(colsPorAgendar[i]);
                c.setCellStyle(headerStyle);
            }
            rowIdx = 1;
            for (var item : detalhe.getFeriasPorAgendar()) {
                Row row = sheetPorAgendar.createRow(rowIdx++);
                row.createCell(0).setCellValue(item.getNomeColaborador() != null ? item.getNomeColaborador() : "");
                row.createCell(1).setCellValue(item.getTotalDireito() != null ? item.getTotalDireito() : 0);
                row.createCell(2).setCellValue(item.getTotalDireitoPorAno() != null ? item.getTotalDireitoPorAno() : 0);
            }
            for (int i = 0; i < colsPorAgendar.length; i++) sheetPorAgendar.autoSizeColumn(i);

            wb.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel do mapa de férias", e);
        }
    }

    private CellStyle criarEstiloCabecalho(Workbook wb) {
        CellStyle style = wb.createCellStyle();
        Font font = wb.createFont();
        font.setBold(true);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        return style;
    }
}
