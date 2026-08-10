package cv.inps.rh.configuracao.application.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.configuracao.application.services.engine.ConfigurationProcess;
import cv.inps.rh.configuracao.application.services.model.WrapperListDTO;
import cv.inps.rh.configuracao.application.utils.ConfigurationUtils;
import cv.inps.rh.parametrizacao.application.dto.EstabelecimentoComboDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.dto.EstabelecimentoGroupedDTO;
import cv.inps.rh.shared.application.dto.EstabelecimentoResponseDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.EstabelecimentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.EstabelecimentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.GeografiaEntityRepository;
import cv.inps.rh.shared.util.PageMapper;
import jakarta.validation.Validator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Transactional
@Service("estabelecimento_type")
public class EstabelecimentoService extends ConfigurationProcess<EstabelecimentoGroupedDTO> {

  private final EstabelecimentoEntityRepository estabelecimentoRepository;
  private final GeografiaEntityRepository geografiaRepository;

  public EstabelecimentoService(
      EstabelecimentoEntityRepository estabelecimentoRepository,
      GeografiaEntityRepository geografiaRepository,
      Validator validator,
      ObjectMapper jsonMapper) {

    super(validator, jsonMapper, EstabelecimentoGroupedDTO.class);
    this.estabelecimentoRepository = estabelecimentoRepository;
    this.geografiaRepository = geografiaRepository;
  }

  @Override
  public Object create(EstabelecimentoGroupedDTO dto) {

    var pais = geografiaRepository.findByIdOrThrow(dto.getPaisId());

    var estabelecimentos = dto.getEstabelecimentos() == null
        ? List.<EstabelecimentoGroupedDTO.EstabelecimentoData>of()
        : dto.getEstabelecimentos();

    var idsRecebidos = estabelecimentos.stream()
        .map(EstabelecimentoGroupedDTO.EstabelecimentoData::id)
        .filter(StringUtils::hasText)
        .collect(Collectors.toSet());

    var existentes = estabelecimentoRepository.findEntityByPaisId(List.of(dto.getPaisId()));
    existentes.stream()
        .filter(e -> !idsRecebidos.contains(e.getUuid()))
        .forEach(e -> {
          e.setEstado(Estado.I.getCode());
          estabelecimentoRepository.save(e);
        });

    for (var data : estabelecimentos) {

      EstabelecimentoEntity estabelecimento;

      if (StringUtils.hasText(data.id())) {
        estabelecimento = estabelecimentoRepository.findByUuidOrThrow(data.id());
      } else {
        estabelecimento = new EstabelecimentoEntity();
        estabelecimento.setUuid(UuidCreator.getTimeOrderedEpoch().toString());
        estabelecimento.setPais(pais);
        estabelecimento.setEstado(Estado.A.getCode());
      }

      estabelecimento.setNome(data.nome());
      estabelecimentoRepository.save(estabelecimento);
    }

    return list(Map.of("filter", dto.getPaisId().toString()));
  }

  @Override
  public Object update(String uuid, EstabelecimentoGroupedDTO dto) {

    return null;
  }

  @Override
  public Object read(String uuid) {

    var estabelecimento = estabelecimentoRepository.findByUuidOrThrow(uuid);

    return buildResponse(estabelecimento);
  }

  @Override
  public Object list(Map<String, String> filters) {

    var pageable = ConfigurationUtils.buildDefaultPageRequest(filters);

    var nome = filters.get("nome");

    Long paisId = StringUtils.hasText(filters.get("paisId"))
        ? Long.valueOf(filters.get("paisId"))
        : null;

    var paisPage = geografiaRepository.findCountries(
        paisId,
        StringUtils.hasText(nome) ? ConfigurationUtils.normalizeAndSetToLowerCaseText(nome) : null,
        pageable
    );

    var response = new WrapperListDTO();
    PageMapper.fillPagination(paisPage, response);

    var paisIds = paisPage.getContent()
        .stream()
        .map(GeografiaEntity::getId)
        .toList();

    var estabelecimentos = estabelecimentoRepository.findByPaisId(paisIds)
        .stream()
        .collect(Collectors.groupingBy(
            EstabelecimentoComboDTO::getPaisId
        ));

    List<Object> content = paisPage.getContent()
        .stream()
        .map(row -> {

          var data = estabelecimentos
              .getOrDefault(row.getId(), List.of())
              .stream()
              .map(estabelecimento ->
                  new EstabelecimentoGroupedDTO.EstabelecimentoData(
                      estabelecimento.getValueUuid(),
                      estabelecimento.getLabel()
                  )
              )
              .toList();

          return new EstabelecimentoGroupedDTO(
              row.getId(),
              row.getNome(),
              data
          );
        })
        .collect(Collectors.toUnmodifiableList());

    response.setContent(content);

    return response;
  }

  private EstabelecimentoResponseDTO buildResponse(EstabelecimentoEntity estabelecimento) {

    var response = new EstabelecimentoResponseDTO();
    response.setUuid(estabelecimento.getUuid());
    response.setNome(estabelecimento.getNome());

    var pais = estabelecimento.getPais();
    if (pais != null) {
      response.setPaisId(pais.getId());
      response.setPais(pais.getNome());
    }

    return response;
  }

  @Override
  public void delete(String uuid) {


  }
}
