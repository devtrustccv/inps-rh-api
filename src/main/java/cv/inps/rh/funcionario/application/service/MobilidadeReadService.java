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

    // Mostra activos, pendentes e inactivos (histórico completo). A edição/validação
    // de registos inactivos (I) ou eliminados (E) é bloqueada na camada de escrita.
    var estados = List.of(Estado.A.getCode(), Estado.P.getCode(), Estado.I.getCode());

    String tipoSituacao = StringUtils.hasText(query.getTipoMobilidade()) ? query.getTipoMobilidade() : null;
    LocalDate dataInicio = StringUtils.hasText(query.getDataInicio())
        ? DateFormatter.stringToLocalDate(query.getDataInicio()) : null;
    LocalDate dataFim = StringUtils.hasText(query.getDataFim())
        ? DateFormatter.stringToLocalDate(query.getDataFim()) : null;

    // A ordenação (DATA_INICIO DESC) já vem no ORDER BY da native query da vista.
    Pageable pageable = PageRequest.of(pageNumber, pageSize);
    Page<RhVMobilidadeEntity> page = rhVMobilidadeEntityRepository.findByFunUuidWithFilters(
        idFuncionario, estados, tipoSituacao, dataInicio, dataFim, pageable);

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

    return mobilidadeMapper.mobilidadeDTO(mobilidade);
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
