package cv.inps.rh.funcionario.application.service.documento;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.OrdemServicoItemReqDTO;
import cv.inps.rh.funcionario.application.dto.OrdemServicoListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListOrdemServicoDTO;
import cv.inps.rh.funcionario.application.queries.GetListOrdemServicoQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.service.OrdemServicoPdfService;
import cv.inps.rh.shared.domain.service.OrdemServicoService;
import cv.inps.rh.shared.domain.service.model.OrdemServico;
import cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.OrdemServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.OrdemServicoEntityRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrdemServicoReadService {

  private final OrdemServicoEntityRepository ordemServicoRepository;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final DocumentoEntityRepository documentoEntityRepository;
  private final DocumentoMapper documentoMapper;
  private final OrdemServicoService ordemServicoService;
  private final OrdemServicoPdfService ordemServicoPdfService;

  @Transactional(readOnly = true)
  public WrapperListOrdemServicoDTO listar(GetListOrdemServicoQuery query) {

    int pageNumber = query.getPageNumber() != null ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = query.getPageSize() != null ? Integer.parseInt(query.getPageSize()) : 20;

    var funcionarioUuid = UUID.fromString(query.getFuncionarioUuid());

    Specification<OrdemServicoEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      predicates.add(cb.equal(root.get("funId").get("uuid"), funcionarioUuid));
      predicates.add(cb.notEqual(root.get("estado"), Estado.E));
      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
    var page = ordemServicoRepository.findAll(spec, pageable);

    // Load attached documents for all OS records in one batch
    var osUuids = page.getContent().stream().map(OrdemServicoEntity::getUuid).toList();
    Map<UUID, String> docUrlByOsUuid = loadDocUrlsByOsUuids(osUuids);

    var content = page.getContent().stream()
        .map(os -> toDTO(os, docUrlByOsUuid.get(os.getUuid())))
        .toList();

    var wrapper = new WrapperListOrdemServicoDTO();
    wrapper.setContent(content);
    wrapper.setPageNumber(page.getNumber());
    wrapper.setPageSize(page.getSize());
    wrapper.setTotalElements(page.getTotalElements());
    wrapper.setTotalPages(page.getTotalPages());
    wrapper.setFirst(page.isFirst());
    wrapper.setLast(page.isLast());

    return wrapper;
  }

  @Transactional
  public void salvar(String funcionarioUuid, List<OrdemServicoItemReqDTO> items) {

    var funcionario = funcionarioRepository.findByUuidOrThrow(UUID.fromString(funcionarioUuid));
    var existentes = ordemServicoRepository.findByFunId_Uuid(UUID.fromString(funcionarioUuid));

    if (items == null) return;

    for (var item : items) {
      OrdemServicoEntity os = findOrCreate(existentes, item, funcionario);
      os = ordemServicoRepository.save(os);

      if (item.getDocumento() != null && StringUtils.hasText(item.getDocumento().getDocumento())) {
        saveOrUpdateDocument(os, item, funcionario);
      }
    }

    // Soft-delete removed items
    for (var existing : existentes) {
      boolean stillExists = items.stream()
          .anyMatch(i -> Objects.equals(i.getId(), existing.getId()));
      if (!stillExists && existing.getEstado() != Estado.E) {
        existing.setEstado(Estado.E);
        ordemServicoRepository.save(existing);
      }
    }
  }

  @Transactional(readOnly = true)
  public byte[] gerarPdf(String osUuid) {
    var os = ordemServicoRepository.findByUuidOrThrow(UUID.fromString(osUuid));
    var tipo = OrdemServico.valueOf(os.getReferente());
    var funcionarioUuid = os.getFunId().getUuid().toString();

    var htmlDTO = ordemServicoService.content(tipo, funcionarioUuid);
    var context = ordemServicoService.generate(tipo, htmlDTO.getHtml());

    return ordemServicoPdfService.generate(context);
  }

  private OrdemServicoEntity findOrCreate(List<OrdemServicoEntity> existentes,
                                          OrdemServicoItemReqDTO item,
                                          FuncionarioEntity funcionario) {
    if (item.getId() != null) {
      var found = existentes.stream()
          .filter(e -> Objects.equals(e.getId(), item.getId()))
          .findFirst()
          .orElse(null);
      if (found != null) {
        found.setDescricao(item.getDescricao());
        found.setReferente(item.getReferente());
        found.setNuOrdem(item.getNuOrdem());
        return found;
      }
    }

    var novo = new OrdemServicoEntity();
    novo.setFunId(funcionario);
    novo.setDescricao(item.getDescricao());
    novo.setReferente(item.getReferente());
    novo.setNuOrdem(item.getNuOrdem() != null ? item.getNuOrdem() : "1");
    novo.setEstado(Estado.A);
    novo.setUuid(UuidCreator.getTimeOrderedEpoch());
    return novo;
  }

  private void saveOrUpdateDocument(OrdemServicoEntity os,
                                    OrdemServicoItemReqDTO item,
                                    FuncionarioEntity funcionario) {
    var existing = documentoEntityRepository
        .findAllByReferenciaNameAndReferenciaUuid(Referencia.ORDEM_SERVICO.name(), os.getUuid())
        .stream()
        .filter(d -> d.getEstado() != Estado.E)
        .findFirst();

    DocumentoEntity doc;
    if (existing.isPresent()) {
      doc = existing.get();
      doc.setUrl(item.getDocumento().getDocumento());
      if (item.getDocumento().getTipoDocumentoId() != null) {
        doc.setTpDocumentoId(
            documentoMapper.toEntity(item.getDocumento(), Estado.A,
                Referencia.ORDEM_SERVICO.name(), os.getId(), os.getUuid(), os.getId(), funcionario)
                .getTpDocumentoId()
        );
      }
    } else {
      doc = documentoMapper.toEntity(
          item.getDocumento(),
          Estado.A,
          Referencia.ORDEM_SERVICO.name(),
          os.getId(),
          os.getUuid(),
          os.getId(),
          funcionario
      );
      doc.setUuid(UuidCreator.getTimeOrderedEpoch());
      doc.setFunId(funcionario);
    }
    documentoEntityRepository.save(doc);
  }

  private Map<UUID, String> loadDocUrlsByOsUuids(List<UUID> osUuids) {
    if (osUuids.isEmpty()) return Map.of();
    return osUuids.stream()
        .flatMap(uuid -> documentoEntityRepository
            .findAllByReferenciaNameAndReferenciaUuid(Referencia.ORDEM_SERVICO.name(), uuid)
            .stream()
            .filter(d -> d.getEstado() != Estado.E)
            .findFirst()
            .stream()
            .map(d -> Map.entry(uuid, d.getUrl()))
        )
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private OrdemServicoListDTO toDTO(OrdemServicoEntity os, String documentoUrl) {
    var dto = new OrdemServicoListDTO();
    dto.setId(os.getId());
    dto.setUuid(os.getUuid());
    dto.setDescricao(os.getDescricao());
    dto.setReferente(os.getReferente());
    dto.setNuOrdem(os.getNuOrdem());
    dto.setEstado(os.getEstado() != null ? os.getEstado().name() : null);
    dto.setDocumentoUrl(documentoUrl);
    return dto;
  }

}
