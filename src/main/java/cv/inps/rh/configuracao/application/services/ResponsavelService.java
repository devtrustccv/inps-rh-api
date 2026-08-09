package cv.inps.rh.configuracao.application.services;

import cv.inps.rh.configuracao.application.dto.*;
import cv.inps.rh.configuracao.application.queries.GetResponsaveisEmailsQuery;
import cv.inps.rh.configuracao.application.queries.GetResponsaveisQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.DirecaoEntity_;
import cv.inps.rh.shared.infrastructure.persistence.entity.ResponsavelEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ResponsavelEntity_;
import cv.inps.rh.shared.infrastructure.persistence.entity.SecaoEntity_;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Transactional
@Service
public class ResponsavelService {

  private final ResponsavelEntityRepository responsavelEntityRepository;
  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final SecaoEntityRepository secaoEntityRepository;
  private final DirecaoEntityRepository instituicaoEntityRepository;
  private final MobilidadeEntityRepository mobilidadeEntityRepository;

  public ResponsavelService(ResponsavelEntityRepository responsavelEntityRepository, FuncionarioEntityRepository funcionarioEntityRepository, SecaoEntityRepository secaoEntityRepository, DirecaoEntityRepository instituicaoEntityRepository, MobilidadeEntityRepository mobilidadeEntityRepository) {
    this.responsavelEntityRepository = responsavelEntityRepository;
    this.funcionarioEntityRepository = funcionarioEntityRepository;
    this.secaoEntityRepository = secaoEntityRepository;
    this.instituicaoEntityRepository = instituicaoEntityRepository;
    this.mobilidadeEntityRepository = mobilidadeEntityRepository;
  }

