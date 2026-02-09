package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.processamento.application.dto.MovRowDTO;
import cv.inps.rh.processamento.application.dto.MovimentosImportadosDTO;
import cv.inps.rh.processamento.application.queries.GetMovimentosImportadosQuery;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.ImportacaoMovimentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ImportacaoMovimentoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
public class MovimentoService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MovimentoService.class);

  private final ImportacaoMovimentoEntityRepository importacaoMovimentoEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;

  public void uploadMovement(MultipartFile file) {

    try (var workbook = WorkbookFactory.create(file.getInputStream())) {

      var sheet = workbook.getSheetAt(0);
      var formatter = new DataFormatter();

      for (int i = 1; i <= sheet.getLastRowNum(); i++) {

        var row = sheet.getRow(i);
        if (row == null) continue;

        var uuid = formatter.formatCellValue(row.getCell(0)).trim();
        var mov = new ImportacaoMovimentoEntity();
        mov.setFuncionario(funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(uuid)));
        mov.setTpMovRetencao(formatter.formatCellValue(row.getCell(2)));
        mov.setTpMovRem(formatter.formatCellValue(row.getCell(3)));
        mov.setPercentagem(BigDecimal.valueOf(row.getCell(4).getNumericCellValue()));
        mov.setValor(BigDecimal.valueOf(row.getCell(5).getNumericCellValue()));
        mov.setDataInicio(DateFormatter.stringToLocalDate(formatter.formatCellValue(row.getCell(6))));
        mov.setDataFim(DateFormatter.stringToLocalDate(formatter.formatCellValue(row.getCell(7))));
        mov.setSituacao(formatter.formatCellValue(row.getCell(8)));
        importacaoMovimentoEntityRepository.save(mov);


      }

    } catch (Exception e) {
      LOGGER.error(e.getMessage(), e);
      throw IgrpResponseStatusException.internalServerError("Erro ao processar arquivo");
    }
  }

  public MovimentosImportadosDTO getMovimentos(GetMovimentosImportadosQuery request) {

    var pageRequest = PageRequest.of(
        Integer.parseInt(request.getPage()),
        Integer.parseInt(request.getSize())
    );

    final Page<ImportacaoMovimentoEntity> importsSaved;

    if (StringUtils.hasText(request.getDataImportacao())) {

      var date = DateFormatter.stringToLocalDate(request.getDataImportacao());

      importsSaved = importacaoMovimentoEntityRepository.findByCreatedDateBetween(
          date.atStartOfDay(),
          date.atTime(LocalTime.MAX),
          pageRequest
      );
    } else {
      importsSaved = importacaoMovimentoEntityRepository.findAll(pageRequest);
    }

    var response = new MovimentosImportadosDTO();
    PageMapper.fillPagination(importsSaved, response);
    response.setContent(importsSaved.stream()
        .map(obj -> {
          var movRowDTO = new MovRowDTO();
          movRowDTO.setMovimentoId(obj.getId().toString());
          movRowDTO.setNomeFicheiro("");
          movRowDTO.setFuncionarioId(obj.getFuncionario().getId().toString());
          movRowDTO.setNomeFuncionario(obj.getFuncionario().getNome());
          movRowDTO.setMovimentoRetencao(obj.getTpMovRetencao());
          movRowDTO.setMovimentoRemuneracao(obj.getTpMovRem());
          movRowDTO.setPercentagem(obj.getPercentagem());
          movRowDTO.setValor(obj.getValor());
          movRowDTO.setDataInicio(obj.getDataInicio());
          movRowDTO.setDataFim(obj.getDataFim());
          movRowDTO.setSituacao(obj.getSituacao());
          return movRowDTO;
        })
        .toList());

    return response;
  }

}
