package cv.inps.rh.missaoservico.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.commands.SubmeterMissaoServicoCommand;
import cv.inps.rh.missaoservico.application.dto.MissaoColaboradorRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoSubmissaoRequestDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoColaboradorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.GeografiaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoColaboradorEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoServicoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MissaoServicoService {

  private static final String ESTADO_ATIVO = "A";
  private static final Integer DESTINO_NACIONAL = 1;
  private static final Integer DESTINO_ESTRANGEIRO = 2;
  private static final String ETAPA_1 = "ETAPA_1_SUBMISSAO_AUTORIZACAO";

  private final MissaoServicoEntityRepository missaoServicoRepository;
  private final MissaoColaboradorEntityRepository missaoColaboradorRepository;
  private final GeografiaEntityRepository geografiaRepository;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final DocumentoEntityRepository documentoRepository;
  private final DocumentoMapper documentoMapper;

  @Transactional
  public ResponseEntity<Map<String, ?>> submeter(SubmeterMissaoServicoCommand command) {
    var dto = command != null ? command.getMissaosubmissaorequest() : null;
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    validarSubmissao(dto);

    var pais = geografiaRepository.findByIdOrThrow(dto.getPaisDestinoId());

    var missao = new MissaoServicoEntity();
    missao.setUuid(UuidCreator.getTimeOrderedEpoch());
    missao.setNrMissao(nextNrMissao());
    missao.setPaisDestinoId(pais);
    missao.setFlgDestino(isCaboVerde(pais) ? DESTINO_NACIONAL : DESTINO_ESTRANGEIRO);
    missao.setDescricaoDestino(dto.getDescricaoDestino());
    missao.setDataInicio(dto.getDataInicio());
    missao.setDataFim(dto.getDataFim());
    missao.setNrDias(calcularNrDias(dto.getDataInicio(), dto.getDataFim()));
    missao.setAutorizadoPor(dto.getAutorizadoPor());
    missao.setDataAutorizacao(dto.getDataAutorizacao());
    missao.setEtapa(ETAPA_1);
    missao.setEstado(StringUtils.hasText(dto.getEstado()) ? dto.getEstado() : ESTADO_ATIVO);

    missao = missaoServicoRepository.save(missao);

    var colaboradores = persistirColaboradores(dto.getColaboradores(), missao);
    if (!colaboradores.isEmpty()) {
      missaoColaboradorRepository.saveAll(colaboradores);
    }

    persistirDocumentos(dto, missao);

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", missao.getUuid() != null ? missao.getUuid().toString() : null);
    resp.put("nrMissao", missao.getNrMissao());
    return ResponseEntity.ok(resp);
  }

  private void validarSubmissao(MissaoSubmissaoRequestDTO dto) {
    if (dto.getPaisDestinoId() == null) {
      throw IgrpResponseStatusException.badRequest("paisDestinoId é obrigatório");
    }
    if (!StringUtils.hasText(dto.getDescricaoDestino())) {
      throw IgrpResponseStatusException.badRequest("descricaoDestino é obrigatório");
    }
    if (dto.getDataInicio() == null) {
      throw IgrpResponseStatusException.badRequest("dataInicio é obrigatório");
    }
    if (dto.getDataFim() == null) {
      throw IgrpResponseStatusException.badRequest("dataFim é obrigatório");
    }
    if (dto.getDataFim().isBefore(dto.getDataInicio())) {
      throw IgrpResponseStatusException.badRequest("dataFim não pode ser anterior a dataInicio");
    }
    if (!StringUtils.hasText(dto.getAutorizadoPor())) {
      throw IgrpResponseStatusException.badRequest("autorizadoPor é obrigatório");
    }
    if (dto.getDataAutorizacao() == null) {
      throw IgrpResponseStatusException.badRequest("dataAutorizacao é obrigatório");
    }
    if (CollectionUtils.isEmpty(dto.getColaboradores())) {
      throw IgrpResponseStatusException.badRequest("colaboradores é obrigatório");
    }
  }

  private Long nextNrMissao() {
    var max = missaoServicoRepository.findMaxNrMissao();
    return (max != null ? max : 0L) + 1L;
  }

  private int calcularNrDias(java.time.LocalDate inicio, java.time.LocalDate fim) {
    long diff = ChronoUnit.DAYS.between(inicio, fim);
    return (int) diff + 1;
  }

  private boolean isCaboVerde(cv.inps.rh.shared.infrastructure.persistence.entity.GeografiaEntity pais) {
    if (pais == null) return false;
    var nome = pais.getNome();
    var nomeOficial = pais.getNomeOficial();
    return (StringUtils.hasText(nome) && "cabo verde".equalsIgnoreCase(nome.trim()))
        || (StringUtils.hasText(nomeOficial) && "cabo verde".equalsIgnoreCase(nomeOficial.trim()));
  }

  private ArrayList<MissaoColaboradorEntity> persistirColaboradores(
      java.util.List<MissaoColaboradorRequestDTO> colaboradoresDto,
      MissaoServicoEntity missao
  ) {
    var result = new ArrayList<MissaoColaboradorEntity>();
    var seen = new HashSet<UUID>();

    for (var c : colaboradoresDto) {
      if (c == null || c.getColaboradorId() == null) continue;
      if (!seen.add(c.getColaboradorId())) continue;

      var fun = funcionarioRepository.findByUuidOrThrow(c.getColaboradorId());

      var e = new MissaoColaboradorEntity();
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      e.setFunId(fun);
      e.setMissaoServId(missao);
      e.setNumDocumento(parseLong(fun.getNumDocumento()));
      result.add(e);
    }

    if (result.isEmpty()) {
      throw IgrpResponseStatusException.badRequest("colaboradores inválido");
    }

    return result;
  }

  private Long parseLong(String raw) {
    if (!StringUtils.hasText(raw)) return null;
    try {
      return Long.valueOf(raw.trim());
    } catch (Exception e) {
      return null;
    }
  }

  private void persistirDocumentos(MissaoSubmissaoRequestDTO dto, MissaoServicoEntity missao) {
    if (dto.getDocumentos() == null) return;

    var existentes = documentoRepository.findAllByReferenciaNameAndReferenciaUuid(
        TableName.RH_T_MISSAO_SERVICO.name(),
        missao.getUuid()
    );

    var lista = documentoMapper.syncDocumentos(
        existentes != null ? existentes : new ArrayList<>(),
        dto.getDocumentos(),
        TableName.RH_T_MISSAO_SERVICO.name(),
        missao.getId(),
        missao.getUuid(),
        1L,
        null
    );

    if (lista != null && !lista.isEmpty()) {
      lista.forEach(d -> {
        if (d.getUuid() == null) d.setUuid(UuidCreator.getTimeOrderedEpoch());
        if (d.getEstado() == null) d.setEstado(Estado.A);
      });
      documentoRepository.saveAll(lista);
    }
  }
}
