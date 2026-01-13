package cv.inps.rh.funcionario.application.service.remuneracao;

import cv.inps.rh.funcionario.application.dto.NovoPagamentoRequestDTO;
import cv.inps.rh.funcionario.application.dto.NovoRemuneracaoRequestDTO;
import cv.inps.rh.funcionario.application.dto.RenumeracaoReqDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListRenumeracaoDTO;
import cv.inps.rh.funcionario.application.queries.GetListRenumeracoesQuery;
import cv.inps.rh.funcionario.application.queries.GetPagamentosDescontosByIdQuery;
import cv.inps.rh.funcionario.application.queries.GetRenumeracaoByIdQuery;
import cv.inps.rh.funcionario.infrastructure.mappers.DefinicaoRemuneracaoMapper;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefPagamentoEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoMovimentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DefinicaoRemuneracaoEntityRepository;
import jakarta.persistence.criteria.Join;
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

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RenumeracoesReadService {

  private final DefinicaoRemuneracaoEntityRepository definicaoRemuneracaoEntityRepository;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;

  private final DefPagamentoEntityRepository defPagamentoEntityRepository;

  @Transactional(readOnly = true)
  public WrapperListRenumeracaoDTO getListRenumeracoes(GetListRenumeracoesQuery query) {

    int pageNumber = query.getPageNumber() != null ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = query.getPageSize() != null ? Integer.parseInt(query.getPageSize()) : 20;

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).valor();

    Specification<DefinicaoRemuneracaoEntity> spec = (root, cq, cb) -> {
      List<Predicate> predicates = new java.util.ArrayList<>();

      /*Join<DefinicaoRemuneracaoEntity, TipoMovimentoEntity> tm = root.join("tmId");
      predicates.add(cb.equal(tm.get("tipo"), "REM"));*/

      Join<DefinicaoRemuneracaoEntity, FuncionarioEntity> fun = root.join("funId");
      predicates.add(cb.equal(fun.get("uuid"), idFuncionario));

      if (StringUtils.hasText(query.getEstado())) {
        try {
          Estado estado = Estado.fromCodeOrThrow(query.getEstado());
          predicates.add(cb.equal(root.get("estado"), estado));
        } catch (Exception ignored) {}
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

    Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "dataInicio"));
    Page<DefinicaoRemuneracaoEntity> page = definicaoRemuneracaoEntityRepository.findAll(spec, pageable);

    var content = page.getContent().stream()
        .map(definicaoRemuneracaoMapper::toDTO)
        .toList();

    var wrapper = new WrapperListRenumeracaoDTO();
    wrapper.setContent(content);
    wrapper.setPageNumber(page.getNumber());
    wrapper.setPageSize(page.getSize());
    wrapper.setTotalElements(page.getTotalElements());
    wrapper.setTotalPages(page.getTotalPages());
    wrapper.setFirst(page.isFirst());
    wrapper.setLast(page.isLast());

    return wrapper;
  }

  public NovoRemuneracaoRequestDTO getRenumeracaoById(GetRenumeracaoByIdQuery query){

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).valor();
    var renumeracaoId = IdentificadorUnico.from(query.getRenumeracaoId()).valor();

    var renumeracao = definicaoRemuneracaoEntityRepository.findByUuidOrThrow(renumeracaoId);

    if(!Objects.equals(renumeracao.getFunId().getUuid(), idFuncionario)){
      throw IgrpResponseStatusException.badRequest("Renumeracao nao pertence ao funcionario :" +renumeracao.getFunId().getNome());
    }

    var response = new NovoRemuneracaoRequestDTO();
    response.setPercentagem(renumeracao.getPercentagem());
    response.setValor(renumeracao.getValor());
    response.setMoeda(renumeracao.getMoeda());
    response.setMovimentoId(renumeracao.getTmId().getId());
    response.setDataInicio(renumeracao.getDataInicio()!=null ? DateFormatter.localDateToString(renumeracao.getDataInicio()) : null);
    response.setDataFim(renumeracao.getDataFim()!=null ? DateFormatter.localDateToString(renumeracao.getDataFim()) : null);
    response.setObservacao(renumeracao.getObs());

    return response;


  }

  public NovoPagamentoRequestDTO getPagamentoById(GetPagamentosDescontosByIdQuery query){

    var idFuncionario = IdentificadorUnico.from(query.getIdFuncionario()).valor();
    var pagamentoId = IdentificadorUnico.from(query.getPagamentoId()).valor();

    var pagamento = defPagamentoEntityRepository.findByUuidOrThrow(pagamentoId);

    if(!Objects.equals(pagamento.getFunId().getUuid(), idFuncionario)){
      throw IgrpResponseStatusException.badRequest("Pagamento Desconto nao pertence ao funcionario :" +pagamento.getFunId().getNome());
    }

    var response = new NovoPagamentoRequestDTO();
    response.setPercentagem(pagamento.getPercentagem());
    response.setValor(pagamento.getValor());
    response.setMovimentoId(pagamento.getTmId().getId());
    response.setDataInicio(pagamento.getDataInicio()!=null ? DateFormatter.localDateToString(pagamento.getDataInicio()) : null);
    response.setDataFim(pagamento.getDataFim()!=null ? DateFormatter.localDateToString(pagamento.getDataFim()) : null);
    response.setObservacao(pagamento.getObs());
    response.setNib(pagamento.getNib() != null ? pagamento.getNib() : null);
    response.setNif(pagamento.getNif() != null ? pagamento.getNif() : null);
    response.setEntidade(pagamento.getEntId()!= null ? pagamento.getEntId().getId(): null);
    response.setBanco(pagamento.getRhbId()!=null ? pagamento.getRhbId().getId(): null);

    return response;


  }
}
