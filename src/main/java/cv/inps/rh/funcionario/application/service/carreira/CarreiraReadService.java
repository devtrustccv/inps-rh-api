package cv.inps.rh.funcionario.application.service.carreira;

import cv.inps.rh.funcionario.application.dto.*;
import cv.inps.rh.funcionario.application.queries.GetCarreiraListQuery;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.service.DominioService;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefPagamentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefinicaoRemuneracaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.TiposRelacionamentoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import jakarta.persistence.criteria.Join;
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
  private final DefPagamentoEntityRepository defPagamentoEntityRepository;
  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final DominioService dominioService;
  private final FuncionarioRules funcionarioRules;

  @Transactional(readOnly = true)
  public WrapperCarreiraListDTO list(GetCarreiraListQuery query) {

    var pageNumber = Integer.parseInt(query.getPageNumber());
    var pageSize = Integer.parseInt(query.getPageSize());

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).valor();

    Specification<TiposRelacionamentoEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new java.util.ArrayList<>();

      Join<TiposRelacionamentoEntity, FuncionarioEntity> fun = root.join("funId");
      predicates.add(cb.equal(fun.get("uuid"), idFuncionario));

      var estados = List.of(Estado.A, Estado.P, Estado.I);
      predicates.add(
          root.get("estado").in(estados)
      );

      Join<TiposRelacionamentoEntity, CarreiraEntity> carreira = root.join("carreiraId");

      if (StringUtils.hasText(query.getTipoCarreira())) {
        predicates.add(cb.equal(carreira.get("tipoSituacao"), query.getTipoCarreira()));
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
    var page = tiposRelacionamentoEntityRepository.findAll(spec, pageable);

    var tipoMovimentoLaboralDomain = dominioService.getDominioMap("TIPO_MOV_LABORAL");

    List<CarreiraListDTO> content = page.getContent().stream().map(tr -> {
      var dto = new CarreiraListDTO();
      var car = tr.getCarreiraId();
      var fun = tr.getFunId();
      var vinc = tr.getContrVinculoId().getVinculoId();
      var carrPcc = tr.getCarreiraId()!=null ? tr.getCarreiraId().getCarrPccsId() : null;
      var cargo = tr.getCargoId();
      var esc = tr.getCarreiraId()!=null ? tr.getCarreiraId().getEscalaoId(): null;
      var sitLab = tr.getSituacLaboralId();

      dto.setId(car != null ? car.getId() : null);
      dto.setUuid(car != null && car.getUuid() != null ? car.getUuid().toString() : null);
      dto.setIdFuncionario(fun != null ? fun.getId() : null);
      dto.setUuidFuncionario(fun != null && fun.getUuid() != null ? fun.getUuid().toString() : null);
      dto.setVinculo(vinc != null ? vinc.getNome() : null);
      dto.setCarreira(carrPcc != null ? carrPcc.getNome() : null);
      dto.setCargo(cargo != null ? cargo.getNome() : null);
      dto.setEscalao(esc != null ? esc.getEscalao() : null);
      dto.setSalario(car != null && car.getSalario() != null ? car.getSalario().toString() : null);
      dto.setSituacaoLaboral(sitLab != null ? sitLab.getSituacaoLaboralId().getNome() : null);
      dto.setDataInicio(tr.getDataInicio());
      dto.setDataFim(tr.getDataFim());
      dto.setProcessamento(tr.getFlgProcessa()== 1 ? "SIM" : "NAO");
      dto.setEstado(car != null && car.getEstado() != null ? car.getEstado().getCode() : null);
      dto.setEstadoDesc(car != null && car.getEstado() != null ? car.getEstado().getDescription() : null);

      if (car != null) {
        dto.setTipoCarreira(car.getTipoSituacao());
        dto.setTipoCarreiraDesc(dominioService.traduzir(tipoMovimentoLaboralDomain, car.getTipoSituacao()));
      }

      return dto;
    }).toList();

    var wrapper = new WrapperCarreiraListDTO();
    wrapper.setContent(content);
    wrapper.setPageNumber(page.getNumber());
    wrapper.setPageSize(page.getSize());
    wrapper.setTotalElements(page.getTotalElements());
    wrapper.setTotalPages(page.getTotalPages());
    wrapper.setFirst(page.isFirst());
    wrapper.setLast(page.isLast());
    return wrapper;
  }

  public CarreiraResponseDTO getCarreiraById(String carreiraId) {
    var tr = tiposRelacionamentoEntityRepository.findByCarreiraId_uuid(UUID.fromString(carreiraId));
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
    var carrPcc = tr.getCarreiraId() !=null ? tr.getCarreiraId().getCarrPccsId() : null;
    var esc = tr.getCarreiraId() !=null ? tr.getCarreiraId().getEscalaoId() : null;
    var categoria = tr.getCarreiraId()!=null ? tr.getCarreiraId().getEscalaoId(): null;

    var dto = new CarreiraResponseDTO();
    dto.setMoeda(tr.getMoeda());
    dto.setFuncionarioId(fun != null && fun.getUuid() != null ? fun.getUuid().toString() : null);
    dto.setTipoCarreira(car!=null ? car.getTipoSituacao() : null);
    dto.setTipoContratoId(contrato.getTpContratoId().getId());
    dto.setCargoId(tr.getCargoId() != null ? tr.getCargoId().getId() : null);
    dto.setCarreiraId(carrPcc != null ? carrPcc.getId() : null);
    dto.setEscalaoId(esc != null ? esc.getId() : null);
    dto.setSalario(car!=null ? car.getSalario().toString() : null);
    dto.setTipoVinculoLaboral(vinc != null ? vinc.getNome() : null);
    dto.setTipoVinculoLaboralId(vinc != null ? vinc.getId() : null);
    dto.setSituacaoLaboralId(tr.getSituacLaboralId().getSituacaoLaboralId().getId());

    dto.setDataInicio(DateFormatter.localDateToString(tr.getDataInicio()));
    dto.setDataFim(DateFormatter.localDateToString(tr.getDataFim()));
    dto.setProcessaSalarioNestaCarreira(tr.getFlgProcessa()== 1 ? "SIM" : "NAO");
    dto.setCategoriaId(categoria != null ? categoria.getId() : null);

    if (car != null) {
      dto.setEstado(car.getEstado().getCode());
      dto.setEstadoDesc(car.getEstado().getDescription());
    }

    var encargos = new ArrayList<EncargosDescontosReqDTO>();
    var paymentsNotNeedInDetails = List.of("INPS", "IUR");

    var data = defPagamentoEntityRepository.findByFunIdAndEstado(fun, tr.getEstado())
        .stream()
        .filter(obj -> !paymentsNotNeedInDetails.contains(obj.getTmId().getTipo()))
        .toList();

    data.forEach(obj -> {
      var row = new EncargosDescontosReqDTO();
      row.setId(obj.getId());
      row.setTipoEncargoId(obj.getTmId() != null ? obj.getTmId().getId() : null);
      row.setValor(obj.getValor());
      row.setDataInicio(obj.getDataInicio());
      row.setDataFim(obj.getDataFim());
      row.setObservacoes(obj.getObs());
      encargos.add(row);
    });
    dto.setEncargosDescontos(encargos);

    var subsidios = new ArrayList<SubsidioReqDTO>();
    var subsidioDBData = definicaoRemuneracaoEntityRepository.findByFunIdAndEstado(fun, tr.getEstado());
    subsidioDBData.forEach(obj -> {
      var row = new SubsidioReqDTO();
      row.setId(obj.getId());
      row.setTipoSubsidioId(obj.getTmId() != null ? obj.getTmId().getId() : null);
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


