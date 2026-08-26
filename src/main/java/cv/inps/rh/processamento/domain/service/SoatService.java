package cv.inps.rh.processamento.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.services.model.WrapperListDTO;
import cv.inps.rh.processamento.application.constants.SoatStatus;
import cv.inps.rh.processamento.application.dto.*;
import cv.inps.rh.processamento.domain.service.model.SoatAggregateDTO;
import cv.inps.rh.processamento.domain.service.model.SoatPdfResult;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DadosApoliceEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DadosInstituicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SoatDetalheEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SoatEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DadosApoliceEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DadosInstituicaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SoatDetalheEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.SoatEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import cv.inps.rh.shared.util.PdfGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SoatService {

  private static final String YEAR_MONTH_PATTERN_FORMATTER = "%d%02d";

  private final SoatEntityRepository soatRepository;
  private final DadosInstituicaoEntityRepository dadosInstituicaoRepository;
  private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
  private final DadosApoliceEntityRepository dadosApoliceRepository;
  private final SoatDetalheEntityRepository soatDetalheRepository;
  private final EntityManager entityManager;
  private final PdfGenerator pdfGenerator;

  @Transactional(readOnly = true)
  public WrapperListDTO list(Integer anoReferente, Integer mesReferente, Integer page, Integer size) {

    var data = soatRepository.findSoatPage(anoReferente, mesReferente, PageRequest.of(page, size));

    var ids = data.getContent().stream().map(SoatEntity::getId).toList();

    Map<Long, SoatAggregateDTO> agregadosPorId = ids.isEmpty()
        ? Map.of()
        : soatRepository.findAgregadosByIds(ids).stream()
        .collect(Collectors.toMap(SoatAggregateDTO::soatId, a -> a));

    var soatStatus = SoatStatus.codeDescriptionMap();

    var resultPage = data.map(s -> {

          var aggregate = agregadosPorId.getOrDefault(
              s.getId(), new SoatAggregateDTO(s.getId(), BigDecimal.ZERO, 0L)
          );

          return new SoapRowResponseDTO(
              s.getUuid(),
              YEAR_MONTH_PATTERN_FORMATTER.formatted(s.getAnoReferente(), s.getMesReferente()),
              s.getCreatedDate(),
              aggregate.totalRemuneracao(),
              aggregate.totalColaboradores(),
              soatStatus.getOrDefault(s.getEstado(), s.getEstado())
          );
        })
        .stream()
        .toList();

    var response = new WrapperListDTO();
    PageMapper.fillPagination(data, response);
    response.setContent(resultPage);

    return response;
  }

  public void finalizarSoat(String uuid) {
    var soat = soatRepository.findByUuidOrThrow(uuid);
    soat.setEstado(SoatStatus.F.getCode());
    soatRepository.save(soat);
  }

  @Transactional
  public void criarSoat(Integer ano, Integer mes) {
    entityManager.createStoredProcedureQuery("RH_PK_GERA_SOAT_DB.REGISTAR_LISTA_SOAT")
        .registerStoredProcedureParameter("P_ANO", Integer.class, ParameterMode.IN)
        .registerStoredProcedureParameter("P_MES", String.class, ParameterMode.IN)
        .setParameter("P_ANO", ano)
        .setParameter("P_MES", "%02d".formatted(mes))
        .execute();
  }

  @Transactional
  public synchronized DadosInstituicaoResponseDTO salvarDadosInstituicao(DadosInstituicaoRequestDTO request) {

    dadosInstituicaoRepository.findFirstByEstadoOrderByIdDesc(Estado.A.getCode())
        .ifPresent(atual -> {
          atual.setEstado(Estado.I.getCode());
          dadosInstituicaoRepository.saveAndFlush(atual);
        });

    var novo = new DadosInstituicaoEntity();
    novo.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
    novo.setEstado(Estado.A.getCode());
    apply(request, novo);

    return toResponse(dadosInstituicaoRepository.save(novo));
  }

  private void apply(DadosInstituicaoRequestDTO request, DadosInstituicaoEntity entity) {
    entity.setNome(request.getNome());
    entity.setNif(request.getNif());
    entity.setCodCae(request.getCodCae());
    entity.setAtividadeEconomica(request.getAtividadeEconomica());
    entity.setNumCertidaoComercial(request.getNumCertidaoComercial());
    entity.setDataValidade(request.getDataValidade());
    entity.setTelefone(request.getTelefone());
    entity.setLocalidade(request.getLocalidade());
    entity.setEmail(request.getEmail());
    entity.setMorada(request.getMorada());
    entity.setConcelhoId(request.getConcelhoId());
  }

  @Transactional(readOnly = true)
  public DadosInstituicaoResponseDTO obterDadosInstituicaoAtual() {
    return dadosInstituicaoRepository.findFirstByEstadoOrderByIdDesc(Estado.A.getCode())
        .map(this::toResponse)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Active institution data not found"));
  }

  @Transactional(readOnly = true)
  public WrapperListDTO getDadosApoliceAtivos(Integer page, Integer size) {

    var pageable = PageRequest.of(page, size);

    var data = dadosApoliceRepository.findAllByEstado(Estado.A.getCode(), pageable);

    var response = new WrapperListDTO();
    PageMapper.fillPagination(data, response);
    response.setContent(
        data.getContent()
            .stream()
            .map(this::toResponse)
            .toList()
    );

    return response;
  }

  @Transactional(readOnly = true)
  public SoatPdfResult gerarFicheiroSoat(String soatUuid, String apoliceUuid) {

    /*var soat = soatRepository.findByUuidOrThrow(soatUuid);

    var instituicao = dadosInstituicaoRepository
        .findFirstByEstadoOrderByIdDesc(Estado.A.getCode())
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Active institution data not found"));

    var apolice = dadosApoliceRepository
        .findFirstByUuidAndEstadoOrderByIdDesc(apoliceUuid, Estado.A.getCode())
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Active insurance policy not found for uuid: " + apoliceUuid));

    var pessoas = soatDetalheRepository.findAllBySoat_IdOrderByFun_NomeAsc(soat.getId())
        .stream()
        .map(this::toPdfRow)
        .toList();

    var massaSalarialAnual = pessoas.stream()
        .map(SoatPdfRowDTO::getRetribuicaoAnual)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    var pdfData = new SoatPdfDTO(
        DATE_FORMAT.format(LocalDate.now(ZoneId.systemDefault())),
        "%02d/%d".formatted(soat.getMesReferente(), soat.getAnoReferente()),
        apolice.getNumApolice(),
        formatDate(apolice.getDataApolice()),
        value(instituicao.getNome()),
        value(instituicao.getNif()),
        value(instituicao.getCodCae()),
        value(instituicao.getAtividadeEconomica()),
        value(instituicao.getNumCertidaoComercial()),
        formatDate(instituicao.getDataValidade()),
        value(instituicao.getTelefone()),
        value(instituicao.getTelemovel()),
        value(instituicao.getLocalidade()),
        value(instituicao.getEmail()),
        value(instituicao.getMorada()),
        value(instituicao.getConcelhoId()),
        pessoas.size(),
        massaSalarialAnual,
        pessoas
    );

    var bytes = pdfGenerator.generate("soat-pdf", Map.of("soat", pdfData));
    var filename = "soat-%d%02d-%s.pdf".formatted(
        soat.getAnoReferente(),
        soat.getMesReferente(),
        safeFilename(apolice.getNumApolice())
    );
    return new SoatPdfResult(filename, bytes);*/

    return null;
  }

  private SoatPdfRowDTO toPdfRow(SoatDetalheEntity detalhe) {

    var funcionario = detalhe.getFun();
    var tipoDocumento = funcionario.getTipoDocumentoId() == null
        ? ""
        : value(funcionario.getTipoDocumentoId().getNome());
    var tiprel = detalhe.getProcsal().getTiprel();
    var carreira = tiprel == null ? null : tiprel.getCarreiraId();
    var profissao = carreira == null || carreira.getCargoId() == null
        ? ""
        : value(carreira.getCargoId().getNome());
    if (profissao.isBlank() && carreira != null && carreira.getCarrPccsId() != null) {
      profissao = value(carreira.getCarrPccsId().getNome());
    }
    var tipoContrato = tiprel == null || tiprel.getContrVinculoId() == null
                       || tiprel.getContrVinculoId().getTpContratoId() == null
        ? ""
        : value(tiprel.getContrVinculoId().getTpContratoId().getNome());
    var retribuicao = Optional.ofNullable(detalhe.getVlRemunMan()).orElse(BigDecimal.ZERO);
    var diasTrabalho = Optional.ofNullable(detalhe.getNuTrabMan()).orElse(0L);

    return new SoatPdfRowDTO(
        value(funcionario.getNome()),
        tipoDocumento,
        value(funcionario.getNumDocumento()),
        "",
        value(funcionario.getNif()),
        formatDate(funcionario.getDataNascimento()),
        formatSexo(funcionario.getSexo()),
        "M",
        profissao,
        isAprendizOuEstagiario(tipoContrato) ? "Sim" : "Não",
        BigDecimal.valueOf(44L)
            .multiply(BigDecimal.valueOf(diasTrabalho))
            .divide(BigDecimal.valueOf(30L), 2, RoundingMode.HALF_UP),
        "M",
        retribuicao,
        retribuicao.multiply(BigDecimal.valueOf(12L)),
        "Não",
        value(detalhe.getObs())
    );
  }

  private boolean isAprendizOuEstagiario(String tipoContrato) {
    var normalized = tipoContrato.toLowerCase();
    return normalized.contains("aprendiz")
           || normalized.contains("estagi")
           || normalized.contains("eventual")
           || normalized.contains("tempor")
           || normalized.contains("pratic");
  }

  private String formatSexo(String sexo) {
    if ("M".equalsIgnoreCase(sexo)) {
      return "Masculino";
    }
    if ("F".equalsIgnoreCase(sexo)) {
      return "Feminino";
    }
    return value(sexo);
  }

  private String formatDate(LocalDate date) {
    return date == null ? "" : DATE_FORMAT.format(date);
  }

  private String value(Object value) {
    return value == null ? "" : value.toString();
  }

  private String safeFilename(String value) {
    var sanitized = value == null ? "apolice" : value.replaceAll("[^a-zA-Z0-9._-]", "-");
    return sanitized.isBlank() ? "apolice" : sanitized;
  }

  private DadosInstituicaoResponseDTO toResponse(DadosInstituicaoEntity entity) {
    return new DadosInstituicaoResponseDTO(
        entity.getUuid(),
        entity.getNome(),
        entity.getNif(),
        entity.getCodCae(),
        entity.getAtividadeEconomica(),
        entity.getNumCertidaoComercial(),
        entity.getDataValidade(),
        entity.getTelefone(),
        entity.getLocalidade(),
        entity.getEmail(),
        entity.getMorada(),
        entity.getConcelhoId(),
        entity.getEstado()
    );
  }

  private DadosApoliceResponseDTO toResponse(DadosApoliceEntity entity) {
    return new DadosApoliceResponseDTO(
        entity.getId(),
        entity.getDadosInstituicao().getUuid(), // TODO 26/08/2026 21:11 improve performance of this
        entity.getNumApolice(),
        entity.getIlhaId(),
        entity.getDataApolice(),
        entity.getEstado()
    );
  }

}
