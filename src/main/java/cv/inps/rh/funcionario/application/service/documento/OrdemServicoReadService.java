package cv.inps.rh.funcionario.application.service.documento;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.OrdemServicoListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListOrdemServicoDTO;
import cv.inps.rh.funcionario.application.queries.GetListOrdemServicoQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import cv.inps.rh.shared.application.service.OrdemServicoPdfService;
import cv.inps.rh.shared.domain.service.OrdemServicoService;
import cv.inps.rh.shared.domain.service.model.OrdemServico;
import cv.inps.rh.shared.infrastructure.persistence.entity.OrdemServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.OrdemServicoEntityRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrdemServicoReadService {

  private final OrdemServicoEntityRepository ordemServicoRepository;
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
      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
    Page<OrdemServicoEntity> page = ordemServicoRepository.findAll(spec, pageable);

    var content = page.getContent().stream()
        .map(this::toDTO)
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
  public String gerarPdf(String osUuid) {
    var os = ordemServicoRepository.findByUuidOrThrow(UUID.fromString(osUuid));
    var tipo = OrdemServico.valueOf(os.getReferente());
    var funcionarioUuid = os.getFunId().getUuid().toString();

    var htmlDTO = ordemServicoService.content(tipo, funcionarioUuid);
    var context = ordemServicoService.generate(tipo, htmlDTO.getHtml());

    return ordemServicoPdfService.generate(context);
  }

  @Transactional
  public void anexar(String osUuid, AnexoReqDTO anexo) {
    var os = ordemServicoRepository.findByUuidOrThrow(UUID.fromString(osUuid));
    var funcionario = os.getFunId();

    var documento = documentoMapper.toEntity(
        anexo,
        Estado.A,
        Referencia.ORDEM_SERVICO.name(),
        os.getId(),
        os.getUuid(),
        os.getId(),
        funcionario
    );
    documento.setUuid(UuidCreator.getTimeOrderedEpoch());
    documento.setFunId(funcionario);
    documentoEntityRepository.save(documento);
  }

  private OrdemServicoListDTO toDTO(OrdemServicoEntity os) {
    var dto = new OrdemServicoListDTO();
    dto.setId(os.getId());
    dto.setUuid(os.getUuid());
    dto.setDescricao(os.getDescricao());
    dto.setReferente(os.getReferente());
    dto.setNuOrdem(os.getNuOrdem());
    dto.setEstado(os.getEstado() != null ? os.getEstado().name() : null);
    return dto;
  }

}
