package cv.inps.rh.missaoservico.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.missaoservico.application.commands.CreatePrestadorServicoCommand;
import cv.inps.rh.missaoservico.application.commands.UpdatePrestadorServicoCommand;
import cv.inps.rh.missaoservico.application.dto.PrestadorEmailRequestDTO;
import cv.inps.rh.missaoservico.application.dto.PrestadorServicoRequestDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamPrestadorDetEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamPrestadorEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.EntidadeEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.GeografiaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamPrestadorDetEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamPrestadorEntityRepository;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.regex.Pattern;

/** Gestão de Prestadores de Serviço (menu próprio da Missão de Serviço, spec 14/09). */
@RequiredArgsConstructor
@Service
public class PrestadorServicoServiceWrite {

  private static final String ESTADO_ATIVO = "A";
  private static final String ESTADO_INATIVO = "I";
  private static final Pattern EMAIL = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

  private final ParamPrestadorEntityRepository paramPrestadorRepository;
  private final ParamPrestadorDetEntityRepository paramPrestadorDetRepository;
  private final EntidadeEntityRepository entidadeRepository;
  private final GeografiaEntityRepository geografiaRepository;

  @Transactional
  public ResponseEntity<SuccessResponseDTO> criar(CreatePrestadorServicoCommand command) {
    var dto = command != null ? command.getPrestadorservicorequest() : null;
    validar(dto);

    // Um registo por entidade: a mesma agência duas vezes duplicaria as opções na selecção de prestadores.
    if (paramPrestadorRepository.existsByEntId(dto.getEntId())) {
      throw IgrpResponseStatusException.of(HttpStatus.CONFLICT,
          "Já existe um prestador registado para a entidade " + dto.getEntId());
    }

    var prestador = new ParamPrestadorEntity();
    prestador.setUuid(UuidCreator.getTimeOrderedEpoch());
    aplicar(prestador, dto);
    prestador.setEstado(StringUtils.hasText(dto.getEstado()) ? dto.getEstado().trim() : ESTADO_ATIVO);
    prestador = paramPrestadorRepository.save(prestador);

    if (dto.getEmails() != null) {
      syncEmails(prestador, dto.getEmails());
    }

    return ResponseEntity.ok(new SuccessResponseDTO(true, prestador.getUuid().toString(), "Prestador gravado", new ArrayList<>()));
  }

  @Transactional
  public ResponseEntity<SuccessResponseDTO> atualizar(UpdatePrestadorServicoCommand command) {
    var uuid = IdentificadorUnico.from(command != null ? command.getUuid() : null).valor();
    var dto = command.getPrestadorservicorequest();
    validar(dto);

    var prestador = paramPrestadorRepository.findByUuidOrThrow(uuid);
    if (paramPrestadorRepository.existsByEntIdAndUuidNot(dto.getEntId(), uuid)) {
      throw IgrpResponseStatusException.of(HttpStatus.CONFLICT,
          "Já existe outro prestador registado para a entidade " + dto.getEntId());
    }

    aplicar(prestador, dto);
    if (StringUtils.hasText(dto.getEstado())) {
      prestador.setEstado(dto.getEstado().trim());
    }
    paramPrestadorRepository.save(prestador);

    if (dto.getEmails() != null) {
      syncEmails(prestador, dto.getEmails());
    }

    return ResponseEntity.ok(new SuccessResponseDTO(true, prestador.getUuid().toString(), "Prestador gravado", new ArrayList<>()));
  }

  private void validar(PrestadorServicoRequestDTO dto) {
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }
    if (dto.getEntId() == null) {
      throw IgrpResponseStatusException.badRequest("entId é obrigatório");
    }
    if (!StringUtils.hasText(dto.getEmail())) {
      throw IgrpResponseStatusException.badRequest("email é obrigatório");
    }
    validarEmail(dto.getEmail());
    if (StringUtils.hasText(dto.getEstado())
        && !ESTADO_ATIVO.equals(dto.getEstado().trim()) && !ESTADO_INATIVO.equals(dto.getEstado().trim())) {
      throw IgrpResponseStatusException.badRequest("estado inválido: " + dto.getEstado() + " (A ou I)");
    }

