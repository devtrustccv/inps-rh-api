package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/** O Excel do "Exportar Direito" é o modelo que volta a entrar em "Importar Mapa". */
class ModeloMapaFeriasTest {

  private final MapaFeriaExcelImportService importacao = new MapaFeriaExcelImportService(null, null, null);

  @Test
  void excelExportadoEhLidoPelaImportacao() throws Exception {
    var linha = new FeriaExcelExportService.Linha(958959L, "Wilson", LocalDate.of(2026, 10, 12), "DRHDO",
        "UGAL", "SANTIAGO", "PRAIA", "Técnico", 4, 26,
        LocalDate.of(2026, 12, 14), LocalDate.of(2026, 12, 18), null, null);
    byte[] excel = FeriaExcelExportService.gerarExcel(List.of(linha));

    try (var wb = WorkbookFactory.create(new ByteArrayInputStream(excel))) {
      var sheet = wb.getSheetAt(0);
      assertThat(sheet.getRow(0).getCell(0).getStringCellValue()).isEqualTo("NUMERO");
      assertThat(sheet.getRow(0).getLastCellNum()).isEqualTo((short) 15);

      var col = importacao.colunas(sheet);
      assertThat(col).isEqualTo(new MapaFeriaExcelImportService.Colunas(1, 11, 12, 13, 14));

      var row = sheet.getRow(1);
      assertThat((long) row.getCell(col.id()).getNumericCellValue()).isEqualTo(958959L);
      assertThat(importacao.lerData(row.getCell(col.inicio1()))).isEqualTo(LocalDate.of(2026, 12, 14));
      assertThat(importacao.lerData(row.getCell(col.fim1()))).isEqualTo(LocalDate.of(2026, 12, 18));
      assertThat(importacao.lerData(row.getCell(col.inicio2()))).isNull();
      assertThat(row.getCell(9).getNumericCellValue()).isEqualTo(4);   // DIAS_REMANESCENTE
      assertThat(row.getCell(10).getNumericCellValue()).isEqualTo(26); // TOTAL_DIAS_FERIAS
    }
  }

  @Test
  void cabecalhoComEspacosEAcentosTambemServe() {
    assertThat(MapaFeriaExcelImportService.normalizar("Data Início 1")).isEqualTo("DATAINICIO1");
    assertThat(MapaFeriaExcelImportService.normalizar("DATA_INICIO_2")).isEqualTo("DATAINICIO2");
    assertThat(MapaFeriaExcelImportService.normalizar("DATA_FIM2")).isEqualTo("DATAFIM2");
  }

  @Test
  void modeloAntigoDoRhContinuaPorPosicao() throws Exception {
    try (var wb = new XSSFWorkbook()) {
      var sheet = wb.createSheet();
      var cab = sheet.createRow(0);
      cab.createCell(0).setCellValue("Nº");
      cab.createCell(1).setCellValue("ID");
      cab.createCell(13).setCellValue("Início Férias 1");
      assertThat(importacao.colunas(sheet)).isEqualTo(new MapaFeriaExcelImportService.Colunas(1, 13, 14, 16, 17));
    }
  }

  @Test
  void ficheiroQueNaoEhOModeloEhRecusado() throws Exception {
    try (var wb = new XSSFWorkbook()) {
      var sheet = wb.createSheet();
      sheet.createRow(0).createCell(0).setCellValue("QUALQUER");
      assertThatThrownBy(() -> importacao.colunas(sheet))
          .isInstanceOf(IgrpResponseStatusException.class);
    }
  }
}
