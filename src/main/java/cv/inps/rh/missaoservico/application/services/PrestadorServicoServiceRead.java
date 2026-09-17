package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.missaoservico.application.dto.PrestadorAvaliacaoResponseDTO;
import cv.inps.rh.missaoservico.application.dto.PrestadorEmailResponseDTO;
import cv.inps.rh.missaoservico.application.dto.PrestadorServicoResponseDTO;
import cv.inps.rh.missaoservico.application.dto.WrapperListPrestadorServicoDTO;
import cv.inps.rh.missaoservico.application.queries.GetAvaliacoesPrestadorServicoQuery;
import cv.inps.rh.missaoservico.application.queries.GetListaPrestadorServicoQuery;
import cv.inps.rh.missaoservico.application.queries.GetPrestadorServicoQuery;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoPrestadorAvalEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamPrestadorDetEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamPrestadorEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoPrestadorAvalEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamPrestadorDetEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamPrestadorEntityRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@RequiredArgsConstructor
@Service
public class PrestadorServicoServiceRead {

  private static final String ESTADO_ATIVO = "A";

  private final ParamPrestadorEntityRepository paramPrestadorRepository;
  private final ParamPrestadorDetEntityRepository paramPrestadorDetRepository;
  private final MissaoPrestadorAvalEntityRepository missaoPrestadorAvalRepository;

