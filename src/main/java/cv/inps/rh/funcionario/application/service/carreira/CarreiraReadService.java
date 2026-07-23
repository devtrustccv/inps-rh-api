package cv.inps.rh.funcionario.application.service.carreira;

import cv.inps.rh.funcionario.application.dto.*;
import cv.inps.rh.funcionario.application.queries.GetCarreiraListQuery;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.RhVCarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.CarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefPagamentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefinicaoRemuneracaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ProcessamentoFuncionarioRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.RhVCarreiraEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Transactional
@Service
@RequiredArgsConstructor
public class CarreiraReadService {

  private final TiposRelacionamentoEntityRepository tiposRelacionamentoEntityRepository;
  private final CarreiraEntityRepository carreiraEntityRepository;
  private final DefPagamentoEntityRepository defPagamentoEntityRepository;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final RhVCarreiraEntityRepository rhVCarreiraEntityRepository;
  private final ProcessamentoFuncionarioRepository processamentoFuncionarioRepository;
  private final FuncionarioRules funcionarioRules;

  @Transactional(readOnly = true)
  public WrapperCarreiraListDTO list(GetCarreiraListQuery query) {

    var pageNumber = Integer.parseInt(query.getPageNumber());
    var pageSize = Integer.parseInt(query.getPageSize());

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).valor();

    Specification<RhVCarreiraEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      predicates.add(cb.equal(root.get("funUuid"), idFuncionario));

      // Mostra activos e pendentes; não mostra inactivos (I)
      var estados = List.of(Estado.A.getCode(), Estado.P.getCode(), Estado.I.getCode());
      predicates.add(root.get("estadoCarreira").in(estados));

      if (StringUtils.hasText(query.getTipoCarreira())) {
        predicates.add(cb.equal(root.get("tipoSituacao"), query.getTipoCarreira()));
      }

      if (StringUtils.hasText(query.getDataInicio())) {
        var di = DateFormatter.stringToLocalDate(query.getDataInicio());
        predicates.add(cb.greaterThanOrEqualTo(root.get("dataInicio"), di));
      }
      if (StringUtils.hasText(query.getDataFim())) {
        var df = DateFormatter.stringToLocalDate(query.getDataFim());
        predicates.add(cb.lessThanOrEqualTo(root.get("dataFim"), df));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dataInicio"));
    var page = rhVCarreiraEntityRepository.findAll(spec, pageable);

    List<CarreiraListDTO> content = page.getContent().stream().map(this::toDTO).toList();

    var wrapper = new WrapperCarreiraListDTO();
    wrapper.setContent(content);
    PageMapper.fillPagination(page, wrapper);
    return wrapper;
  }

  private CarreiraListDTO toDTO(RhVCarreiraEntity v) {
    var dto = new CarreiraListDTO();
    dto.setId(v.getCarreiraId());
    dto.setUuid(v.getCarreiraUuid() != null ? v.getCarreiraUuid().toString() : null);
    dto.setIdFuncionario(v.getFunId());
    dto.setUuidFuncionario(v.getFunUuid() != null ? v.getFunUuid().toString() : null);
    dto.setTipoCarreira(v.getTipoSituacao());
    dto.setTipoCarreiraDesc(v.getTipoSituacaoDesc());
    dto.setVinculo(v.getVinculoDesc());
    dto.setCarreira(v.getCarreiraDesc());
    dto.setCargo(v.getCargoDesc());
    dto.setEscalao(v.getEscalaoDesc());
    dto.setSalario(v.getSalario() != null ? v.getSalario().toString() : null);
    dto.setSituacaoLaboralId(v.getSituacaoLaboralId());
    dto.setSituacaoLaboral(v.getSituacaoLaboralDesc());
    dto.setDataInicio(v.getDataInicio());
    dto.setDataFim(v.getDataFim());
    dto.setProcessamento(Integer.valueOf(1).equals(v.getFlgProcessa()) ? "SIM" : "NAO");
    dto.setEstado(v.getEstadoCarreira());
    dto.setEstadoDesc(Estado.fromCode(v.getEstadoCarreira()).map(Estado::getDescription).orElse(null));
    return dto;
  }

  public CarreiraResponseDTO getCarreiraById(String carreiraId) {
    var uuid = UUID.fromString(carreiraId);
    // Valida que a carreira existe (404 claro). O detalhe usa o tipos_relacionamento DA PRÓPRIA
    // carreira — o que foi criado no registo com carreira_id = esta carreira (novoTiprel.setCarreiraId).
    // Em qualquer estado, porque o pendente tem est_act_adm=0 (o findByCarreiraId_uuid só devolve o ativo).
    carreiraEntityRepository.findByUuidOrThrow(uuid);
    var tr = tiposRelacionamentoEntityRepository.findFirstByCarreiraId_UuidOrderByIdDesc(uuid).orElse(null);
    if (tr == null) {
      throw IgrpResponseStatusException.notFound(
          "Não existe relação laboral para a carreira: " + carreiraId);
    }
    return getCarreiraResponseDTO(tr);
  }

