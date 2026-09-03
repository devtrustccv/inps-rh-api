package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.dto.FuncionarioListDTO;
import cv.inps.rh.funcionario.application.dto.WrapperListaFuncionarioDTO;
import cv.inps.rh.funcionario.application.queries.GetListFuncionariosQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.RhVDossieEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.RhVDossieEntity_;
import cv.inps.rh.shared.infrastructure.persistence.repository.RhVDossieEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class FuncionarioReadService {

  private final RhVDossieEntityRepository dossieRepository;

  @Transactional(readOnly = true)
  public WrapperListaFuncionarioDTO getListFuncionarios(GetListFuncionariosQuery query) {

    int pageNumber = Integer.parseInt(query.getPageNumber());
    int pageSize = Integer.parseInt(query.getPageSize());

    // Specification para filtros dinâmicos
    Specification<RhVDossieEntity> spec = (root, _, cb) -> {

      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.equal(root.get(RhVDossieEntity_.ultimoVinculo), 1));

      if (StringUtils.hasText(query.getNome())) {
        var nome = query.getNome().toLowerCase();
        predicates.add(cb.like(cb.lower(root.get(RhVDossieEntity_.nome)), "%" + nome + "%"));
      }

      if (query.getDireccao() != null)
        predicates.add(cb.equal(root.get(RhVDossieEntity_.direcaoId), query.getDireccao()));

      if (query.getSeccao() != null)
        predicates.add(cb.equal(root.get(RhVDossieEntity_.seccaoId), query.getSeccao()));

      if (query.getLocal() != null)
        predicates.add(cb.equal(root.get(RhVDossieEntity_.localTrabalhoId), query.getLocal()));

      if (query.getCargo() != null)
        predicates.add(cb.equal(root.get(RhVDossieEntity_.cargoId), query.getCargo()));

      if (query.getCarreira() != null)
        predicates.add(cb.equal(root.get(RhVDossieEntity_.carreiraId), query.getCarreira()));

      // Por defeito lista activos, pendentes, em correção e inactivos (A, P, C, I) — o maker precisa
      // de ver os registos devolvidos para correção (C) sem ter de filtrar explicitamente.
      // Se for enviado um estado específico, filtra por esse.
      if (StringUtils.hasText(query.getEstado())) {
        predicates.add(cb.equal(root.get(RhVDossieEntity_.estadoColaborador), query.getEstado()));

        // --- DESLIGADO (a aguardar decisão do analista) — "Estado do Colaborador" Pendente na grelha ---
        // Acompanha o estadoColaboradorExibido() no fim da classe: se a grelha mostrar 'P' enquanto há
        // validação aberta, o filtro tem de casar com o que é MOSTRADO, senão filtrar "Ativo" devolve
        // linhas a dizer "Pendente". Reativar EM CONJUNTO com esse método, substituindo o predicado
        // simples acima por este:
        //
        // var estadoPedido = query.getEstado();
        // var temValidacaoAberta = cb.equal(root.get(RhVDossieEntity_.estadoValidacao), Estado.P.name());
        // if (Estado.P.name().equals(estadoPedido)) {
        //   predicates.add(cb.or(cb.equal(root.get(RhVDossieEntity_.estadoColaborador), Estado.P.name()),
        //       temValidacaoAberta));
        // } else {
        //   predicates.add(cb.and(cb.equal(root.get(RhVDossieEntity_.estadoColaborador), estadoPedido),
        //       cb.not(temValidacaoAberta)));
        // }
      } else {
        predicates.add(root.get(RhVDossieEntity_.estadoColaborador).in(Estado.A.name(), Estado.P.name(), Estado.C.name(), Estado.I.name()));
      }

      if (StringUtils.hasText(query.getDataInicio())) {
        var di = DateFormatter.stringToLocalDate(query.getDataInicio());
        predicates.add(cb.greaterThanOrEqualTo(root.get(RhVDossieEntity_.dataInicioContrato), di));
      }

      if (StringUtils.hasText(query.getDataFim())) {
        var df = DateFormatter.stringToLocalDate(query.getDataFim());
        predicates.add(cb.lessThanOrEqualTo(root.get(RhVDossieEntity_.dataFimContrato), df));
      }

      if (query.getTipoVinculoLaboral() != null)
        predicates.add(cb.equal(root.get(RhVDossieEntity_.vinculoId), query.getTipoVinculoLaboral()));

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.DESC, "id"));
    var page = dossieRepository.findAll(spec, pageable);

    //mapear para DTO
    List<FuncionarioListDTO> content = page.getContent()
        .stream()
        .map(d -> {
          FuncionarioListDTO dto = new FuncionarioListDTO();
          dto.setId(d.getFunId());
          dto.setUuid(d.getFunUuid() != null ? d.getFunUuid().toString() : null);
          dto.setNome(d.getNome());
          dto.setNumColaborador(d.getIdColaborador() != null ? d.getIdColaborador().toString() : null);
          dto.setCargo(d.getCargoDesc());
          dto.setDireccao(d.getDirecaoDesc());
          dto.setSeccao(d.getSeccaoDesc());
          String carreiraCategoria = Stream.of(d.getCarreiraDesc(), d.getCategoriaDesc())
              .filter(Objects::nonNull)
              .filter(s -> !s.isBlank())
              .collect(Collectors.joining("/"));

          dto.setCarreiraCategoria(
              carreiraCategoria.isEmpty() ? null : carreiraCategoria
          );
          dto.setDataInicio(d.getDataInicioContrato() != null ? d.getDataInicioContrato().toString() : null);
          dto.setVinculoId(d.getVinculoId());
          // DESLIGADO (a aguardar decisão do analista): mostrar 'P' enquanto há validação aberta.
          // Para reativar, trocar por `var estadoColaborador = estadoColaboradorExibido(d);` e usar essa
          // variável nas duas linhas abaixo — ver o método no fim da classe e o filtro na Specification.
          var estadoColaborador = d.getEstadoColaborador();
          dto.setEstadoColaborador(estadoColaborador);
          dto.setEstadoColaboradorDesc(
              Estado.fromCode(estadoColaborador)
                  .map(Estado::getDescription)
                  .orElse("Desconhecido")
          );
          // "Estado do Registo" indica se o registo do colaborador está validado ou não
          // (maker-checker do registo) e tem fonte própria — RH_V_DOSSIE.ESTADO_VALIDACAO.
          // Não confundir com o "Estado do Colaborador" (A/I/C), acima.
          dto.setEstadoRegisto(d.getEstadoValidacao());
          dto.setEstadoRegistoDesc(Estado.fromCode(d.getEstadoValidacao())
              .map(Estado::getDescription)
              .orElse("Desconhecido"));
          return dto;
        }).toList();

    var wrapper = new WrapperListaFuncionarioDTO();
    PageMapper.fillPagination(page, wrapper);
    wrapper.setContent(content);

    return wrapper;
  }

  /*
   * DESLIGADO — a aguardar decisão do analista (sessão 4). NÃO apagar: volta a ligar-se assim que a
   * regra for confirmada.
   *
   * "Estado do Colaborador" tal como a grelha o mostraria: Pendente enquanto existir uma validação
   * aberta, senão o estado de domínio real (A/I/C).
   *
   * Pedido original: ao enviar uma alteração para validação, tanto o "Estado do Registo" como o
   * "Estado do Colaborador" devem aparecer Pendentes. Derivar aqui, na leitura, em vez de escrever 'P'
   * em RH_T_FUNCIONARIOS.ESTADO — gravar 'P' destruiria o estado de domínio (A/I), que no ramo
   * não-processado é irrecuperável na rejeição (a situação é editada in place, por regra de negócio do
   * Caso de uso), e faria falhar os guards de guardComboInativarAtivar, que comparam == A / == I.
   *
   * Validado live antes de ser desligado (colaborador 958931): BD manteve estado=A com
   * estado_validacao=P; a grelha mostrou P/Pendente nas duas colunas; filtro estado=A devolveu 4 linhas
   * excluindo o pendente, estado=P devolveu-o.
   *
   * Para reativar: descomentar este método, chamá-lo no mapeamento (`estadoColaboradorExibido(d)`) e
   * repor o predicado alinhado na Specification — os dois em conjunto, nunca só um.
   *
   * private String estadoColaboradorExibido(RhVDossieEntity dossie) {
   *   return Estado.P.name().equals(dossie.getEstadoValidacao())
   *       ? Estado.P.name()
   *       : dossie.getEstadoColaborador();
   * }
   */
}
