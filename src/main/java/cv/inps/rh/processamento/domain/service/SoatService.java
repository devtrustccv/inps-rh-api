package cv.inps.rh.processamento.domain.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.services.model.WrapperListDTO;
import cv.inps.rh.processamento.application.constants.SoatStatus;
import cv.inps.rh.processamento.application.dto.*;
import cv.inps.rh.processamento.domain.service.model.SoatAggregateDTO;
import cv.inps.rh.processamento.domain.service.model.SoatPdfResult;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DadosInstituicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SoatEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import cv.inps.rh.shared.util.PdfGenerator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Service
@RequiredArgsConstructor
public class SoatService {

  private static final String YEAR_MONTH_PATTERN_FORMATTER = "%d%02d";

  private final SoatEntityRepository soatRepository;
  private final DadosInstituicaoEntityRepository dadosInstituicaoRepository;
  private final DadosApoliceEntityRepository dadosApoliceRepository;
  private final SoatDetalheEntityRepository soatDetalheRepository;
  private final GeografiaEntityRepository geografiaEntityRepository;
  private final SoatViewEntityRepository soatViewEntityRepository;
  private final PdfGenerator pdfGenerator;
  private final EntityManager entityManager;

  @Transactional
  public void criarSoat(Integer ano, Integer mes) {
    entityManager.createStoredProcedureQuery("RH_PK_GERA_SOAT_DB.REGISTAR_LISTA_SOAT")
        .registerStoredProcedureParameter("P_ANO", Integer.class, ParameterMode.IN)
        .registerStoredProcedureParameter("P_MES", String.class, ParameterMode.IN)
        .setParameter("P_ANO", ano)
        .setParameter("P_MES", "%02d".formatted(mes))
        .execute();
  }

  @Transactional(readOnly = true)
  public WrapperListDTO listSoat(Integer anoReferente, Integer mesReferente, Integer page, Integer size) {

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
  public synchronized DadosInstituicaoResponseDTO salvarDadosInstituicao(DadosInstituicaoRequestDTO request) {

    dadosInstituicaoRepository.findFirstByEstadoOrderByIdDesc(Estado.A.getCode())
        .ifPresent(atual -> {
          atual.setEstado(Estado.I.getCode());
          dadosInstituicaoRepository.saveAndFlush(atual);
        });

    var novo = new DadosInstituicaoEntity();
    novo.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
    novo.setEstado(Estado.A.getCode());
    novo.setNome(request.getNome());
    novo.setNif(request.getNif());
    novo.setCodCae(request.getCodCae());
    novo.setAtividadeEconomica(request.getAtividadeEconomica());
    novo.setNumCertidaoComercial(request.getNumCertidaoComercial());
    novo.setDataValidade(request.getDataValidade());
    novo.setTelefone(request.getTelefone());
    novo.setLocalidade(request.getLocalidade());
    novo.setEmail(request.getEmail());
    novo.setMorada(request.getMorada());
    novo.setConcelhoId(request.getConcelhoId());

    return toResponse(dadosInstituicaoRepository.save(novo));
  }

  @Transactional(readOnly = true)
  public DadosInstituicaoResponseDTO obterDadosInstituicaoAtual() {
    return dadosInstituicaoRepository.findFirstByEstadoOrderByIdDesc(Estado.A.getCode())
        .map(this::toResponse)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("Active institution data not found"));
  }

  @Transactional(readOnly = true)
  public WrapperListDTO getDetalhesSoat(String soatUuid, Integer page, Integer size) {

    var data = soatDetalheRepository.findAllBySoatId(
        soatUuid,
        PageRequest.of(page, size)
    );

    var response = new WrapperListDTO();
    PageMapper.fillPagination(data, response);
    response.setContent(data.getContent());
    return response;
  }

  @Transactional(readOnly = true)
  public void updateDetalhesSoat(List<UpdateDetalheSoatRequestDTO> data) {
    for (var detail : data) {
      soatDetalheRepository.updateByUuid(
          detail.getDetalheSoatId(),
          detail.getDiasTrabalho(),
          detail.getRemuneracao()
      );
    }
  }

  @Transactional(readOnly = true)
  public WrapperListDTO getDadosApoliceAtivos(Integer page, Integer size) {

    var data = dadosApoliceRepository.findAllByEstado(
        Estado.A.getCode(),
        PageRequest.of(page, size)
    );

    var response = new WrapperListDTO();
    PageMapper.fillPagination(data, response);
    response.setContent(data.getContent());
    return response;
  }

  @Transactional(readOnly = true)
  public SoatPdfResult gerarFicheiroSoat(String soatUuid, Long dadosApoliceId) {

    var dadosApolice = dadosApoliceRepository.findById(dadosApoliceId).orElseThrow();
    var dadosInstituicao = dadosApolice.getDadosInstituicao();
    var soat = soatRepository.findByUuidOrThrow(soatUuid);

    var persons = soatViewEntityRepository.findBySoatUuid(soatUuid)
        .stream()
        .map(obj -> new SoatPdfRowDTO(
            obj.getNome(),
            obj.getTpDocumento(),
            obj.getNumDocumento(),
            DateFormatter.DATE.format(obj.getDataValidade()),
            obj.getNif(),
            DateFormatter.DATE.format(obj.getDataNascimento()),
            obj.getSexo(),
            obj.getSituacao(),
            obj.getCargoCarreira(),
            obj.getEstagiarioAprendiz(),
            obj.getDiasTrabSemana(),
            obj.getSalarioBase(),
            obj.getSalarioBase(),
            obj.getSalarioBaseAnual(),
            obj.getColabNoEstrangeiro(),
            obj.getObs()
        ))
        .toList();

    var phoneNumber = ofNullable(dadosInstituicao.getTelefone()).map(Objects::toString).orElse("");

    var annualSalary = persons.stream()
        .map(SoatPdfRowDTO::retribuicaoAnual)
        .filter(Objects::nonNull)
        .mapToLong(Long::longValue)
        .sum();

    var pdfData = new SoatPdfDTO(
        DateFormatter.DATE.format(LocalDate.now(ZoneId.systemDefault())),
        YEAR_MONTH_PATTERN_FORMATTER.formatted(soat.getAnoReferente(), soat.getMesReferente()),
        dadosApolice.getNumApolice(),
        DateFormatter.DATE.format(dadosApolice.getDataApolice()),
        dadosInstituicao.getNome(),
        ofNullable(dadosInstituicao.getNif()).map(Objects::toString).orElse(""),
        dadosInstituicao.getCodCae(),
        dadosInstituicao.getAtividadeEconomica(),
        dadosInstituicao.getNumCertidaoComercial(),
        DateFormatter.DATE.format(dadosInstituicao.getDataValidade()),
        phoneNumber,
        phoneNumber,
        dadosInstituicao.getLocalidade(),
        dadosInstituicao.getEmail(),
        dadosInstituicao.getMorada(),
        ofNullable(dadosInstituicao.getConcelhoId()).map(geografiaEntityRepository::getDescriptionById).orElse(""),
        persons.size(),
        annualSalary,
        persons
    );

    return new SoatPdfResult(
        "soat-%s.pdf".formatted(pdfData.referencia()),
        pdfGenerator.generate("soat", Map.of("soat", pdfData))
    );
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
}
