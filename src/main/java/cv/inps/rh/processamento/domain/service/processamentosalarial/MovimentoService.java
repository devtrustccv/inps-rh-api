package cv.inps.rh.processamento.domain.service.processamentosalarial;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.application.service.helper.TipoRelRemPagHelper;
import cv.inps.rh.processamento.application.dto.MovRowDTO;
import cv.inps.rh.processamento.application.dto.MovimentosImportadosDTO;
import cv.inps.rh.processamento.application.dto.ValidacaoMovimentoImportadoDTO;
import cv.inps.rh.processamento.application.queries.GetMovimentosImportadosQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
  private final DefPagamentoEntityRepository defPagamentoEntityRepository;
  private final FuncionarioRules funcionarioRules;
  private final TipoRelRemPagHelper tipoRelRemPagHelper;
  private final ValidacaoEntityRepository validacaoEntityRepository;

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
        var tpMovRetencao = parseTipoMovimento(formatter.formatCellValue(row.getCell(2)), "Mov. Retenção", errosLinha);
        var tpMovRem = parseTipoMovimento(formatter.formatCellValue(row.getCell(3)), "Mov. Remuneração", errosLinha);
        var situacao = formatter.formatCellValue(row.getCell(8));
        if (situacao.length() > 50)   // RH_T_IMPORTACAO_MOVIMENTO.SITUACAO é VARCHAR2(50)
          errosLinha.add("Situação com mais de 50 caracteres");
        if (dataInicio != null && dataFim != null && dataFim.isBefore(dataInicio))
          errosLinha.add("Data Fim anterior à Data Início");

        if (!errosLinha.isEmpty()) {
          erros.add("Linha %d: %s".formatted(linha, String.join("; ", errosLinha)));
          continue;
        }

        var mov = new ImportacaoMovimentoEntity();
        mov.setUuid(UuidCreator.getTimeOrderedEpoch());
        mov.setFuncionario(funcionario);
        mov.setTpMovRetencao(tpMovRetencao);
        mov.setTpMovRem(tpMovRem);
        mov.setPercentagem(percentagem);
        mov.setValor(valor);
        mov.setDataInicio(dataInicio);
        mov.setDataFim(dataFim);
        mov.setSituacao(situacao);
        mov.setNomeFicheiro(fileName);
        mov.setNomeFuncionario(StringUtils.hasText(formatter.formatCellValue(row.getCell(1)))
            ? formatter.formatCellValue(row.getCell(1)).trim() : null);
        // Vínculo do colaborador no momento da importação ("Vínculo (Situação)" da lista); sem contrato
        // atual fica vazio — a validação continua a usar o contrato atual nesse momento (spec: est_act_adm=1).
        mov.setTiprel(funcionarioRules.getTipoRelacionamentoAtualOrNull(funcionario.getUuid()));
        // Spec PROCESSAMENTO SALARIAL (Importação Movimentos): importado fica P; a validação passa a A/I.
        mov.setEstado(Estado.P.name());
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

    // Spec: CODIGO = identificador do ficheiro, igual em todas as linhas; uma RH_T_VALIDACAO por ficheiro
    // (REFERENCIA_ID = CODIGO, sem colaborador nem vínculo).
    Long codigo = importacaoMovimentoEntityRepository.nextCodigo();
    movimentos.forEach(m -> m.setCodigo(codigo));
    importacaoMovimentoEntityRepository.saveAll(movimentos);

    var validacao = new ValidacaoEntity();
    validacao.setTipoAccao(TipoAcao.INSERT.name());
    validacao.setReferenciaName(Referencia.IMPORTACAO_MOVIMENTO.name());
    validacao.setReferenciaId(codigo);
    validacao.setEstado(Estado.P);
    validacao.setUuid(UuidCreator.getTimeOrderedEpoch());
    validacao.setObs("Ficheiro: " + fileName);
    validacaoEntityRepository.save(validacao);
  }

  /**
   * A validação do ficheiro fica P enquanto houver linhas por decidir e passa a A quando todas estiverem
   * decididas (validadas ou rejeitadas) — a decisão é linha a linha, a RH_T_VALIDACAO é uma por ficheiro.
   */
  private void fecharValidacaoSeDecidido(Long codigo) {
    if (codigo == null || importacaoMovimentoEntityRepository.countByCodigoAndEstado(codigo, Estado.P.name()) > 0)
      return;
    validacaoEntityRepository.findFirstByReferenciaNameAndReferenciaIdAndEstado(
            Referencia.IMPORTACAO_MOVIMENTO.name(), codigo, Estado.P)
        .ifPresent(v -> {
          v.setEstado(Estado.A);
          validacaoEntityRepository.save(v);
        });
  }

  /**
   * Coluna A do ficheiro: número de colaborador (RH_T_FUNCIONARIOS.ID_COLABORADOR) — é o que o ficheiro
   * de ginásio/cantina traz (spec: "Num_fun"). O número não é único na BD: se der mais de um colaborador,
   * a linha dá erro em vez de escolher um.
   */
  private FuncionarioEntity parseFuncionario(String numero, List<String> erros) {
    long idColaborador;
    try {
      idColaborador = new BigDecimal(numero.trim()).longValueExact();
    } catch (NumberFormatException | ArithmeticException e) {
      erros.add("número de colaborador inválido (" + numero + ")");
      return null;
    }
    var funcionarios = funcionarioEntityRepository.findAllByIdColaborador(idColaborador);
    if (funcionarios.isEmpty()) {
      erros.add("colaborador não encontrado (número " + idColaborador + ")");
      return null;
    }
    if (funcionarios.size() > 1) {
      erros.add("o número de colaborador " + idColaborador + " corresponde a " + funcionarios.size() + " colaboradores");
      return null;
    }
    return funcionarios.getFirst();
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

    // Nome dos tipos de movimento da página (spec: INPSSIGOF.RH_TIPOS_MOVIMENTO.NOME), numa só consulta.
    var idsTipoMovimento = importsSaved.stream()
        .flatMap(m -> java.util.stream.Stream.of(m.getTpMovRetencao(), m.getTpMovRem()))
        .filter(Objects::nonNull)
        .distinct()
        .toList();
    Map<Long, String> nomeTipoMovimento = new HashMap<>();
    tipoMovimentoEntityRepository.findAllById(idsTipoMovimento)
        .forEach(tm -> nomeTipoMovimento.put(tm.getId(), tm.getDescricao()));

    var response = new MovimentosImportadosDTO();
    PageMapper.fillPagination(importsSaved, response);
    response.setContent(importsSaved.stream()
        .map(obj -> {
          var movRowDTO = new MovRowDTO();
          // O validar (POST colaborador/movimentos/{movimentoId}/validar) procura pelo uuid; com o id
          // numérico o front levava 400 "Invalid UUID string".
          movRowDTO.setMovimentoId(obj.getUuid().toString());
          movRowDTO.setNomeFicheiro(obj.getNomeFicheiro());
          movRowDTO.setMovimentoRetencaoDesc(nomeTipoMovimento.get(obj.getTpMovRetencao()));
          movRowDTO.setMovimentoRemuneracaoDesc(nomeTipoMovimento.get(obj.getTpMovRem()));
          movRowDTO.setFuncionarioId(obj.getFuncionario().getId().toString());
          movRowDTO.setNomeFuncionario(obj.getFuncionario().getNome());
          // O contrato com o front continua texto (o id do tipo de movimento).
          movRowDTO.setMovimentoRetencao(Objects.toString(obj.getTpMovRetencao(), null));
          movRowDTO.setMovimentoRemuneracao(Objects.toString(obj.getTpMovRem(), null));
          movRowDTO.setPercentagem(obj.getPercentagem());
          movRowDTO.setValor(obj.getValor());
          movRowDTO.setDataInicio(obj.getDataInicio());
          movRowDTO.setDataFim(obj.getDataFim());
          movRowDTO.setSituacao(obj.getSituacao());
          movRowDTO.setCodigo(obj.getCodigo());
          movRowDTO.setVinculoId(obj.getTiprel() != null ? obj.getTiprel().getId() : null);
          movRowDTO.setEstado(obj.getEstado());
          movRowDTO.setEstadoDesc(descricaoEstado(obj.getEstado()));
          return movRowDTO;
        })
        .toList());

    return response;
  }

  private static String descricaoEstado(String estado) {
    if (!StringUtils.hasText(estado))
      return null;
    try {
      return Estado.valueOf(estado).getDescription();
    } catch (IllegalArgumentException e) {
      return estado;
    }
  }

  /**
   * Id do tipo de movimento vindo do ficheiro ou do formulário (TP_MOV_* é NUMBER na BD): vazio → null;
   * não inteiro → erro, em vez de deixar a BD rebentar ao gravar.
   */
  private static Long parseTipoMovimento(String valor, String campo, List<String> erros) {
    if (!StringUtils.hasText(valor))
      return null;
    try {
      return new BigDecimal(valor.trim()).longValueExact();
    } catch (NumberFormatException | ArithmeticException e) {
      erros.add(campo + " inválido (" + valor.trim() + ")");
      return null;
    }
  }

  public void validarMovimento(String movimentoId, ValidacaoMovimentoImportadoDTO data) {

    var mov = importacaoMovimentoEntityRepository.findByUuidOrThrow(UUID.fromString(movimentoId));
    // Só se decide uma vez: validar de novo criava outra vez as definições e as ligações ao vínculo.
    if (!Estado.P.name().equals(mov.getEstado()))
      throw IgrpResponseStatusException.badRequest(
          "Este movimento já foi %s.".formatted(Estado.I.name().equals(mov.getEstado()) ? "rejeitado" : "validado"));
    var erros = new ArrayList<String>();
    var tpMovRetencao = parseTipoMovimento(data.getMovimentoRetencao(), "Mov. Retenção", erros);
    var tpMovRem = parseTipoMovimento(data.getMovimentoRemuneracao(), "Mov. Remuneração", erros);
    if (!erros.isEmpty())
      throw IgrpResponseStatusException.badRequest(String.join("; ", erros));
    mov.setTpMovRetencao(tpMovRetencao);
    mov.setTpMovRem(tpMovRem);
    mov.setPercentagem(data.getPercentagem());
    mov.setValor(data.getValor());
    mov.setDataInicio(data.getDataInicio());
    mov.setDataFim(data.getDataFim());
    mov.setSituacao(data.getSituacao());
    importacaoMovimentoEntityRepository.save(mov);

    var fun = mov.getFuncionario();
    var currentRelation = funcionarioRules.getTipoRelacionamentoAtual(fun.getUuid());
    var validation = data.getValidacao();

    // Só SIM ou NAO: a importação não tem ciclo de correção, e CORRIGIR caía no ramo do SIM.
    if (!EstadoValidacao.SIM.name().equals(validation) && !EstadoValidacao.NAO.name().equals(validation))
      throw IgrpResponseStatusException.badRequest("Invalid validation flag: %s".formatted(validation));

    if (validation.equals(EstadoValidacao.NAO.name())) {
      mov.setEstado(Estado.I.name());
      importacaoMovimentoEntityRepository.save(mov);
      fecharValidacaoSeDecidido(mov.getCodigo());
      return;
    }

    mov.setEstado(Estado.A.name());
    importacaoMovimentoEntityRepository.save(mov);
    fecharValidacaoSeDecidido(mov.getCodigo());

    if (mov.getTpMovRem() != null) {

      var remunerationDefinition = new DefinicaoRemuneracaoEntity();
      remunerationDefinition.setPercentagem(mov.getPercentagem());
      remunerationDefinition.setValor(mov.getValor());
      remunerationDefinition.setEstado(Estado.A);
      remunerationDefinition.setObs("Registo referente ao ficheiro:" + mov.getNomeFicheiro());
      remunerationDefinition.setUuid(UuidCreator.getTimeOrderedEpoch());
      remunerationDefinition.setTmId(tipoMovimentoEntityRepository.findByIdOrThrow(mov.getTpMovRem()));
      remunerationDefinition.setMoeda("CVE");
      remunerationDefinition.setDataInicio(mov.getDataInicio());
      remunerationDefinition.setDataFim(mov.getDataFim());
      remunerationDefinition.setFunId(fun);
      var saved = definicaoRemuneracaoEntityRepository.save(remunerationDefinition);
      tipoRelRemPagHelper.associarLista(currentRelation, List.of(saved), List.of());
    }

    if (mov.getTpMovRetencao() != null) {

      var paymentDefinition = new DefPagamentoEntity();
      paymentDefinition.setTmId(tipoMovimentoEntityRepository.findByIdOrThrow(mov.getTpMovRetencao()));
      paymentDefinition.setValor(mov.getValor());
      paymentDefinition.setDataInicio(mov.getDataInicio());
      paymentDefinition.setDataFim(mov.getDataFim());
      paymentDefinition.setEstado(Estado.A);
      paymentDefinition.setObs("Registo referente ao ficheiro:" + mov.getNomeFicheiro());
      paymentDefinition.setUuid(UuidCreator.getTimeOrderedEpoch());
      paymentDefinition.setPercentagem(mov.getPercentagem());
      paymentDefinition.setFunId(fun);
      var saved = defPagamentoEntityRepository.save(paymentDefinition);
      tipoRelRemPagHelper.associarLista(currentRelation, List.of(), List.of(saved));
    }
  }

}
