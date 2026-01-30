package cv.inps.rh.emprestimo.domain.service;

import cv.inps.rh.emprestimo.application.dto.EmprestimoListDTO;
import cv.inps.rh.emprestimo.application.dto.EmprestimoListRowDTO;
import cv.inps.rh.emprestimo.application.dto.InformacaoEmprestimoRequestDTO;
import cv.inps.rh.emprestimo.application.dto.PedidoEmprestimoDTO;
import cv.inps.rh.emprestimo.application.queries.ListarEmprestimosQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.EmprestimoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.EmprestimoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamEmprestimoEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class EmprestimoReadService {

  private final ParamEmprestimoEntityRepository paramEmprestimoEntityRepository;
  private final EmprestimoEntityRepository emprestimoEntityRepository;

  public List<InformacaoEmprestimoRequestDTO> getAllConfiguracaoEmprestimo() {
    return paramEmprestimoEntityRepository.findAll()
        .stream()
        .map(entity -> new InformacaoEmprestimoRequestDTO(
            entity.getCarrPccs().getUuid().toString(),
            entity.getValorLimite(),
            entity.getNumeroLimite(),
            entity.getEstado(),
            entity.getUuid()
        ))
        .toList();
  }

  public PedidoEmprestimoDTO getPedidoEmprestimoByUuid(String uuid) {

    var entity = emprestimoEntityRepository.findByUuidOrThrow(uuid);

    var dto = new PedidoEmprestimoDTO();
    dto.setMarca(entity.getMarca());
    dto.setAnoFabrico(entity.getAnoFabrico());
    dto.setCilindrada(entity.getCilincrada());
    dto.setTipoviatura(entity.getTipoViatura());
    dto.setCombustivel(entity.getCombustivel());
    dto.setEstadoViatura(entity.getEstadoViatura());
    dto.setValorEmprestimo(entity.getValorEmprestimo());
    dto.setNumeroPrestacoes(entity.getNrPrestacao());
    dto.setFuncionarioId(entity.getTiprel().getFunId().getUuid().toString());

    return dto;
  }

  public EmprestimoListDTO listarEmprestimos(ListarEmprestimosQuery query) {

    var page = Integer.parseInt(query.getPage());
    var size = Integer.parseInt(query.getSize());

    var pageable = PageRequest.of(page, size, Sort.by("dataInicio").descending());

    Specification<EmprestimoEntity> specification = (root, cq, cb) -> {

      var predicates = new ArrayList<Predicate>();

      if (StringUtils.hasText(query.getTipoEmprestimo()))
        predicates.add(cb.equal(root.get("tipoEmprestimo"), query.getTipoEmprestimo()));


      if (StringUtils.hasText(query.getEstadoEmprestimo()))
        predicates.add(cb.equal(root.get("estado"), query.getEstadoEmprestimo()));


      if (StringUtils.hasText(query.getDataInicio()) && StringUtils.hasText(query.getDataFim()))
        predicates.add(cb.between(root.get("dataInicio"), LocalDate.parse(query.getDataInicio()), LocalDate.parse(query.getDataFim()))
        );

      if (StringUtils.hasText(query.getDireccaoId())) {
        var relacionamento = root.join("tiprel");
        predicates.add(
            cb.equal(relacionamento.get("mobId").get("instidId").get("id"), Long.valueOf(query.getDireccaoId()))
        );
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var pageResult = emprestimoEntityRepository.findAll(specification, pageable);

    var response = new EmprestimoListDTO();
    PageMapper.fillPagination(pageResult, response);
    response.setContent(pageResult.getContent()
        .stream()
        .map(e -> {
          var dto = new EmprestimoListRowDTO();
          dto.setEstado(e.getEstado());
          dto.setEstadoDesc(Estado.codeDescriptionMap().get(e.getEstado()));
          dto.setTipoEmprestimo(e.getTipoEmprestimo());
          dto.setRenegociacaoDivida(e.getRenogociacao());
          dto.setValorConcedido(e.getValorEmprestimo());
          dto.setNumeroPrestacoesPagas(e.getNrPrestacao());
          dto.setValorPago(e.getValorPago());
          dto.setDataInicioEmprestimo(e.getDataInicio());
          dto.setEmprestimoId(e.getUuid());
          dto.setSaldoEmDivida(e.getValorDivida());
          dto.setDataInicioEmprestimo(e.getDataInicio());
          var funId = e.getTiprel().getFunId();
          dto.setFuncionarioId(funId.getUuid().toString());
          dto.setNomeColaborador(funId.getNome());
          return dto;
        })
        .toList());

    return response;
  }
}

