package cv.inps.rh.assiduidade.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.assiduidade.application.dto.FeriasGozadaItemDTO;
import cv.inps.rh.assiduidade.application.dto.FeriasListDTO;
import cv.inps.rh.assiduidade.application.dto.PedidoFeriaAlterarReqDTO;
import cv.inps.rh.assiduidade.application.dto.PedidoFeriaReqDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaFeriaDTO;
import cv.inps.rh.assiduidade.application.queries.GetListaFeriaQuery;
import cv.inps.rh.assiduidade.application.queries.GetPedidoFeriaQuery;
import cv.inps.rh.shared.util.ValidationUtil;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FeriasGozadasEntity;
import cv.inps.rh.shared.infrastructure.persistence.projections.FeriasResumoAnoProjection;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.PageMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

    // Uma linha por funcionário e ano (lista pai); as férias gozadas desse ano vão em
    // feriasGozadas (lista filho), lidas numa só consulta para a página toda.
    UUID funcionario = null;
    if (StringUtils.hasText(query.getFuncionarioUuid())) {
      try {
        funcionario = UUID.fromString(query.getFuncionarioUuid());
      } catch (IllegalArgumentException ignored) {
        // Ignore invalid UUIDs
      }
    }
    String nome = StringUtils.hasText(query.getColaborador())
        ? "%" + query.getColaborador().trim().toLowerCase() + "%"
        : null;

    Page<FeriasResumoAnoProjection> page = vFeriasMensalEntityRepository.findResumoPorAno(
        query.getAnoReferente(), query.getDirecao(), query.getSeccao(), nome, funcionario,
        PageRequest.of(pageNumber, pageSize));

    var gozadasPorLinha = feriasGozadasDaPagina(page.getContent());

    var content = page.getContent()
        .stream()
        .map(r -> toDTO(r, gozadasPorLinha.getOrDefault(chave(r.getUuidFuncionario(), r.getAno()), List.of())))
        .toList();

    var wrapper = new WrapperListaFeriaDTO();
    PageMapper.fillPagination(page, wrapper);
    wrapper.setContent(content);
    return wrapper;
  }

  private Map<String, List<FeriasGozadaItemDTO>> feriasGozadasDaPagina(List<FeriasResumoAnoProjection> linhas) {
    if (linhas.isEmpty()) return Map.of();
    var funcionarios = linhas.stream().map(FeriasResumoAnoProjection::getUuidFuncionario).collect(Collectors.toSet());
    var anos = linhas.stream().map(r -> String.valueOf(r.getAno())).collect(Collectors.toSet());
    return feriasGozadasEntityRepository.findDaListaFerias(funcionarios, anos, Estado.E)
        .stream()
        .collect(Collectors.groupingBy(
            fg -> chave(fg.getFunId().getUuid(), Integer.valueOf(fg.getAnoId().getAno())),
            Collectors.mapping(this::toGozadaDTO, Collectors.toList())));
  }

  private static String chave(UUID funcionario, Integer ano) {
    return funcionario + "|" + ano;
  }

  private FeriasListDTO toDTO(FeriasResumoAnoProjection r, List<FeriasGozadaItemDTO> gozadas) {
    var dto = new FeriasListDTO();
    dto.setUuidFuncionario(r.getUuidFuncionario().toString());
    dto.setNomeColaborador(r.getNomeColaborador());
    dto.setDirecao(r.getDirecao());
    dto.setSecao(r.getSecao());
    dto.setVinculo(r.getVinculo());
    dto.setCategoria(r.getCategoria());
    dto.setTotalDireito(inteiro(r.getTotalDireito()));
    dto.setTotalDireitoAno(inteiro(r.getTotalDireitoAno()));
    dto.setTotalPlaneado(inteiro(r.getTotalPlaneado()));
    dto.setTotalGozado(inteiro(r.getTotalGozado()));
    dto.setAno(r.getAno());
    dto.setFeriasGozadas(new ArrayList<>(gozadas));
    return dto;
  }

  private FeriasGozadaItemDTO toGozadaDTO(FeriasGozadasEntity fg) {
    var pedido = fg.getPedidoId();
    var dto = new FeriasGozadaItemDTO();
    dto.setId(fg.getId());
    dto.setUuid(fg.getUuid() != null ? fg.getUuid().toString() : null);
    dto.setPedidoUuid(pedido != null && pedido.getUuid() != null ? pedido.getUuid().toString() : null);
    dto.setTipoPedido(pedido != null ? pedido.getTipoPedido() : null);
    dto.setDataInicio(fg.getDataInicio());
    dto.setDataFim(fg.getDataFim());
    dto.setNumDias(fg.getNumDia());
    dto.setEstado(fg.getEstado() != null ? fg.getEstado().getCode() : null);
    dto.setEstadoDesc(fg.getEstado() != null ? fg.getEstado().getDescription() : null);
    dto.setObservacao(fg.getMotivoAlteracao());
    return dto;
  }

  private static Integer inteiro(Number n) {
    return n != null ? n.intValue() : 0;
  }


  @Transactional(readOnly = true)
  public PedidoFeriaAlterarReqDTO getPedidoFeria(GetPedidoFeriaQuery query) {
    // UuidCreator.fromString lançava IllegalArgumentException num id malformado, o que
    // saía como 500. Um identificador inválido é erro do cliente: 400.
    var pedidoUuid = ValidationUtil.parseUuid(query.getPedidoId(), "Identificador do pedido de férias");

    var entity = feriasGozadasEntityRepository.findPedidoFeria(pedidoUuid)
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
      // uuid do FUNCIONÁRIO, como o registo e o reenvio o recebem (antes vinha o do vínculo).
      req.setSubstituidoPor(substituido != null ? substituido.getFunId().getUuid() : null);
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
