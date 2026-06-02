package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.queries.GetExportDireitoFeriasQuery;
import cv.inps.rh.shared.infrastructure.persistence.entity.VFeriasMensalEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.VFeriasMensalEntityRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeriaExcelExportService {

    private final VFeriasMensalEntityRepository vFeriasMensalEntityRepository;

    private static final String[] HEADERS = {
        "CODIGO_DIRECAO", "NOME_DIRECAO", "ID_COLABORADOR",
        "NOME_COLABORADOR", "TOTAL_DIREITO", "TOTAL_DIREITO_ANO"
    };

    @Transactional(readOnly = true)
    public byte[] exportDireitoFerias(GetExportDireitoFeriasQuery query) {

        // Construir filtros
        Specification<VFeriasMensalEntity> spec = (root, cq, cb) -> cb.conjunction();

        if (query.getAnoReferente() != null) {
            spec = spec.and((root, cq, cb) -> cb.equal(root.get("ano"), query.getAnoReferente()));
        }
        if (query.getDirecaoId() != null) {
            spec = spec.and((root, cq, cb) -> cb.equal(root.get("direcaoId"), query.getDirecaoId()));
        }

        List<VFeriasMensalEntity> dados = vFeriasMensalEntityRepository.findAll(spec);

        return gerarExcel(dados);
    }

    private byte[] gerarExcel(List<VFeriasMensalEntity> dados) {
        try (Workbook wb = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = wb.createSheet("Direito Ferias");

            // Estilo do cabeçalho
            CellStyle headerStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            headerStyle.setFont(font);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);

            // Linha do cabeçalho
            Row header = sheet.createRow(0);
            for (int i = 0; i < HEADERS.length; i++) {
                Cell cell = header.createCell(i);
                cell.setCellValue(HEADERS[i]);
                cell.setCellStyle(headerStyle);
            }

            // Dados
            int rowIdx = 1;
            for (VFeriasMensalEntity e : dados) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(e.getCodigoDirecao() != null ? e.getCodigoDirecao() : "");
                row.createCell(1).setCellValue(e.getDirecao() != null ? e.getDirecao() : "");
                row.createCell(2).setCellValue(e.getIdColaborador() != null ? e.getIdColaborador() : "");
                row.createCell(3).setCellValue(e.getNomeColaborador() != null ? e.getNomeColaborador() : "");
                row.createCell(4).setCellValue(e.getTotalDireito() != null ? e.getTotalDireito() : 0);
                row.createCell(5).setCellValue(e.getTotalDireitoAno() != null ? e.getTotalDireitoAno() : 0);
            }

            // Auto-size colunas
            for (int i = 0; i < HEADERS.length; i++) {
                sheet.autoSizeColumn(i);
            }

            wb.write(out);
            return out.toByteArray();

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel de direito de férias", e);
        }
    }
}
