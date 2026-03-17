package cv.inps.rh.funcionario.application.service.declaracao;

import cv.inps.rh.funcionario.application.dto.PedidoDeclaracaoResponseDTO;
import cv.inps.rh.funcionario.application.queries.GetPedidoDeclaracoesQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.PedidoDeclaracaoMapper;
import cv.inps.rh.funcionario.application.dto.WrapperListaPedidoDeclaracaoDTO;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import cv.inps.rh.shared.application.dto.AnexoRespDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.DeclaracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DeclaracaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ResponsavelEntityRepository;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PedidoDeclaracaoReadService {

  private final DeclaracaoEntityRepository declaracaoRepository;
  private final PedidoDeclaracaoMapper pedidoDeclaracaoMapper;
  private final DocumentoEntityRepository documentoEntityRepository;

  @Transactional(readOnly = true)
  public WrapperListaPedidoDeclaracaoDTO findAll(GetPedidoDeclaracoesQuery query) {
    Pageable pageable = PageRequest.of(
        Integer.parseInt(query.getPageNumber()),
        Integer.parseInt(query.getPageSize()),
        Sort.by(Sort.Direction.DESC, "dataPedido"));

    Specification<DeclaracaoEntity> spec = (root, criteriaQuery, cb) -> {
      // Evitar N+1 fetches
      root.fetch("pedidoId", JoinType.LEFT).fetch("funId", JoinType.LEFT);

      List<Predicate> predicates = new ArrayList<>();

      if (StringUtils.hasText(query.getTipoDeclaracao())) {
        predicates.add(cb.equal(root.get("tipoDeclaracao"), query.getTipoDeclaracao()));
      }

      if (StringUtils.hasText(query.getDataPedidoDe())) {
        LocalDate dataDe = LocalDate.parse(query.getDataPedidoDe(), DateTimeFormatter.ISO_LOCAL_DATE);
        predicates.add(cb.greaterThanOrEqualTo(root.get("dataPedido"), dataDe));
      }

      if (StringUtils.hasText(query.getDataPedidoAte())) {
        LocalDate dataAte = LocalDate.parse(query.getDataPedidoAte(), DateTimeFormatter.ISO_LOCAL_DATE);
        predicates.add(cb.lessThanOrEqualTo(root.get("dataPedido"), dataAte));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Page<DeclaracaoEntity> page = declaracaoRepository.findAll(spec, pageable);

    WrapperListaPedidoDeclaracaoDTO response = new WrapperListaPedidoDeclaracaoDTO();
    response.setContent(page.getContent().stream().map(pedidoDeclaracaoMapper::toDto).collect(Collectors.toList()));
    response.setTotalPages(page.getTotalPages());
    response.setTotalElements(page.getTotalElements());
    response.setPageNumber(page.getNumber());
    response.setPageSize(page.getSize());

    return response;
  }

  @Transactional(readOnly = true)
  public PedidoDeclaracaoResponseDTO findById(String id) {
    var uuid = UUID.fromString(id);
    DeclaracaoEntity entity = declaracaoRepository.findByUuid(uuid).orElseThrow(
        () -> IgrpResponseStatusException.notFound("Declaracao not found for id: " + id)
    );
    var pedidoDeclaracao = pedidoDeclaracaoMapper.toResponseDto(entity);

    var documentos = documentoEntityRepository
        .findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_DECLARACAO.name(), entity.getUuid());



    if (!CollectionUtils.isEmpty(documentos)) {
      pedidoDeclaracao.setAnexos(documentos.stream().map(d -> {
        var anexo = new AnexoRespDTO();
        anexo.setId(d.getId() != null ? d.getId() : null);
        anexo.setTipoDocumentoId(d.getTpDocumentoId() != null ? d.getTpDocumentoId().getId() : null);
        anexo.setTipoDocumentoDesc(d.getTpDocumentoId() != null ? d.getTpDocumentoId().getNome() : null);
        anexo.setDocumento(d.getUrl());
        return anexo;
      }).collect(Collectors.toList()));
    }

    return pedidoDeclaracao;
  }
}
