package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.HoraExtraDTO;
import cv.inps.rh.assiduidade.application.dto.HoraExtraLinhaDTO;
import cv.inps.rh.assiduidade.application.dto.HoraExtraPedidoDTO;
import cv.inps.rh.assiduidade.application.dto.HoraExtraReqDTO;
import cv.inps.rh.assiduidade.application.dto.WrapperListaHoraExtraDTO;
import cv.inps.rh.assiduidade.application.queries.GetHoraExtraQuery;
import cv.inps.rh.assiduidade.application.queries.GetListaHoraExtraQuery;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.dto.AnexoReqDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.HoraExtraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.VHoraExtraMensalEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.VHoraExtraPedidoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.HoraExtraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.VHoraExtraMensalEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.VHoraExtraPedidoEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Leitura de hora extra.
 *
 * <p>A lista é servida em dois níveis: pedido (a unidade de validação) com os itens
 * colaborador × mês aninhados. Ver
 * {@code docs/frontend_changes_assiduidade.md} para o contrato.
 */
@Service
@RequiredArgsConstructor
public class HoraExtraReadService {

  private static final Locale PT = Locale.forLanguageTag("pt-PT");

  private final HoraExtraEntityRepository horaExtraRepository;
  private final DocumentoEntityRepository documentoEntityRepository;
  private final VHoraExtraMensalEntityRepository vHoraExtraMensalRepository;
  private final VHoraExtraPedidoEntityRepository vHoraExtraPedidoRepository;

  @Transactional(readOnly = true)
  public WrapperListaHoraExtraDTO getListaHoraExtra(GetListaHoraExtraQuery query) {

    int pageNumber = StringUtils.hasText(query.getPageNumber())
        ? Integer.parseInt(query.getPageNumber())
        : 0;

    int pageSize = StringUtils.hasText(query.getPageSize())
        ? Integer.parseInt(query.getPageSize())
        : 20;

    // Pagina sobre pedidos — antes paginava sobre registos, e um pedido de 8
    // colaboradores consumia 8 slots de página.
    Pageable pageable = PageRequest.of(pageNumber, pageSize,
        Sort.by(Sort.Direction.DESC, "dataPedido"));

    Page<VHoraExtraPedidoEntity> page =
        vHoraExtraPedidoRepository.findAll(buildPedidoSpec(query), pageable);

    var wrapper = new WrapperListaHoraExtraDTO();
    PageMapper.fillPagination(page, wrapper);

    if (page.isEmpty())
      return wrapper;

    // Um único select para os itens de todos os pedidos da página.
    var pedidoIds = page.getContent().stream().map(VHoraExtraPedidoEntity::getPedidoId).toList();
    var itens = vHoraExtraMensalRepository.findAll(buildItemSpec(query, pedidoIds));

    Map<Long, List<VHoraExtraMensalEntity>> itensPorPedido = itens.stream()
        .filter(i -> i.getPedidoId() != null)
        .collect(Collectors.groupingBy(VHoraExtraMensalEntity::getPedidoId));

    wrapper.setContent(page.getContent().stream()
        .map(p -> toPedidoDTO(p, itensPorPedido.getOrDefault(p.getPedidoId(), List.of())))
        .toList());

    return wrapper;
  }

