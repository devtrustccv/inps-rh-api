package cv.inps.rh.configuracao.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.dto.*;
import cv.inps.rh.configuracao.application.queries.GetResponsaveisQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

  @Transactional
  public void saveResponsaveis(AssociarResponsaveisRequestDTO request) {

    var direcao = instituicaoEntityRepository.findByIdOrThrow(
        request.getDirecaoData().direcaoId()
    );

    /*
     * ============================================================
     * 1. DIRECTION RESPONSIBLE
     * ============================================================
     *
     * A direction responsible is identified by:
     *
     *     responsavel.institId = direcao
     *     responsavel.secaoId  = null
     *
     * There can only be one direction responsible.
     */
    var directionResponsible = responsavelEntityRepository
        .findByInstitId_IdAndSecaoIdIsNull(direcao.getId());

    var funcionarioResponsavelId =
        request.getDirecaoData().funcionarioResponsavelId();

    if (funcionarioResponsavelId != null) {

      var funcionario = funcionarioEntityRepository
          .findByUuidOrThrow(funcionarioResponsavelId);

      ResponsavelEntity responsavel;

      if (directionResponsible.isPresent()) {
        responsavel = directionResponsible.get();
      } else {
        responsavel = new ResponsavelEntity();
        responsavel.setInstitId(direcao);
        responsavel.setSecaoId(null);
        responsavel.setEstado(Estado.A.name());
      }

      responsavel.setFunId(funcionario);

      responsavelEntityRepository.save(responsavel);

    } else if (directionResponsible.isPresent()) {

      /*
       * The direction has no responsible.
       */
      var responsavel = directionResponsible.get();
      responsavel.setEstado(Estado.I.name());

      responsavelEntityRepository.save(responsavel);
    }

    /*
     * ============================================================
     * 2. LOAD CURRENT ACTIVE SECTIONS
     * ============================================================
     */
    var savedSections = secaoEntityRepository
        .findAllByEstadoAndInstId_Id(Estado.A, direcao.getId());

    var savedSectionsById = savedSections.stream()
        .collect(Collectors.toMap(
            SecaoEntity::getUuid,
            Function.identity()
        ));

    /*
     * ============================================================
     * 3. LOAD CURRENT ACTIVE SECTION RESPONSIBLES
     * ============================================================
     */
    var savedResponsaveis =
        responsavelEntityRepository
            .findByInstitIdAndSecaoIdIsNotNullAndEstado(
                direcao,
                Estado.A.name()
            );

    /*
     * ============================================================
     * 4. KEEP TRACK OF WHAT THE CLIENT SENT
     * ============================================================
     *
     * A section can be:
     *
     *   - sent with a responsible
     *   - sent without a responsible
     *   - not sent at all
     *
     * The distinction is important:
     *
     *   sent without responsible:
     *       section remains active,
     *       responsible becomes inactive
     *
     *   not sent:
     *       section becomes inactive,
     *       responsible becomes inactive
     */
    Set<UUID> receivedSectionIds = new HashSet<>();
    Set<UUID> receivedResponsibleSectionIds = new HashSet<>();

    /*
     * ============================================================
     * 5. PROCESS REQUEST SECTIONS
     * ============================================================
     */
    for (var row : request.getSessaoData()) {

      SecaoEntity secao;

      /*
       * --------------------------------------------------------
       * 5.1 CREATE / UPDATE SECTION
       * --------------------------------------------------------
       */
      if (row.idSeccao() == null) {
        secao = new SecaoEntity();
        secao.setUuid(UuidCreator.getTimeOrderedEpoch());
        secao.setInstId(direcao);
        secao.setEstado(Estado.A);
      } else {
        secao = savedSectionsById.get(row.idSeccao());
        if (secao == null) {
          throw IgrpResponseStatusException.badRequest(
              "Secção não encontrada: " + row.idSeccao()
          );
        }
        receivedSectionIds.add(secao.getUuid());
      }

      /*
       * Section name can be changed for both new and existing
       * sections.
       */
      secao.setNome(row.nomeSeccao());
      secao.setNomeNormalizado(normalize(row.nomeSeccao()));
      secao = secaoEntityRepository.save(secao);

      /*
       * New sections must also be considered received.
       */
      receivedSectionIds.add(secao.getUuid());

      /*
       * --------------------------------------------------------
       * 5.2 SECTION RESPONSIBLE
       * --------------------------------------------------------
       *
       * idFuncionario is OPTIONAL.
       *
       * If idFuncionario != null:
       *     create/update responsible.
       *
       * If idFuncionario == null:
       *     section has no responsible.
       *     Any existing responsible must be inactive.
       */
      if (row.idFuncionario() != null) {

        var funcionario = funcionarioEntityRepository.findByUuidOrThrow(row.idFuncionario());

        ResponsavelEntity responsavel;

        if (row.idResponsavel() != null) {

          /*
           * Update existing responsible.
           */
          responsavel = responsavelEntityRepository.findByIdOrThrow(row.idResponsavel());

          /*
           * Make sure the responsible belongs to this
           * direction.
           */
          if (responsavel.getInstitId() == null
              || !Objects.equals(
              responsavel.getInstitId().getId(),
              direcao.getId()
          )) {

            throw IgrpResponseStatusException.badRequest(
                "Responsável não pertence à direção informada"
            );
          }

        } else {
          responsavel = new ResponsavelEntity();
          responsavel.setInstitId(direcao);
          responsavel.setEstado(Estado.A.name());
        }

        responsavel.setFunId(funcionario);
        responsavel.setSecaoId(secao);
        responsavelEntityRepository.save(responsavel);

        /*
         * This section has an active responsible.
         */
        receivedResponsibleSectionIds.add(secao.getUuid());

      } else {

        /*
         * ----------------------------------------------------
         * The section is active but has NO responsible.
         * ----------------------------------------------------
         *
         * If an active responsible currently exists for this
         * section, deactivate it.
         */
        responsavelEntityRepository
            .findAllByInstitId_idAndSecaoId_uuid(direcao.getId(), secao.getUuid())
            .forEach(responsavel -> {
              responsavel.setEstado(Estado.I.name());
              responsavelEntityRepository.save(responsavel);
            });
      }
    }

    /*
     * ============================================================
     * 6. DEACTIVATE SECTIONS NOT SENT BY CLIENT
     * ============================================================
     *
     * Any active section from the database that was not sent by
     * the client is considered removed.
     */
    for (var savedSection : savedSections) {

      if (!receivedSectionIds.contains(savedSection.getUuid())) {

        savedSection.setEstado(Estado.I);

        secaoEntityRepository.save(savedSection);
      }
    }


    /*
     * ============================================================
     * 7. DEACTIVATE SECTION RESPONSIBLES NOT SENT BY CLIENT
     * ============================================================
     *
     * This handles:
     *
     *   - sections removed from the request
     *   - any other active responsible whose section was not
     *     represented as having a responsible in the request
     */
    for (var savedResponsavel : savedResponsaveis) {

      var secao = savedResponsavel.getSecaoId();
      if (secao == null)
        continue;

      if (!receivedResponsibleSectionIds.contains(secao.getUuid())) {
        savedResponsavel.setEstado(Estado.I.name());
        responsavelEntityRepository.save(savedResponsavel);
      }
    }
  }

  private String normalize(String value) {
    return value == null
        ? null
        : value.trim().toLowerCase(Locale.ROOT);
  }

  public ResponsaveisDirecaoResponseDTO getResponsavelData(Long institutoId) {

    var response = new ResponsaveisDirecaoResponseDTO();

    var savedResponsibles = responsavelEntityRepository.findAllSectionsByDirection(institutoId);

    responsavelEntityRepository.findByInstitId_IdAndSecaoIdIsNull(institutoId)
        .map(ResponsavelEntity::getFunId)
        .ifPresent(obj -> {
          response.setResponsavelDirecaoId(obj.getUuid());
          response.setResponsavelDirecaoNome(obj.getNome());
        });

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


}
