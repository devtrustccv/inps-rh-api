package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.dto.FeriasListDTO;
import cv.inps.rh.assiduidade.application.dto.PedidoFeriaAlterarReqDTO;
import cv.inps.rh.assiduidade.application.dto.PedidoFeriaReqDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaFeriaDTO;
import cv.inps.rh.assiduidade.application.queries.GetListaFeriaQuery;
import cv.inps.rh.assiduidade.application.queries.GetPedidoFeriaQuery;
import cv.inps.rh.shared.util.ValidationUtil;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.VFeriasMensalEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FeriaReadService {

  private final FeriasGozadasEntityRepository feriasGozadasEntityRepository;
  private final VFeriasMensalEntityRepository vFeriasMensalEntityRepository;
  private final DocumentoEntityRepository documentoEntityRepository;
  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final ResponsavelEntityRepository responsavelEntityRepository;


  @Transactional(readOnly = true)
  public WrapperListaFeriaDTO getListaFeria(GetListaFeriaQuery query) {
    int pageNumber = StringUtils.hasText(query.getPageNumber())
        ? Integer.parseInt(query.getPageNumber())
        : 0;
    int pageSize = StringUtils.hasText(query.getPageSize())
        ? Integer.parseInt(query.getPageSize())
        : 20;

    Specification<VFeriasMensalEntity> spec = buildSpec(query);

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
    Page<VFeriasMensalEntity> page = vFeriasMensalEntityRepository.findAll(spec, pageable);

    var content = page.getContent()
        .stream()
        .map(this::toDTO)
        .toList();

    var wrapper = new WrapperListaFeriaDTO();
    PageMapper.fillPagination(page, wrapper);
    wrapper.setContent(content);
    return wrapper;
  }

  private Specification<VFeriasMensalEntity> buildSpec(GetListaFeriaQuery query) {
    return (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      if (query.getAnoReferente() != null) {
        predicates.add(cb.equal(root.get("ano"), query.getAnoReferente()));
      }
      if (query.getIlha() != null) {
        predicates.add(cb.equal(root.get("ilhaId"), query.getIlha()));
      }
      if (query.getDirecao() != null) {
        predicates.add(cb.equal(root.get("direcaoId"), query.getDirecao()));
      }
      if (query.getSeccao() != null) {
        predicates.add(cb.equal(root.get("secaoId"), query.getSeccao()));
      }
      if (StringUtils.hasText(query.getColaborador())) {
        predicates
            .add(cb.like(cb.lower(root.get("nomeColaborador")), "%" + query.getColaborador().toLowerCase() + "%"));
      }

      if (StringUtils.hasText(query.getFuncionarioUuid())) {
        try {
          var funcUuid = java.util.UUID.fromString(query.getFuncionarioUuid());
          predicates.add(cb.equal(root.get("uuidFuncionario"), funcUuid));
        } catch (IllegalArgumentException ignored) {
          // Ignore invalid UUIDs
        }
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  private FeriasListDTO toDTO(VFeriasMensalEntity e) {
    var dto = new FeriasListDTO();
    dto.setPedidoId(e.getPedidoId());
    dto.setPedidoUuid(e.getUuidPedido() != null ? e.getUuidPedido().toString() : "");
    dto.setUuidFuncionario(e.getUuidFuncionario().toString());
    dto.setNomeColaborador(e.getNomeColaborador());
    dto.setDirecao(e.getDirecao());
    dto.setSecao(e.getSecao());
    dto.setVinculo(e.getVinculo());
    dto.setCategoria(e.getCategoria());
    dto.setTotalDireito(e.getTotalDireito());
    dto.setTotalDireitoAno(e.getTotalDireitoAno());
    dto.setTotalPlaneado(e.getTotalPlaneado());
    dto.setTotalGozado(e.getTotalGozado());
    dto.setAno(e.getAno());
    dto.setEstado(e.getEstado());
    dto.setEstadoDesc(e.getEstadoDesc());
    return dto;
  }


  @Transactional(readOnly = true)
  public PedidoFeriaAlterarReqDTO getPedidoFeria(GetPedidoFeriaQuery query) {
    // UuidCreator.fromString lançava IllegalArgumentException num id malformado, o que
    // saía como 500. Um identificador inválido é erro do cliente: 400.
    var pedidoUuid = ValidationUtil.parseUuid(query.getPedidoId(), "Identificador do pedido de férias");

    var entity = feriasGozadasEntityRepository.findByPedidoId_Uuid(pedidoUuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND,
            "Pedido de férias não encontrado: " + query.getPedidoId()));

    var req = new PedidoFeriaReqDTO();
    req.setColaborador(entity.getFunId() != null ? entity.getFunId().getUuid() : null);
    req.setColaboradorNome(entity.getFunId() != null ? entity.getFunId().getNome() : null);
    req.setDataInicio(entity.getDataInicio());
    req.setDataFim(entity.getDataFim());
    req.setNumDias(entity.getNumDia());

    if (entity.getTiprelIdSubstituido() != null) {
      var substituido = tiposRelacionamentoEntityRepository.findById(entity.getTiprelIdSubstituido()).orElse(null);
      req.setSubstituidoPor(substituido != null ? substituido.getUuid() : null);
      req.setSubstituidoPorNome(substituido != null ? substituido.getFunId().getNome() : null);
    }

    req.setObsConvinienciaServico(entity.getObsInfoConveniencia());

    if (entity.getResponsavelId() != null) {
      responsavelEntityRepository.findById(entity.getResponsavelId())
          .ifPresent(responsavel -> {
            req.setResponsavel(responsavel.getFunId().getUuid());
            req.setResponsavelNome(responsavel.getFunId().getNome());
          });
    }
    req.setParecer(entity.getDecisaoResponsavel());
    req.setObsParecer(entity.getObsResponsavel());

    // Férias podem existir sem pedido associado (ex.: dedução de falta em férias cria
    // o registo directamente). Sem esta guarda a leitura rebentava com NPE.
    var documentos = entity.getPedidoId() != null
        ? documentoEntityRepository.findAllByReferenciaNameAndReferenciaUuid(
            TableName.RH_T_FERIAS_GOZADAS.name(), entity.getPedidoId().getUuid())
        : null;

    if (documentos != null && !documentos.isEmpty()) {
      req.setDocumentos(documentos.stream().map(d -> {
        var anexo = new AnexoReqDTO();
        anexo.setId(d.getId());
        anexo.setTipoDocumentoId(d.getTpDocumentoId() != null ? d.getTpDocumentoId().getId() : null);
        anexo.setDocumento(d.getUrl());
        return anexo;
      }).collect(Collectors.toList()));
    }

    var dto = new PedidoFeriaAlterarReqDTO();
    dto.setFeria(req);
    dto.setTipoAlteracao("DATA");
    dto.setNovaDataFim(entity.getDataFim());
    dto.setMotivo(entity.getMotivoAlteracao());
    return dto;

  }
}