    if (dto.getEmails() != null) {
      var vistos = new HashSet<String>();
      vistos.add(dto.getEmail().trim().toLowerCase());
      for (var e : dto.getEmails()) {
        if (e == null || !StringUtils.hasText(e.getEmail())) {
          throw IgrpResponseStatusException.badRequest("emails: email é obrigatório");
        }
        validarEmail(e.getEmail());
        if (!vistos.add(e.getEmail().trim().toLowerCase())) {
          throw IgrpResponseStatusException.badRequest("Email duplicado: " + e.getEmail().trim());
        }
      }
    }
  }

  private void validarEmail(String email) {
    if (!EMAIL.matcher(email.trim()).matches()) {
      throw IgrpResponseStatusException.badRequest("Email inválido: " + email.trim());
    }
  }

  private void aplicar(ParamPrestadorEntity prestador, PrestadorServicoRequestDTO dto) {
    var entidade = entidadeRepository.findById(dto.getEntId())
        .orElseThrow(() -> IgrpResponseStatusException.badRequest("Entidade inválida: " + dto.getEntId()));

    prestador.setEntId(entidade.getId());
    prestador.setNome(StringUtils.hasText(dto.getNome()) ? dto.getNome().trim() : entidade.getNome());
    prestador.setNif(trimToNull(dto.getNif()));
    prestador.setEmail(dto.getEmail().trim());
    prestador.setTelefone(trimToNull(dto.getTelefone()));
    prestador.setMorada(trimToNull(dto.getMorada()));
    prestador.setIlhaId(dto.getIlhaId() != null ? geografiaRepository.findByIdOrThrow(dto.getIlhaId()) : null);
  }

  /**
   * Sincroniza os emails adicionais com a lista recebida: com id actualiza, sem id cria (ou reactiva
   * um email igual já existente), e o que ficou de fora é inactivado — o histórico mantém-se.
   */
  private void syncEmails(ParamPrestadorEntity prestador, List<PrestadorEmailRequestDTO> emails) {
    var existentes = paramPrestadorDetRepository.findAllByParamPrestId_IdOrderByIdAsc(prestador.getId());
    var existentesPorId = new HashMap<Long, ParamPrestadorDetEntity>();
    existentes.forEach(d -> existentesPorId.put(d.getId(), d));

    var mantidos = new HashSet<Long>();
    var toSave = new ArrayList<ParamPrestadorDetEntity>();

    for (var e : emails) {
      var email = e.getEmail().trim();
      ParamPrestadorDetEntity det;
      if (e.getId() != null) {
        det = existentesPorId.get(e.getId());
        if (det == null) {
          throw IgrpResponseStatusException.badRequest("Email " + e.getId() + " não pertence ao prestador");
        }
      } else {
        det = existentes.stream()
            .filter(d -> !mantidos.contains(d.getId()) && email.equalsIgnoreCase(d.getEmail()))
            .findFirst()
            .orElse(null);
        if (det == null) {
          det = new ParamPrestadorDetEntity();
          det.setUuid(UuidCreator.getTimeOrderedEpoch());
          det.setParamPrestId(prestador);
        }
      }
      det.setEmail(email);
      det.setEstado(ESTADO_ATIVO);
      if (det.getId() != null) {
        mantidos.add(det.getId());
      }
      toSave.add(det);
    }

    for (var d : existentes) {
      if (!mantidos.contains(d.getId()) && ESTADO_ATIVO.equals(d.getEstado())) {
        d.setEstado(ESTADO_INATIVO);
        toSave.add(d);
      }
    }

    if (!toSave.isEmpty()) {
      paramPrestadorDetRepository.saveAll(toSave);
    }
  }

  private String trimToNull(String s) {
    return StringUtils.hasText(s) ? s.trim() : null;
  }
}