  public void saveResponsaveis(AssociarResponsaveisRequestDTO request) {
    request.getResponsaveis().forEach(row -> {
      var responsavel = ValidationUtil.isValidNumberId(row.getIdResponsavel()) ? responsavelEntityRepository.findByIdOrThrow(row.getIdResponsavel()) : new ResponsavelEntity();
      responsavel.setInstitId(instituicaoEntityRepository.findByIdOrThrow(row.getIdDirecao()));
      responsavel.setFunId(funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(row.getIdFuncionario())));
      responsavel.setSecaoId(StringUtils.hasText(row.getIdSeccao()) ? secaoEntityRepository.findByUuidOrThrow(UUID.fromString(row.getIdSeccao())) : null);
      responsavel.setEstado(Estado.A.name());
      responsavelEntityRepository.save(responsavel);
    });
  }

  public void removerSecaoDirecaoAsociation(Long responsavelId) {
    var responsavel = responsavelEntityRepository.findByIdOrThrow(responsavelId);
    responsavel.setEstado(Estado.I.name());
    responsavelEntityRepository.save(responsavel);
  }

  public ResponsaveisDirecaoResponseDTO getResponsavelData(Long institutoId) {

    var savedResponsibles = responsavelEntityRepository.findAllSectionsByDirection(institutoId);

    var rows = savedResponsibles.stream()
        .filter(obj -> obj.secaoId() != null)
        .map(obj -> {
          var o = new ResponsavelResponseDTO();
          o.setResponsavelId(obj.responsavelId());
          o.setSeccao(obj.secaoNome());
          o.setSeccaoId(obj.secaoId().toString());
          o.setEmail(obj.email());
          o.setFuncionarioId(obj.funcionarioId() != null ? obj.funcionarioId().toString() : null);
          return o;
        })
        .toList();

    var response = new ResponsaveisDirecaoResponseDTO();
    response.setContent(rows);
    return response;
  }


  public WrapperListResponsaveisDTO getResponsaveis(GetResponsaveisQuery query) {

   /* int pageNumber = StringUtils.hasText(query.getPageNumber()) ? Integer.parseInt(query.getPageNumber()) : 0;
    int pageSize = StringUtils.hasText(query.getPageSize()) ? Integer.parseInt(query.getPageSize()) : 20;

    Specification<ResponsavelEntity> spec = (root, _, cb) -> {
      var predicates = new ArrayList<Predicate>();

      if (StringUtils.hasText(query.getNomeFuncionario())) {
        var value = "%" + query.getNomeFuncionario().toLowerCase() + "%";
        predicates.add(cb.like(cb.lower(root.get(ResponsavelEntity_.funId).get(FuncionarioEntity_.NOME)), value));
      }

      if (StringUtils.hasText(query.getNomeInstituicao())) {
        var value = "%" + query.getNomeInstituicao().toLowerCase() + "%";
        predicates.add(cb.like(cb.lower(root.get(ResponsavelEntity_.institId).get(DirecaoEntity_.NOME)), value));
      }

      if (query.getIdInstituicao() != null) {
        predicates.add(cb.equal(root.get(ResponsavelEntity_.institId).get(DirecaoEntity_.ID), query.getIdInstituicao()));
      }

      if (StringUtils.hasText(query.getNomeSecccao())) {
        var value = "%" + query.getNomeSecccao().toLowerCase() + "%";
        predicates.add(cb.like(cb.lower(root.get(ResponsavelEntity_.secaoId).get(SecaoEntity_.NOME)), value));
      }

      if (query.getIdSeccao() != null) {
        predicates.add(cb.equal(root.get(ResponsavelEntity_.secaoId).get(SecaoEntity_.ID), query.getIdSeccao()));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    var pageable = PageRequest.of(pageNumber, pageSize, Sort.by(Sort.Direction.ASC, ResponsavelEntity_.ID));
    var page = responsavelEntityRepository.findAll(spec, pageable);

    var content = page.getContent().stream().map(e -> {
      var dto = new ResponsavelResponseDTO();
      dto.setIdResponsavel(e.getId());

      var instit = e.getInstitId();
      dto.setIdDirecao(instit.getId());
      dto.setNomeDirecao(instit.getNome());

      var fun = e.getFunId();
      dto.setIdFuncionario(fun.getUuid().toString());
      dto.setNomeFuncionario(fun.getNome());

      var secao = e.getSecaoId();
      if (secao != null) {
        dto.setIdSeccao(secao.getId().toString());
        dto.setNomeSeccao(secao.getNome());
      }

      dto.setEmail(e.getEmail());
      return dto;
    }).toList();

    var wrapper = new WrapperListResponsaveisDTO();
    PageMapper.fillPagination(page, wrapper);
    wrapper.setContent(content);*/
    return null;
  }

  /**
   * Emails de RH_T_RESPONSAVEL para o multiselect "Email do Responsável" do ecrã de notificação.
   *
   * <p>Quando vem {@code funcionarioId}, a direção/secção é deduzida da mobilidade activa do
   * colaborador — é o que o ecrã tem em mão. Sem filtro nenhum devolve todos os responsáveis
   * com email, o que é aceitável porque a tabela é pequena (uma linha por direção/secção).</p>
   *
   * <p>Linhas sem email ficam de fora: no multiselect seriam opções impossíveis de escolher.</p>
   */
  @Transactional(readOnly = true)
  public List<ResponsavelEmailDTO> getResponsaveisEmails(GetResponsaveisEmailsQuery query) {

    Long idInstituicao = query.getIdInstituicao();
    Long idSeccao = query.getIdSeccao();

    if (StringUtils.hasText(query.getFuncionarioId())) {
      var funcionario = funcionarioEntityRepository.findByUuidOrThrow(UUID.fromString(query.getFuncionarioId()));
      var mobilidade = mobilidadeEntityRepository.findByFunIdAndEstadoAndDataFimIsNull(funcionario, Estado.A);

      if (mobilidade == null || mobilidade.getInstidId() == null) {
        // Sem colocação conhecida não há como escolher responsáveis; devolver a lista toda seria
        // pior do que devolver nada, porque sugeriria chefias de outras direções.
        return List.of();
      }

      idInstituicao = mobilidade.getInstidId().getId();
      idSeccao = mobilidade.getSecaoId() != null ? mobilidade.getSecaoId().getId() : null;
    }

    var filtroInstituicao = idInstituicao;
    var filtroSeccao = idSeccao;

    Specification<ResponsavelEntity> spec = (root, _, cb) -> {
      var predicates = new ArrayList<Predicate>();
      predicates.add(cb.isNotNull(root.get(ResponsavelEntity_.email)));

      if (filtroInstituicao != null) {
        predicates.add(cb.equal(root.get(ResponsavelEntity_.institId).get(DirecaoEntity_.ID), filtroInstituicao));
      }
      if (filtroSeccao != null) {
        predicates.add(cb.equal(root.get(ResponsavelEntity_.secaoId).get(SecaoEntity_.ID), filtroSeccao));
      }

      return cb.and(predicates.toArray(new Predicate[0]));
    };

    return responsavelEntityRepository.findAll(spec).stream()
        .filter(e -> StringUtils.hasText(e.getEmail()))
        .map(e -> {
          var dto = new ResponsavelEmailDTO();
          dto.setIdResponsavel(e.getId());
          dto.setEmail(e.getEmail().trim());

          var fun = e.getFunId();
          dto.setNome(fun != null && StringUtils.hasText(fun.getNome()) ? fun.getNome() : e.getEmail().trim());

          var direcao = e.getInstitId();
          if (direcao != null) dto.setDirecao(direcao.getNome());

          var secao = e.getSecaoId();
          if (secao != null) dto.setSeccao(secao.getNome());

          return dto;
        })
        .toList();
  }

}
