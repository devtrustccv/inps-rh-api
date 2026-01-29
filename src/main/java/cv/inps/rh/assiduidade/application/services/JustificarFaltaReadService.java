package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.JustificarFaltaDTO;
import cv.inps.rh.assiduidade.application.dto.FaltaItemDTO;
import cv.inps.rh.assiduidade.application.queries.GetJustificacaoFaltaQuery;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AssiduidadeSinteseDiarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FaltaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AssiduidadeSinteseDiarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FaltaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.util.DateFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JustificarFaltaReadService {

  private final FaltaEntityRepository faltaRepository;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final DocumentoEntityRepository documentoEntityRepository;

  private final AssiduidadeSinteseDiarioEntityRepository assiduidadeSinteseDiarioEntityRepository;

  @Transactional(readOnly = true)
  public JustificarFaltaDTO getFaltaJustificada(GetJustificacaoFaltaQuery query) {

    UUID funcUuid;
    try {
      funcUuid = UUID.fromString(query.getFuncionarioId());
    } catch (IllegalArgumentException e) {
      throw IgrpResponseStatusException.badRequest("Funcionario UUID inválido");
    }

    FuncionarioEntity funcionario = funcionarioRepository.findByUuid(funcUuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(
            HttpStatus.NOT_FOUND,
            "Funcionário não encontrado para UUID: " + funcUuid
        ));

    // --- calcular intervalo do mês ---
    LocalDate inicioMes = LocalDate.of(query.getAno(), query.getMes(), 1);
    LocalDate fimMes = inicioMes.withDayOfMonth(inicioMes.lengthOfMonth());

    // --- buscar todas as faltas do funcionario no mês ---
    List<FaltaEntity> faltas = faltaRepository.findAllByFuncionarioAndPeriodo(funcUuid, inicioMes, fimMes);

    // --- mapear para FaltaItemDTO ---
    List<FaltaItemDTO> itensFalta = faltas.stream().map(f -> {
      FaltaItemDTO item = new FaltaItemDTO();
      item.setId(f.getId());

      item.setHorasAusencia(f.getHorasAusencia());
      item.setValorAusencia(null); // ou outro campo de valor
      item.setMotivo(f.getDescricaoMotivo());
      item.setComJustificativo(f.getFlgJustificativo());
      // documento pode ser mapeado se houver relacionamento
      return item;
    }).collect(Collectors.toList());

    // --- montar DTO principal ---
    JustificarFaltaDTO dto = new JustificarFaltaDTO();
    dto.setColaboradorId(funcionario.getUuid());
    dto.setNomeColaborador(funcionario.getNome());
    dto.setItensFalta(itensFalta);
    dto.setAno(query.getAno());
    dto.setMes(query.getMes());

    // campos de decisão, despacho e tipoJustificacao podem ser preenchidos se houver somente um registro
    if (!faltas.isEmpty()) {
      FaltaEntity primeira = faltas.get(0);
      dto.setParecerResponsavel(primeira.getDecisaoResponsavel());
      dto.setResponsavelId(primeira.getResponsavelId() != null ? primeira.getResponsavelId().getId() : null);
      dto.setObsResponsavel(primeira.getObsResponsavel());
      dto.setDespachoRh(primeira.getDespachoRh());
      dto.setTipoJustificacao(primeira.getParamSitId() != null ? primeira.getParamSitId().getId() : null);
    }

    return dto;
  }

}