  @Transactional(readOnly = true)
  public ResponseEntity<WrapperListPrestadorServicoDTO> listar(GetListaPrestadorServicoQuery query) {
    var nome = query != null ? query.getNome() : null;
    var ilhaId = parseLong(query != null ? query.getIlhaId() : null);
    var estado = query != null ? query.getEstado() : null;
    var pageNumber = parseInt(query != null ? query.getPageNumber() : null, 0);
    var pageSize = parseInt(query != null ? query.getPageSize() : null, 10);

    Specification<ParamPrestadorEntity> spec = (root, q, cb) -> {
      var predicates = new ArrayList<Predicate>();
      if (StringUtils.hasText(nome)) {
        predicates.add(cb.like(cb.upper(root.get("nome")), "%" + nome.trim().toUpperCase() + "%"));
      }
      if (ilhaId != null) {
        predicates.add(cb.equal(root.get("ilhaId").get("id"), ilhaId));
      }
      if (StringUtils.hasText(estado)) {
        predicates.add(cb.equal(root.get("estado"), estado.trim()));
      }
      return cb.and(predicates.toArray(Predicate[]::new));
    };

    var page = paramPrestadorRepository.findAll(spec,
        PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, "nome")));

    var ids = page.getContent().stream().map(ParamPrestadorEntity::getId).toList();
    var emailsPorPrestador = new HashMap<Long, List<ParamPrestadorDetEntity>>();
    if (!ids.isEmpty()) {
      for (var d : paramPrestadorDetRepository.findAllByParamPrestId_IdInAndEstado(ids, ESTADO_ATIVO)) {
        emailsPorPrestador.computeIfAbsent(d.getParamPrestId().getId(), _ -> new ArrayList<>()).add(d);
      }
    }

    var wrapper = new WrapperListPrestadorServicoDTO();
    wrapper.setContent(page.getContent().stream()
        .map(p -> toDto(p, emailsPorPrestador.getOrDefault(p.getId(), List.of())))
        .toList());
    wrapper.setPageNumber(page.getNumber());
    wrapper.setPageSize(page.getSize());
    wrapper.setTotalElements(page.getTotalElements());
    wrapper.setTotalPages(page.getTotalPages());
    wrapper.setFirst(page.isFirst());
    wrapper.setLast(page.isLast());
    return ResponseEntity.ok(wrapper);
  }

  @Transactional(readOnly = true)
  public ResponseEntity<PrestadorServicoResponseDTO> detalhe(GetPrestadorServicoQuery query) {
    var uuid = IdentificadorUnico.from(query != null ? query.getUuid() : null).valor();
    var prestador = paramPrestadorRepository.findByUuidOrThrow(uuid);
    // No detalhe (ecrã de edição) vêm todos os emails, com o estado, para o formulário.
    var emails = paramPrestadorDetRepository.findAllByParamPrestId_IdOrderByIdAsc(prestador.getId());
    return ResponseEntity.ok(toDto(prestador, emails));
  }

  /** Ecrã "Ver Avaliação": avaliações feitas ao prestador em cada processo de missão. */
  @Transactional(readOnly = true)
  public ResponseEntity<List<PrestadorAvaliacaoResponseDTO>> avaliacoes(GetAvaliacoesPrestadorServicoQuery query) {
    var uuid = IdentificadorUnico.from(query != null ? query.getUuid() : null).valor();
    var prestador = paramPrestadorRepository.findByUuidOrThrow(uuid);

    var avaliacoes = missaoPrestadorAvalRepository
        .findAllByMissaoPrestId_ParamPrestId_IdAndEstadoOrderByIdDesc(prestador.getId(), ESTADO_ATIVO);
    return ResponseEntity.ok(avaliacoes.stream().map(this::toAvaliacaoDto).toList());
  }

  private PrestadorServicoResponseDTO toDto(ParamPrestadorEntity p, List<ParamPrestadorDetEntity> emails) {
    var dto = new PrestadorServicoResponseDTO();
    dto.setId(p.getId());
    dto.setUuid(p.getUuid());
    dto.setEntId(p.getEntId());
    dto.setNome(p.getNome());
    dto.setNif(p.getNif());
    dto.setEmail(p.getEmail());
    dto.setTelefone(p.getTelefone());
    dto.setIlhaId(p.getIlhaId() != null ? p.getIlhaId().getId() : null);
    dto.setIlhaNome(p.getIlhaId() != null ? p.getIlhaId().getNome() : null);
    dto.setMorada(p.getMorada());
    dto.setEstado(p.getEstado());
    dto.setEstadoDesc(ESTADO_ATIVO.equals(p.getEstado()) ? "Activo" : "Inactivo");
    dto.setEmails(emails.stream().map(d -> {
      var e = new PrestadorEmailResponseDTO();
      e.setId(d.getId());
      e.setUuid(d.getUuid());
      e.setEmail(d.getEmail());
      e.setEstado(d.getEstado());
      return e;
    }).toList());
    dto.setDataRegisto(toLocalDate(p.getCreatedDate()));
    dto.setUserRegistoName(p.getCreatedBy());
    dto.setDataAlteracao(toLocalDate(p.getLastModifiedDate()));
    dto.setUserAlteracaoName(p.getLastModifiedBy());
    return dto;
  }

  private PrestadorAvaliacaoResponseDTO toAvaliacaoDto(MissaoPrestadorAvalEntity a) {
    var dto = new PrestadorAvaliacaoResponseDTO();
    dto.setUuid(a.getUuid());
    var missaoPrestador = a.getMissaoPrestId();
    var missao = missaoPrestador != null ? missaoPrestador.getMissaoServId() : null;
    if (missao != null) {
      dto.setMissaoUuid(missao.getUuid());
      dto.setNrMissao(missao.getNrMissao());
      dto.setNrMissaoFormatado(missao.getAno() != null
          ? missao.getNrMissao() + "/" + missao.getAno()
          : String.valueOf(missao.getNrMissao()));
    }
    if (missaoPrestador != null && missaoPrestador.getMissaoProcessoId() != null) {
      dto.setTipoProcesso(missaoPrestador.getMissaoProcessoId().getTipoProcesso());
    }
    dto.setSistemaQualidade(a.getSistemaQualidade());
    dto.setPrazoFornecimento(a.getPrazoFornecimento());
    dto.setQualidadeProduto(a.getQualidadeProduto());
    dto.setCapacidadeResposta(a.getCapacidadeResposta());
    dto.setPreco(a.getPreco());
    dto.setTotal(a.getTotal());
    dto.setDesignacao(a.getDesignacao());
    dto.setDataRegisto(toLocalDate(a.getCreatedDate()));
    return dto;
  }

  private LocalDate toLocalDate(LocalDateTime dt) {
    return dt != null ? dt.toLocalDate() : null;
  }

  private Long parseLong(String raw) {
    if (!StringUtils.hasText(raw))
      return null;
    try {
      return Long.valueOf(raw.trim());
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private int parseInt(String raw, int fallback) {
    if (!StringUtils.hasText(raw))
      return fallback;
    try {
      return Math.max(0, Integer.parseInt(raw.trim()));
    } catch (NumberFormatException e) {
      return fallback;
    }
  }
}
