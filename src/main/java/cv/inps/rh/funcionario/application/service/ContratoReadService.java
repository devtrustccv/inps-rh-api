package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.WrapperListContratoDTO;
import cv.inps.rh.funcionario.application.queries.GetListContratosQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.ContratoMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.service.DominioService;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.RhVContratoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.RhVContratoEntityRepository;
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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContratoReadService {

  private final ContratoMapper contratoMapper;
  private final RhVContratoEntityRepository rhVContratoEntityRepository;
  private final DominioService dominioService;
  private final cv.inps.rh.funcionario.application.rules.FuncionarioRules funcionarioRules;

  @Transactional(readOnly = true)
  public WrapperListContratoDTO listaContratos(GetListContratosQuery query) {

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).valor();

    int pageNumber = query.getPageNumber() != null ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = query.getPageSize() != null ? Integer.parseInt(query.getPageSize()) : 20;

    Specification<RhVContratoEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      predicates.add(cb.equal(root.get("funUuid"), idFuncionario));

      // Gestão Contratual é uma vista de histórico (tem "Ver Informação Inicial/Atual"): mostra
      // activos (A), pendentes de validação (P), em correção (C) e também os inactivos/encerrados (I)
      // — o contrato anterior substituído por um novo fica I e deve continuar visível no histórico.
      // O C é necessário porque uma renovação devolvida para correção põe o histórico em C
      // (marcarRenovacaoPendenteComoCorrecao); sem ele a renovação em correção desaparecia da lista
      // (RH_V_CONTRATO expõe d.estado cru, logo emite C — basta alargar o filtro, sem overlay).
      var estados = List.of(Estado.A.getCode(), Estado.P.getCode(), Estado.C.getCode(), Estado.I.getCode());
      predicates.add(root.get("estado").in(estados));

      if (query.getVinculo() != null) {
        predicates.add(cb.equal(root.get("vinculoId"), query.getVinculo()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dataInicio"));
    Page<RhVContratoEntity> page = rhVContratoEntityRepository.findAll(spec, pageable);

    // situacaoDesc: traduz o tipo de situação do contrato (domínio TIPO_MOV_LABORAL, com fallback ao código)
    var dominioMovLaboral = dominioService.getDominioMap("TIPO_MOV_LABORAL");

    // "Atual" (botão Ver Informação Atual) = a versão mais recente, não rejeitada, do contrato da
    // RELAÇÃO LABORAL atual (tiprel est_act_adm=1) — e não o est_act_adm do histórico: com um novo
    // contrato/renovação pendente, o histórico do contrato anterior ainda tem est_act_adm=1 e os
    // botões apareciam trocados (anterior "Atual", pendente "Inicial").
    var tiprelAtual = funcionarioRules.getTipoRelacionamentoAtual(idFuncionario);
    Long contratoAtualId = tiprelAtual != null && tiprelAtual.getContrVinculoId() != null
        ? tiprelAtual.getContrVinculoId().getId() : null;
    Integer versaoAtual = contratoAtualId == null ? null
        : rhVContratoEntityRepository.findAllByContratoId(contratoAtualId).stream()
            .filter(h -> h.getEstado() == null || !("I".equals(h.getEstado()) || "E".equals(h.getEstado())))
            .map(RhVContratoEntity::getVersao)
            .filter(java.util.Objects::nonNull)
            .max(Integer::compare)
            .orElse(null);
    var content = page.getContent().stream()
        .map(v -> {
          var dto = contratoMapper.toDTO(v);
          if (contratoAtualId != null && versaoAtual != null)
            dto.setAtual(java.util.Objects.equals(v.getContratoId(), contratoAtualId)
                && java.util.Objects.equals(v.getVersao(), versaoAtual));
          dto.setSituacaoDesc(dominioService.traduzir(dominioMovLaboral, v.getTipoSituacao()));
          return dto;
        })
        .toList();

    var wrapper = new WrapperListContratoDTO();
    wrapper.setContent(content);
    PageMapper.fillPagination(page, wrapper);

    return wrapper;
  }
}
