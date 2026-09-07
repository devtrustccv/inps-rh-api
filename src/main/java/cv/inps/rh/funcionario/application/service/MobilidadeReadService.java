package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import cv.inps.rh.funcionario.application.dto.MobilidadeListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListMobilidadeDTO;
import cv.inps.rh.funcionario.application.queries.GetListMobilidadesQuery;
import cv.inps.rh.funcionario.application.queries.GetMobilidadeAtualQuery;
import cv.inps.rh.funcionario.application.queries.GetMobilidadeByIdQuery;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.MobilidadeMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.MobilidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RhVMobilidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.MobilidadeEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.RhVMobilidadeEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MobilidadeReadService {

  private final MobilidadeEntityRepository mobilidadeEntityRepository;
  private final RhVMobilidadeEntityRepository rhVMobilidadeEntityRepository;
  private final MobilidadeMapper mobilidadeMapper;
  private final FuncionarioRules funcionarioRules;

  @Transactional(readOnly = true)
  public WrapperListMobilidadeDTO getListMobilidade(GetListMobilidadesQuery query) {

    int pageNumber = query.getPageNumber() != null ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = query.getPageSize() != null ? Integer.parseInt(query.getPageSize()) : 20;

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).valor().toString();

    // Mostra activos, pendentes, inactivos e EM CORREÇÃO (histórico completo). A edição/validação
    // de registos inactivos (I) ou eliminados (E) é bloqueada na camada de escrita.
    var estados = List.of(Estado.A.getCode(), Estado.P.getCode(), Estado.I.getCode(), Estado.C.getCode());

    String tipoSituacao = StringUtils.hasText(query.getTipoMobilidade()) ? query.getTipoMobilidade() : null;
    LocalDate dataInicio = StringUtils.hasText(query.getDataInicio())
        ? DateFormatter.stringToLocalDate(query.getDataInicio()) : null;
    LocalDate dataFim = StringUtils.hasText(query.getDataFim())
        ? DateFormatter.stringToLocalDate(query.getDataFim()) : null;

    // A ordenação (DATA_INICIO DESC) já vem no ORDER BY da native query da vista.
    Pageable pageable = PageRequest.of(pageNumber, pageSize);
    Page<RhVMobilidadeEntity> page = rhVMobilidadeEntityRepository.findByFunUuidWithFilters(
        idFuncionario, estados, tipoSituacao, dataInicio, dataFim, pageable);

    // A vista RH_V_MOBILIDADE calcula o estado por datas (A/I) e NÃO reflete os estados de workflow
    // (P pendente, C em correção). Buscamos o estado real da TABELA por mob_id para os sobrepor abaixo —
    // sem isto o maker/checker não veem uma mobilidade devolvida para correção (aparecia como "Ativo").
    var mobIds = page.getContent().stream().map(RhVMobilidadeEntity::getMobId).filter(Objects::nonNull).toList();
    Map<Long, Estado> estadoTabelaPorId = mobIds.isEmpty() ? Map.of()
        : mobilidadeEntityRepository.findAllById(mobIds).stream()
            .filter(e -> e.getId() != null && e.getEstado() != null)
            .collect(Collectors.toMap(MobilidadeEntity::getId, MobilidadeEntity::getEstado));

    List<MobilidadeListDTO> content = page.getContent().stream().map(m -> {
      MobilidadeListDTO dto = new MobilidadeListDTO();
      dto.setId(m.getMobId());
      dto.setIdFuncionario(m.getFunId());
      dto.setUuid(m.getMobUuid());
      dto.setUuidFuncionario(m.getFunUuid());
      dto.setDireccao(m.getDirecaoNome());
      dto.setSeccao(m.getUnidadeDesc());
      dto.setLocalTrabalho(m.getLocalTrabNome());
      dto.setDataInicio(DateFormatter.localDateToString(m.getDataInicio()));
      dto.setDataFim(DateFormatter.localDateToString(m.getDataFim()));
      dto.setProcessamento(m.getProcessamento() != null && m.getProcessamento() > 0);
      dto.setEstado(m.getEstado());
      dto.setEstadoDesc(Estado.fromCode(m.getEstado()).map(Estado::getDescription).orElse(null));
      // Sobrepõe o estado da vista (A/I por datas) com o estado de workflow da TABELA quando é P ou C,
      // para o ciclo maker-checker (pendente / em correção) ficar visível na lista.
      var estadoTabela = estadoTabelaPorId.get(m.getMobId());
      if (estadoTabela == Estado.P || estadoTabela == Estado.C) {
        dto.setEstado(estadoTabela.getCode());
        dto.setEstadoDesc(estadoTabela.getDescription());
      }
      dto.setTipoMobilidade(m.getTipoSituacao());
      dto.setTipoMobilidadeDesc(m.getTipoSituacaoDesc());

      return dto;
    }).toList();

    var wrapper = new WrapperListMobilidadeDTO();
    wrapper.setContent(content);
    wrapper.setPageNumber(page.getNumber());
    wrapper.setPageSize(page.getSize());
    wrapper.setTotalElements(page.getTotalElements());
    wrapper.setTotalPages(page.getTotalPages());
    wrapper.setFirst(page.isFirst());
    wrapper.setLast(page.isLast());

    return wrapper;
  }

  @Transactional(readOnly = true)
  public MobilidadeDTO getMobilidade(GetMobilidadeByIdQuery query) {

    IdentificadorUnico id = IdentificadorUnico.from(query.getId());
    var mobilidade = mobilidadeEntityRepository.findByUuid(id.valor()).orElseThrow(
        () -> IgrpResponseStatusException.notFound("mobilidade nao encontrada com id"+query.getId())
    );

    // O "antes" é o pai directo do registo: RH_T_MOBILIDADE.MOB_ID, a mobilidade que estava em vigor
    // quando esta foi registada. É o mapper que compara pai vs registo para decidir, dimensão a
    // dimensão, o que mudou (Origem+Destino) e o que não mudou (só Origem).
    //
    // Não se sobe a cadeia de tiprel à procura do "antes": além de não funcionar enquanto a mobilidade
    // está pendente (ainda não existe tiprel a apontar-lhe), vários tiprels podem partilhar o mesmo MOB
    // e a heurística nunca acrescentava nada ao que o MOB_ID já diz. Fica null na primeira mobilidade
    // do funcionário e em registos gravados antes de MOB_ID ser preenchido — e nesse caso o mapper
    // trata o registo como posição, não como movimento.
    var pai = mobilidade.getMobId() != null
        && !java.util.Objects.equals(mobilidade.getMobId().getId(), mobilidade.getId())
        ? mobilidade.getMobId()
        : null;

    return mobilidadeMapper.mobilidadeDetalheDTO(mobilidade, pai);
  }

  @Transactional(readOnly = true)
  public MobilidadeDTO getMobilidadeAtual(GetMobilidadeAtualQuery query) {

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).valor();

    var tiprel = funcionarioRules.getTipoRelacionamentoAtual(idFuncionario);
    if (tiprel == null || tiprel.getMobId() == null)
      throw IgrpResponseStatusException.notFound(
          "Funcionário não tem mobilidade atual: " + query.getIdFuncionario());

    return mobilidadeMapper.mobilidadeDTO(tiprel.getMobId());
  }
}
