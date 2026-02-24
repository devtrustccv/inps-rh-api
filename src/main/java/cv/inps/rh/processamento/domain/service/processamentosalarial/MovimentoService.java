package cv.inps.rh.processamento.domain.service.processamentosalarial;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.processamento.application.dto.MovRowDTO;
import cv.inps.rh.processamento.application.dto.MovimentosImportadosDTO;
import cv.inps.rh.processamento.application.dto.ValidacaoMovimentoImportadoDTO;
import cv.inps.rh.processamento.application.queries.GetMovimentosImportadosQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
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
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final TipoMovimentoEntityRepository tipoMovimentoEntityRepository;
  private final RemuneracaoTiprelEntityRepository remuneracaoTiprelEntityRepository;
  private final DefPagamentoEntityRepository defPagamentoEntityRepository;
  private final PagTiprelEntityRepository pagTiprelEntityRepository;
  private final FuncionarioRules funcionarioRules;

  public void uploadMovement(MultipartFile file) {

    var fileName = file.getOriginalFilename();

    try (var workbook = WorkbookFactory.create(file.getInputStream())) {

      var sheet = workbook.getSheetAt(0);
      var formatter = new DataFormatter();

      for (int i = 1; i <= sheet.getLastRowNum(); i++) {

        var row = sheet.getRow(i);
        if (row == null) continue;

        var uuid = formatter.formatCellValue(row.getCell(0)).trim();
        var mov = new ImportacaoMovimentoEntity();
        mov.setUuid(UuidCreator.getTimeOrderedEpoch());
        mov.setFuncionario(funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(uuid)));
        mov.setTpMovRetencao(formatter.formatCellValue(row.getCell(2)));
        mov.setTpMovRem(formatter.formatCellValue(row.getCell(3)));
        mov.setPercentagem(BigDecimal.valueOf(row.getCell(4).getNumericCellValue()));
        mov.setValor(BigDecimal.valueOf(row.getCell(5).getNumericCellValue()));
        mov.setDataInicio(DateFormatter.stringToLocalDate(formatter.formatCellValue(row.getCell(6))));
        mov.setDataFim(DateFormatter.stringToLocalDate(formatter.formatCellValue(row.getCell(7))));
        mov.setSituacao(formatter.formatCellValue(row.getCell(8)));
        mov.setNomeFicheiro(fileName);
        mov.setEstado(Estado.A.name());
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

  public void validarMovimento(String movimentoId, ValidacaoMovimentoImportadoDTO data) {

    var mov = importacaoMovimentoEntityRepository.findByUuidOrThrow(UUID.fromString(movimentoId));
    mov.setTpMovRetencao(data.getMovimentoRetencao());
    mov.setTpMovRem(data.getMovimentoRemuneracao());
    mov.setPercentagem(data.getPercentagem());
    mov.setValor(data.getValor());
    mov.setDataInicio(data.getDataInicio());
    mov.setDataFim(data.getDataFim());
    mov.setSituacao(data.getSituacao());
    importacaoMovimentoEntityRepository.save(mov);

    var fun = mov.getFuncionario();
    var currentRelation = funcionarioRules.getTipoRelacionamentoAtual(fun.getUuid());


    var validation = data.getValidacao();

    if (validation.equals("DESVALIDAR")) {   // TODO 10/02/2026 validar este codigo
      mov.setEstado(Estado.I.name());
      importacaoMovimentoEntityRepository.save(mov);
      return;
    }

    if (validation.equals("VALIDAR")) {   // TODO 10/02/2026  validar este codigo

      if (StringUtils.hasText(mov.getTpMovRem())) {

        var remunerationDefinition = new DefinicaoRemuneracaoEntity();
        remunerationDefinition.setPercentagem(mov.getPercentagem());
        remunerationDefinition.setValor(mov.getValor());
        remunerationDefinition.setEstado(Estado.A);
        remunerationDefinition.setObs("Registo referente ao ficheiro:" + mov.getNomeFicheiro());
        remunerationDefinition.setUuid(UuidCreator.getTimeOrderedEpoch());
        remunerationDefinition.setTmId(tipoMovimentoEntityRepository.findByIdOrThrow(Long.valueOf(mov.getTpMovRem())));
        remunerationDefinition.setMoeda("CVE");
        remunerationDefinition.setDataInicio(mov.getDataInicio());
        remunerationDefinition.setDataFim(mov.getDataFim());
        remunerationDefinition.setFunId(fun);
        var saved = definicaoRemuneracaoEntityRepository.save(remunerationDefinition);

        var remunerationRelType = new RemuneracaoTiprelEntity();
        remunerationRelType.setEstado(Estado.A);
        remunerationRelType.setObs(saved.getObs());
        remunerationRelType.setUuid(UuidCreator.getTimeOrderedEpoch());
        remunerationRelType.setRemId(saved);
        remunerationRelType.setTiprelId(currentRelation);
        remuneracaoTiprelEntityRepository.save(remunerationRelType);
      }

      if (StringUtils.hasText(mov.getTpMovRetencao())) {

        var paymentDefinition = new DefPagamentoEntity();
        paymentDefinition.setTmId(tipoMovimentoEntityRepository.findByIdOrThrow(Long.valueOf(mov.getTpMovRetencao())));
        paymentDefinition.setValor(mov.getValor());
        paymentDefinition.setDataInicio(mov.getDataInicio());
        paymentDefinition.setDataFim(mov.getDataFim());
        paymentDefinition.setEstado(Estado.A);
        paymentDefinition.setObs("Registo referente ao ficheiro:" + mov.getNomeFicheiro());
        paymentDefinition.setUuid(UuidCreator.getTimeOrderedEpoch());
        paymentDefinition.setPercentagem(mov.getPercentagem());
        paymentDefinition.setFunId(fun);
        var saved = defPagamentoEntityRepository.save(paymentDefinition);

        var relPaymentType = new PagTiprelEntity();
        relPaymentType.setPagId(saved);
        relPaymentType.setTiprelId(currentRelation);
        relPaymentType.setEstado(Estado.A);
        relPaymentType.setObs(saved.getObs());
        relPaymentType.setUuid(UuidCreator.getTimeOrderedEpoch());
        pagTiprelEntityRepository.save(relPaymentType);
      }
    }
  }

}
