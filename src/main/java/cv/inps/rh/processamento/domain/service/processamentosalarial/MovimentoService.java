package cv.inps.rh.processamento.domain.service.processamentosalarial;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.processamento.application.dto.MovRowDTO;
import cv.inps.rh.processamento.application.dto.MovimentosImportadosDTO;
import cv.inps.rh.processamento.application.dto.ValidacaoMovimentoImportadoDTO;
import cv.inps.rh.processamento.application.queries.GetMovimentosImportadosQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
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

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

  private static BigDecimal parseNumero(Cell cell, DataFormatter formatter, String campo, List<String> erros) {
    if (cell == null || cell.getCellType() == CellType.BLANK)
      return null;
    if (cell.getCellType() == CellType.NUMERIC || cell.getCellType() == CellType.FORMULA) {
      try {
        return BigDecimal.valueOf(cell.getNumericCellValue());
      } catch (IllegalStateException e) {
        // fórmula que não devolve número: cai na leitura como texto
      }
    }
    var texto = formatter.formatCellValue(cell).trim().replace(",", ".");
    try {
      return new BigDecimal(texto);
    } catch (NumberFormatException e) {
      erros.add(campo + " inválido (" + texto + ")");
      return null;
    }
  }

  private static LocalDate parseData(String valor, String campo, List<String> erros) {
    if (!StringUtils.hasText(valor))
      return null;
    var data = DateFormatter.stringToLocalDate(valor.trim());
    if (data == null)
      erros.add(campo + " inválida (" + valor.trim() + ")");
    return data;
  }

  public void uploadMovement(MultipartFile file) {

    var fileName = file.getOriginalFilename();
    var movimentos = new ArrayList<ImportacaoMovimentoEntity>();
    var erros = new ArrayList<String>();

    try (var workbook = WorkbookFactory.create(file.getInputStream())) {

      var sheet = workbook.getSheetAt(0);
      var formatter = new DataFormatter();

      for (int i = 1; i <= sheet.getLastRowNum(); i++) {

        var row = sheet.getRow(i);
        if (row == null || formatter.formatCellValue(row.getCell(0)).isBlank()) continue;

        // linha como o utilizador a vê no Excel (cabeçalho = linha 1)
        var linha = i + 1;
        var errosLinha = new ArrayList<String>();

        var funcionario = parseFuncionario(formatter.formatCellValue(row.getCell(0)).trim(), errosLinha);
        var percentagem = parseNumero(row.getCell(4), formatter, "Percentagem", errosLinha);
        var valor = parseNumero(row.getCell(5), formatter, "Valor", errosLinha);
        var dataInicio = parseData(formatter.formatCellValue(row.getCell(6)), "Data Início", errosLinha);
        var dataFim = parseData(formatter.formatCellValue(row.getCell(7)), "Data Fim", errosLinha);
        if (dataInicio != null && dataFim != null && dataFim.isBefore(dataInicio))
          errosLinha.add("Data Fim anterior à Data Início");

        if (!errosLinha.isEmpty()) {
          erros.add("Linha %d: %s".formatted(linha, String.join("; ", errosLinha)));
          continue;
        }

        var mov = new ImportacaoMovimentoEntity();
        mov.setUuid(UuidCreator.getTimeOrderedEpoch());
        mov.setFuncionario(funcionario);
        mov.setTpMovRetencao(formatter.formatCellValue(row.getCell(2)));
        mov.setTpMovRem(formatter.formatCellValue(row.getCell(3)));
        mov.setPercentagem(percentagem);
        mov.setValor(valor);
        mov.setDataInicio(dataInicio);
        mov.setDataFim(dataFim);
        mov.setSituacao(formatter.formatCellValue(row.getCell(8)));
        mov.setNomeFicheiro(fileName);
        mov.setEstado(Estado.A.name());
        movimentos.add(mov);
      }

    } catch (IOException | RuntimeException e) {
      // ficheiro que o POI não consegue abrir (não é Excel, corrompido, ...)
      LOGGER.error(e.getMessage(), e);
      throw IgrpResponseStatusException.badRequest("Ficheiro inválido: deve ser um Excel (.xls/.xlsx) no formato do modelo");
    }

    // tudo ou nada: com erros não se grava nenhuma linha
    if (!erros.isEmpty())
      throw IgrpResponseStatusException.badRequest("O ficheiro tem linhas inválidas", erros);

    if (movimentos.isEmpty())
      throw IgrpResponseStatusException.badRequest("O ficheiro não tem movimentos para importar");

    importacaoMovimentoEntityRepository.saveAll(movimentos);
  }

  private FuncionarioEntity parseFuncionario(String uuid, List<String> erros) {
    try {
      var funcionario = funcionarioEntityRepository.findByUuid(UUID.fromString(uuid));
      if (funcionario.isEmpty())
        erros.add("colaborador não encontrado (" + uuid + ")");
      return funcionario.orElse(null);
    } catch (IllegalArgumentException e) {
      erros.add("identificador do colaborador inválido (" + uuid + ")");
      return null;
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

    if (!Arrays.stream(EstadoValidacao.values()).map(EstadoValidacao::name).toList().contains(validation))
      throw IgrpResponseStatusException.badRequest("Invalid validation flag: %s".formatted(validation));

    if (validation.equals(EstadoValidacao.NAO.name())) {
      mov.setEstado(Estado.I.name());
      importacaoMovimentoEntityRepository.save(mov);
      return;
    }

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
