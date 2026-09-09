package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.dto.ImportarMapaFeriaResultDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AnoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FeriasMapaEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AnoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FeriasMapaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Importa o mapa de férias a partir do Excel-modelo do RH (spec 07/09/2026, secção "Importar Mapa").
 *
 * <p>O modelo traz uma linha por colaborador com <b>dois</b> períodos de férias possíveis
 * (Início/Fim Férias 1 e 2). Cada período preenchido dá origem a um registo em
 * {@code RH_T_FERIAS_MAPA}; as restantes colunas do ficheiro (nome, direcção, cargo, dias de
 * direito, saldos) são contexto para quem preenche e não têm destino na tabela.
 *
 * <p>Regra da spec: importar um mapa para um colaborador/ano que já tenha mapa <b>inactiva o
 * anterior</b> — os registos activos passam a {@code I} e só os importados ficam {@code A}.
 *
 * <p>A importação é tolerante por linha: uma linha inválida não aborta o ficheiro. Cada linha é
 * validada por inteiro (todos os problemas de uma vez, não só o primeiro) e o conjunto de erros do
 * ficheiro volta agregado numa só string em {@link ImportarMapaFeriaResultDTO#getErros()},
 * separados por "; ". Isto segue o padrão do processamento em lote da renovação de contrato — o RH
 * corrige o Excel e reimporta.
 */
@Service
@RequiredArgsConstructor
public class MapaFeriaExcelImportService {

  private static final Logger LOGGER = LoggerFactory.getLogger(MapaFeriaExcelImportService.class);

  /**
   * Colunas do modelo, por índice (0-based). O modelo é um layout fixo do RH, por isso lê-se por
   * posição; o cabeçalho é validado à cabeça para apanhar um ficheiro trocado antes de gravar seja
   * o que for.
   */
  private static final int COL_ID_COLABORADOR = 1;
  private static final int COL_INICIO_FERIAS_1 = 13;
  private static final int COL_FIM_FERIAS_1 = 14;
  private static final int COL_INICIO_FERIAS_2 = 16;
  private static final int COL_FIM_FERIAS_2 = 17;

  /** Texto esperado na coluna do id, para confirmar que é mesmo o modelo do mapa. */
  private static final String CABECALHO_ID = "ID";

  /** Todos os erros do ficheiro vão juntos numa só string, separados por isto. */
  private static final String SEPARADOR_ERROS = "; ";

  private static final DateTimeFormatter[] FORMATOS_DATA = {
      DateTimeFormatter.ofPattern("dd/MM/yyyy"),
      DateTimeFormatter.ofPattern("d/M/yyyy"),
      DateTimeFormatter.ofPattern("yyyy-MM-dd")
  };

  private final FeriasMapaEntityRepository feriasMapaRepository;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final AnoEntityRepository anoRepository;

  @Transactional
  public ImportarMapaFeriaResultDTO importar(MultipartFile ficheiro, Integer ano) {

    if (ficheiro == null || ficheiro.isEmpty())
      throw IgrpResponseStatusException.badRequest("Ficheiro do mapa de férias obrigatório");
    if (ano == null)
      throw IgrpResponseStatusException.badRequest("Ano de referência obrigatório");

    var anoEntity = anoRepository.findByAno(String.valueOf(ano))
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Ano de referência " + ano + " não encontrado em RH_T_ANO"));

    var erros = new ArrayList<String>();
    int linhasLidas = 0;
    int periodosImportados = 0;
    int colaboradores = 0;

    try (Workbook wb = WorkbookFactory.create(ficheiro.getInputStream())) {

      Sheet sheet = wb.getSheetAt(0);
      validarCabecalho(sheet);

      // Linha 0 é o cabeçalho do modelo.
      for (int i = 1; i <= sheet.getLastRowNum(); i++) {
        Row row = sheet.getRow(i);
        if (row == null || linhaVazia(row)) continue;

        linhasLidas++;
        int numeroLinha = i + 1; // como aparece no Excel

        // Valida a linha INTEIRA antes de gravar: recolhe todos os problemas de uma vez, para o RH
        // não ter de reimportar o ficheiro a descobrir um erro de cada vez.
        var errosLinha = new ArrayList<String>();
        var periodos = validarLinha(row, errosLinha);

        if (!errosLinha.isEmpty()) {
          errosLinha.forEach(e -> erros.add("Linha " + numeroLinha + ": " + e));
          continue;
        }

        try {
          periodosImportados += gravarLinha(row, anoEntity, periodos);
          colaboradores++;
        } catch (Exception e) {
          erros.add("Linha " + numeroLinha + ": " + e.getMessage());
        }
      }

    } catch (IgrpResponseStatusException e) {
      throw e;
    } catch (Exception e) {
      LOGGER.error("Falha a ler o ficheiro do mapa de férias", e);
      throw IgrpResponseStatusException.badRequest(
          "Não foi possível ler o ficheiro: " + e.getMessage());
    }

    LOGGER.info("Importação do mapa de férias {}: {} linhas, {} colaboradores, {} períodos, {} erros",
        ano, linhasLidas, colaboradores, periodosImportados, erros.size());

    var resultado = new ImportarMapaFeriaResultDTO();
    resultado.setAno(ano);
    resultado.setLinhasLidas(linhasLidas);
    resultado.setColaboradoresImportados(colaboradores);
    resultado.setPeriodosImportados(periodosImportados);
    resultado.setErros(String.join(SEPARADOR_ERROS, erros));
    return resultado;
  }

  /**
   * Valida a linha toda e devolve os períodos aproveitáveis. Acumula tudo o que estiver mal em
   * {@code erros} em vez de parar no primeiro problema — o colaborador pode não existir E as datas
   * estarem trocadas, e o RH tem direito a saber as duas coisas de uma vez.
   */
  private List<LocalDate[]> validarLinha(Row row, List<String> erros) {

    Long idColaborador = null;
    try {
      idColaborador = lerLong(row.getCell(COL_ID_COLABORADOR));
      if (idColaborador == null) {
        erros.add("ID do colaborador em falta");
      } else if (!funcionarioRepository.existsById(idColaborador)) {
        erros.add("Colaborador " + idColaborador + " não encontrado");
      }
    } catch (IllegalArgumentException e) {
      erros.add(e.getMessage());
    }

    var periodos = new ArrayList<LocalDate[]>();
    validarPeriodo(periodos, erros, row, COL_INICIO_FERIAS_1, COL_FIM_FERIAS_1, "1");
    validarPeriodo(periodos, erros, row, COL_INICIO_FERIAS_2, COL_FIM_FERIAS_2, "2");

    if (periodos.isEmpty() && erros.isEmpty())
      erros.add("Nenhum período de férias preenchido");

    // Os dois períodos da mesma linha não podem colidir.
    if (periodos.size() == 2) {
      var p1 = periodos.get(0);
      var p2 = periodos.get(1);
      if (!p1[1].isBefore(p2[0]) && !p2[1].isBefore(p1[0]))
        erros.add("Férias 1 e 2 sobrepõem-se");
    }

    return periodos;
  }

  /**
   * Um período só conta se tiver as duas datas — {@code DATA_FIM} é NOT NULL na tabela, por isso um
   * início sem fim é erro de preenchimento e não um período de um dia.
   */
  private void validarPeriodo(List<LocalDate[]> periodos, List<String> erros, Row row,
      int colInicio, int colFim, String ordinal) {

    LocalDate inicio = null;
    LocalDate fim = null;

    try {
      inicio = lerData(row.getCell(colInicio));
    } catch (IllegalArgumentException e) {
      erros.add("Férias " + ordinal + " (início): " + e.getMessage());
    }
    try {
      fim = lerData(row.getCell(colFim));
    } catch (IllegalArgumentException e) {
      erros.add("Férias " + ordinal + " (fim): " + e.getMessage());
    }

    if (inicio == null && fim == null) return;

    if (inicio == null || fim == null) {
      erros.add("Férias " + ordinal + ": é preciso preencher início e fim");
      return;
    }

    if (fim.isBefore(inicio)) {
      erros.add("Férias " + ordinal + ": data de fim anterior à data de início");
      return;
    }

    periodos.add(new LocalDate[] {inicio, fim});
  }

  /** Grava os períodos já validados. Devolve quantos períodos foram criados. */
  private int gravarLinha(Row row, AnoEntity anoEntity, List<LocalDate[]> periodos) {

    var idColaborador = lerLong(row.getCell(COL_ID_COLABORADOR));
    var funcionario = funcionarioRepository.findById(idColaborador)
        .orElseThrow(() -> new IllegalArgumentException(
            "Colaborador " + idColaborador + " não encontrado"));

    // Inactiva o mapa anterior deste colaborador/ano antes de gravar o novo.
    var activos = feriasMapaRepository.findByFunId_IdAndAnoId_IdAndEstado(
        funcionario.getId(), anoEntity.getId(), Estado.A);
    activos.forEach(m -> m.setEstado(Estado.I));
    if (!activos.isEmpty()) feriasMapaRepository.saveAll(activos);

    for (var periodo : periodos) {
      var mapa = new FeriasMapaEntity();
      mapa.setFunId(funcionario);
      mapa.setAnoId(anoEntity);
      mapa.setDataInicio(periodo[0]);
      mapa.setDataFim(periodo[1]);
      mapa.setEstado(Estado.A);
      mapa.setUuid(UuidCreator.getTimeOrderedEpoch());
      feriasMapaRepository.save(mapa);
    }

    return periodos.size();
  }

  private void validarCabecalho(Sheet sheet) {
    Row cabecalho = sheet.getRow(0);
    var valor = cabecalho != null ? lerTexto(cabecalho.getCell(COL_ID_COLABORADOR)) : null;
    if (valor == null || !CABECALHO_ID.equalsIgnoreCase(valor.trim()))
      throw IgrpResponseStatusException.badRequest(
          "O ficheiro não corresponde ao modelo do mapa de férias "
              + "(esperava-se \"ID\" na segunda coluna do cabeçalho)");
  }

  private boolean linhaVazia(Row row) {
    for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
      var cell = row.getCell(c);
      if (cell != null && cell.getCellType() != CellType.BLANK) {
        var texto = lerTexto(cell);
        if (texto != null && !texto.isBlank()) return false;
      }
    }
    return true;
  }

  private Long lerLong(Cell cell) {
    if (cell == null) return null;
    if (cell.getCellType() == CellType.NUMERIC) return (long) cell.getNumericCellValue();
    var texto = lerTexto(cell);
    if (texto == null || texto.isBlank()) return null;
    try {
      return Long.parseLong(texto.trim());
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("ID do colaborador inválido: " + texto);
    }
  }

  private LocalDate lerData(Cell cell) {
    if (cell == null || cell.getCellType() == CellType.BLANK) return null;

    if (cell.getCellType() == CellType.NUMERIC) {
      if (!DateUtil.isCellDateFormatted(cell)) return null;
      return cell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    var texto = lerTexto(cell);
    if (texto == null || texto.isBlank()) return null;

    for (var formato : FORMATOS_DATA) {
      try {
        return LocalDate.parse(texto.trim(), formato);
      } catch (Exception ignored) {
        // tenta o formato seguinte
      }
    }
    throw new IllegalArgumentException("Data inválida: \"" + texto + "\" (use dd/MM/aaaa)");
  }

  private String lerTexto(Cell cell) {
    if (cell == null) return null;
    return switch (cell.getCellType()) {
      case STRING -> cell.getStringCellValue();
      case NUMERIC -> DateUtil.isCellDateFormatted(cell)
          ? cell.getDateCellValue().toString()
          : String.valueOf((long) cell.getNumericCellValue());
      case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
      case FORMULA -> cell.getCellFormula();
      default -> null;
    };
  }
}