  public CarreiraResponseDTO getCarreiraAtualByUuidFuncionario(String uuidFuncionario) {
    var tr = funcionarioRules.getTipoRelacionamentoAtual(parseUuid(uuidFuncionario, "uuidFuncionario"));
    return getCarreiraResponseDTO(tr);
  }

  private @NonNull CarreiraResponseDTO getCarreiraResponseDTO(TiposRelacionamentoEntity tr) {
    var car = tr.getCarreiraId();
    var contrato = tr.getContrVinculoId();
    var fun = tr.getFunId();
    var vinc = contrato.getVinculoId();
    var carrPcc = car != null ? car.getCarrPccsId() : null;
    var esc = car != null ? car.getEscalaoId() : null;
    // Ponto 20: a categoria vem de car.getCategoriaId() (antes usava o escalão -> categoriaId==escalaoId).
    var categoria = car != null ? car.getCategoriaId() : null;
    var cargo = car != null ? car.getCargoId() : null;

    var dto = new CarreiraResponseDTO();
    dto.setMoeda(tr.getMoeda());
    dto.setFuncionarioId(fun != null && fun.getUuid() != null ? fun.getUuid().toString() : null);
    dto.setTipoCarreira(car!=null ? car.getTipoSituacao() : null);
    dto.setTipoContratoId(contrato.getTpContratoId().getId());
    dto.setCargoId(cargo != null ? cargo.getId() : null);
    dto.setCargoDesc(cargo != null ? cargo.getNome() : null);
    dto.setCarreiraId(carrPcc != null ? carrPcc.getId() : null);
    dto.setCarreiraDesc(carrPcc != null ? carrPcc.getNome() : null);
    dto.setEscalaoId(esc != null ? esc.getId() : null);
    dto.setEscalaoDesc(esc != null ? (esc.getNivelReferencia() != null ? esc.getNivelReferencia() : "") + esc.getEscalao() : null);
    dto.setSalario(car!=null ? car.getSalario().toString() : null);
    dto.setTipoVinculoLaboral(vinc != null ? vinc.getNome() : null);
    dto.setTipoVinculoLaboralId(vinc != null ? vinc.getId() : null);
    dto.setSituacaoLaboralId(tr.getSituacLaboralId().getSituacaoLaboralId().getId());

    dto.setDataInicio(DateFormatter.localDateToString(tr.getDataInicio()));
    dto.setDataFim(DateFormatter.localDateToString(tr.getDataFim()));
    dto.setProcessaSalarioNestaCarreira(tr.getFlgProcessa()== 1 ? "SIM" : "NAO");
    // PROCESSAMENTO (vista RH_V_CARREIRA): true se a carreira ja foi processada em folha
    dto.setProcessamento(car != null && processamentoFuncionarioRepository.existsByTiprel_CarreiraId_Id(car.getId()));
    dto.setCategoriaId(categoria != null ? categoria.getId() : null);
    dto.setCategoriaDesc(categoria != null ? categoria.getNome() : null);

    if (car != null) {
      dto.setEstado(car.getEstado().getCode());
      dto.setEstadoDesc(car.getEstado().getDescription());
    }

    var encargos = new ArrayList<EncargosDescontosRespDTO>();
    var paymentsNotNeedInDetails = List.of("INPS", "IUR");

    var data = defPagamentoEntityRepository.findByFunIdAndEstado(fun, tr.getEstado())
        .stream()
        .filter(obj -> !paymentsNotNeedInDetails.contains(obj.getTmId().getTipo()))
        .toList();

    data.forEach(obj -> {
      var row = new EncargosDescontosRespDTO();
      row.setId(obj.getId());
      row.setTipoEncargoId(obj.getTmId() != null ? obj.getTmId().getId() : null);
      row.setTipoEncargoDesc(obj.getTmId() != null ? obj.getTmId().getDescricao() : null);
      row.setValor(obj.getValor());
      row.setPercentagem(obj.getPercentagem());
      row.setDataInicio(obj.getDataInicio());
      row.setDataFim(obj.getDataFim());
      row.setObservacoes(obj.getObs());
      encargos.add(row);
    });
    dto.setEncargosDescontos(encargos);

    var subsidios = new ArrayList<SubsidioRespDTO>();
    var subsidioDBData = definicaoRemuneracaoEntityRepository.findByFunIdAndEstado(fun, tr.getEstado());
    subsidioDBData.forEach(obj -> {
      var row = new SubsidioRespDTO();
      row.setId(obj.getId());
      row.setTipoSubsidioId(obj.getTmId() != null ? obj.getTmId().getId() : null);
      row.setTipoSubsidioDesc(obj.getTmId() != null ? obj.getTmId().getDescricao() : null);
      row.setValor(obj.getValor());
      row.setPercentagem(obj.getPercentagem());
      row.setObservacoes(obj.getObs());
      subsidios.add(row);
    });
    dto.setSubsidios(subsidios);

    return dto;
  }

  private UUID parseUuid(String raw, String field) {
    try {
      return UUID.fromString(raw);
    } catch (Exception e) {
      throw IgrpResponseStatusException.badRequest("UUID inválido para " + field + ": " + raw);
    }
  }
}