  /** Selecção dos pedidos. Os filtros de pessoa/estrutura usam subquery sobre os itens. */
  private Specification<VHoraExtraPedidoEntity> buildPedidoSpec(GetListaHoraExtraQuery query) {
    return (root, cq, cb) -> {

      var predicates = new ArrayList<Predicate>();

      if (StringUtils.hasText(query.getEstado()))
        predicates.add(cb.equal(root.get("estado"), query.getEstado()));

      // Sobreposição, não contenção: um pedido de 20/01 a 10/03 tem de aparecer
      // ao filtrar por Fevereiro.
      if (StringUtils.hasText(query.getDataFim()))
        predicates.add(cb.lessThanOrEqualTo(
            root.get("periodoInicio"), LocalDate.parse(query.getDataFim())));

      if (StringUtils.hasText(query.getDataInicio()))
        predicates.add(cb.greaterThanOrEqualTo(
            root.get("periodoFim"), LocalDate.parse(query.getDataInicio())));

      // Um pedido entra na lista se algum dos seus itens corresponder aos filtros
      // de pessoa/estrutura.
      if (temFiltroDeItem(query)) {
        var sub = cq.subquery(Long.class);
        var item = sub.from(VHoraExtraMensalEntity.class);
        sub.select(item.get("pedidoId"));
        sub.where(cb.and(
            cb.equal(item.get("pedidoId"), root.get("pedidoId")),
            cb.and(itemPredicates(query, item, cb).toArray(new Predicate[0]))));
        predicates.add(cb.exists(sub));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  /** Itens dos pedidos da página, já restringidos pelos mesmos filtros. */
  private Specification<VHoraExtraMensalEntity> buildItemSpec(
      GetListaHoraExtraQuery query, List<Long> pedidoIds) {

    return (root, cq, cb) -> {
      var predicates = new ArrayList<Predicate>();
      predicates.add(root.get("pedidoId").in(pedidoIds));
      predicates.addAll(itemPredicates(query, root, cb));
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }

  private boolean temFiltroDeItem(GetListaHoraExtraQuery query) {
    return StringUtils.hasText(query.getFuncionarioUuid())
        || StringUtils.hasText(query.getColaborador())
        || StringUtils.hasText(query.getMes())
        || query.getDirecao() != null
        || query.getSeccao() != null
        || query.getIlha() != null;
  }

  private List<Predicate> itemPredicates(
      GetListaHoraExtraQuery query,
      jakarta.persistence.criteria.From<?, VHoraExtraMensalEntity> root,
      jakarta.persistence.criteria.CriteriaBuilder cb) {

    var predicates = new ArrayList<Predicate>();

    if (StringUtils.hasText(query.getFuncionarioUuid())) {
      try {
        predicates.add(cb.equal(root.get("funcionarioUuid"),
            UUID.fromString(query.getFuncionarioUuid())));
      } catch (IllegalArgumentException ignored) {
        // UUID inválido — filtro ignorado
      }
    }

    if (StringUtils.hasText(query.getColaborador()))
      predicates.add(cb.like(cb.lower(root.get("nomeFuncionario")),
          "%" + query.getColaborador().toLowerCase() + "%"));

    if (StringUtils.hasText(query.getMes()))
      predicates.add(cb.equal(root.get("mesReferencia"), query.getMes()));

    if (query.getDirecao() != null)
      predicates.add(cb.equal(root.get("idDirecao"), query.getDirecao()));

    if (query.getSeccao() != null)
      predicates.add(cb.equal(root.get("idSecao"), query.getSeccao()));

    if (query.getIlha() != null)
      predicates.add(cb.equal(root.get("idIlha"), query.getIlha()));

    return predicates;
  }

  private HoraExtraPedidoDTO toPedidoDTO(VHoraExtraPedidoEntity p, List<VHoraExtraMensalEntity> itens) {

    var dto = new HoraExtraPedidoDTO();
    dto.setPedidoId(p.getPedidoId());
    dto.setPedidoUuid(p.getPedidoUuid());
    dto.setEstado(p.getEstado());
    dto.setEstadoDesc(p.getEstadoDesc());
    dto.setEtapa(p.getEtapa());
    dto.setDataPedido(toIso(p.getDataPedido()));
    dto.setPeriodoInicio(toIso(p.getPeriodoInicio()));
    dto.setPeriodoFim(toIso(p.getPeriodoFim()));

    dto.setDirecaoId(p.getIdDirecao());
    // A vista só preenche direcção/secção quando são únicas no pedido.
    dto.setDirecao(p.getIdDirecao() != null ? p.getNomeDirecao() : "Várias");
    dto.setSeccaoId(p.getIdSecao());
    dto.setSeccao(p.getIdSecao() != null ? p.getNomeSecao() : "Várias");

    dto.setTotalColaboradoresPedido(p.getTotalColaboradores());
    dto.setValorTotalPedido(p.getValorTotal());

    var linhas = itens.stream()
        .sorted(Comparator
            .comparing(VHoraExtraMensalEntity::getNomeFuncionario, Comparator.nullsLast(String::compareTo))
            .thenComparing(VHoraExtraMensalEntity::getMesReferencia, Comparator.nullsLast(String::compareTo)))
        .map(this::toLinhaDTO)
        .toList();

    dto.setItens(linhas);
    dto.setTotalRegistos(linhas.size());
    dto.setTotalColaboradores((int) linhas.stream()
        .map(HoraExtraLinhaDTO::getFuncionarioUuid).distinct().count());
    dto.setMesesReferencia(linhas.stream()
        .map(HoraExtraLinhaDTO::getMes).filter(java.util.Objects::nonNull).distinct().sorted().toList());
    dto.setValorTotal(linhas.stream()
        .map(HoraExtraLinhaDTO::getValorAcumuladoMes)
        .filter(java.util.Objects::nonNull)
        .reduce(BigDecimal.ZERO, BigDecimal::add));

    return dto;
  }

  private HoraExtraLinhaDTO toLinhaDTO(VHoraExtraMensalEntity e) {

    var dto = new HoraExtraLinhaDTO();
    dto.setHoraExtraId(e.getHoraExtraId());
    dto.setHoraExtraUuid(e.getHoraExtraUuid());
    dto.setFuncionarioUuid(e.getFuncionarioUuid() != null ? e.getFuncionarioUuid().toString() : null);
    dto.setNomeColaborador(e.getNomeFuncionario());
    dto.setCargo(e.getNomeCargo());

    dto.setDirecaoId(e.getIdDirecao());
    dto.setDirecao(e.getNomeDirecao());
    dto.setSeccaoId(e.getIdSecao());
    dto.setSeccao(e.getNomeSecao());
    dto.setIlhaId(e.getIdIlha());
    dto.setIlha(e.getNomeIlha());

    dto.setMes(e.getMesReferencia());
    dto.setMesDesc(mesPorExtenso(e.getAno(), e.getMesNumero()));
    dto.setDataInicio(toIso(e.getDataInicio()));
    dto.setDataFim(toIso(e.getDataFim()));
    dto.setPeriodoInicio(toIso(e.getPeriodoInicio()));
    dto.setPeriodoFim(toIso(e.getPeriodoFim()));

    dto.setDiasUteis(e.getDiasUteis());
    dto.setDiasNaoUteis(e.getDiasNaoUteis());

    dto.setHorasContratadaDiaria(e.getHorasContratadoDiario());
    dto.setHorasContratadaMensal(asText(e.getHorasContratadoMensal()));
    dto.setHorasExtraDiarias(asText(e.getHorasExtraDiarias()));
    dto.setHorasTrabalho(asText(e.getHorasTrabalho()));

    dto.setSalarioMensal(e.getSalarioMensal());
    dto.setPercentagemReferente(e.getPercentagemReferente());
    dto.setPercentagemUtil(e.getPercentagemUtil());
    dto.setPercentagemNaoUtil(e.getPercentagemNaoUtil());
    dto.setValorDiarioUtil(e.getValorDiarioUtil());
    dto.setValorDiarioNaoUtil(e.getValorDiarioNaoUtil());
    dto.setValorAcumuladoMes(e.getValorAcumuladoMes());

    dto.setEstado(e.getEstado());
    dto.setEstadoDesc(e.getEstadoDesc());

    return dto;
  }

  private String mesPorExtenso(Integer ano, Integer mes) {
    if (ano == null || mes == null)
      return null;
    var nome = java.time.Month.of(mes).getDisplayName(TextStyle.FULL, PT);
    return Character.toUpperCase(nome.charAt(0)) + nome.substring(1) + "/" + ano;
  }

  private String toIso(LocalDate d) {
    return d != null ? d.toString() : null;
  }

  private String asText(BigDecimal v) {
    return v != null ? v.toPlainString() : null;
  }

  /**
   * Detalhe de um pedido. Mantido a par da lista: o front pode expandir com o que já
   * veio ou pedir o detalhe fresco, o que é preferível no ecrã de validação.
   */
  @Transactional(readOnly = true)
  public HoraExtraReqDTO getHoraExtra(GetHoraExtraQuery query) {

    HoraExtraReqDTO dto = new HoraExtraReqDTO();

    if (query == null || !StringUtils.hasText(query.getPedidoId()))
      return dto;

    UUID pedidoUuid;
    try {
      pedidoUuid = UUID.fromString(query.getPedidoId());
    } catch (IllegalArgumentException e) {
      return dto;
    }

    var horasExtra = horaExtraRepository.findAllByPedidoId_Uuid(pedidoUuid);
    if (horasExtra == null || horasExtra.isEmpty())
      return dto;

    // Repartição mensal a partir da vista, para o detalhe falar a mesma língua da lista.
    var linhasPorHoraExtra = vHoraExtraMensalRepository
        .findAll((root, cq, cb) -> root.get("horaExtraId")
            .in(horasExtra.stream().map(HoraExtraEntity::getId).toList()))
        .stream()
        .collect(Collectors.groupingBy(VHoraExtraMensalEntity::getHoraExtraId));

    dto.setHoraExtra(new ArrayList<>());

    for (HoraExtraEntity e : horasExtra) {
      var fun = e.getTiprelId() != null ? e.getTiprelId().getFunId() : null;

      AnexoReqDTO anexo = null;
      var docsHe = documentoEntityRepository
          .findAllByReferenciaNameAndReferenciaUuid(TableName.RH_T_HORA_EXTRA.name(), e.getUuid());
      if (docsHe != null && !docsHe.isEmpty()) {
        var d = docsHe.getFirst();
        anexo = new AnexoReqDTO();
        anexo.setTipoDocumentoId(d.getTpDocumentoId() != null ? d.getTpDocumentoId().getId() : null);
        anexo.setDocumento(d.getUrl());
      }

      for (var linha : linhasPorHoraExtra.getOrDefault(e.getId(), List.of())) {
        HoraExtraDTO item = new HoraExtraDTO();
        item.setId(e.getId());
        item.setColaborador(fun != null ? fun.getUuid() : null);
        item.setColaboradorNome(fun != null ? fun.getNome() : null);
        item.setDataInicio(linha.getDataInicio());
        item.setDataFim(linha.getDataFim());
        item.setHorasDiaria(e.getHorasDiarias());
        item.setPercentagemReferente(e.getPercentagemReferente());
        item.setValorDiario(e.getValorDiario());

        item.setMes(linha.getMesReferencia());
        item.setDiasUteis(linha.getDiasUteis());
        item.setDiasNaoUteis(linha.getDiasNaoUteis());
        item.setValorDiarioUtil(linha.getValorDiarioUtil());
        item.setValorDiarioNaoUtil(linha.getValorDiarioNaoUtil());
        item.setValorAcumuladoMes(linha.getValorAcumuladoMes());

        item.setDocumento(anexo);
        dto.getHoraExtra().add(item);
      }
    }

    return dto;
  }

}
