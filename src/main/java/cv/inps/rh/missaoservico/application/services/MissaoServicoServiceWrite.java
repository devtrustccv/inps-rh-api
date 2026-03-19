package cv.inps.rh.missaoservico.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.infrastructure.mappers.DocumentoMapper;
import cv.inps.rh.missaoservico.application.commands.SaveAnaliseProcessoMissaoServicoCommand;
import cv.inps.rh.missaoservico.application.commands.SaveSubmissaoServicoCommand;
import cv.inps.rh.missaoservico.application.commands.SubmeterMissaoServicoCommand;
import cv.inps.rh.missaoservico.application.dto.MissaoAnaliseRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoColaboradorRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoNotificacaoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoPrestadorDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoSubmissaoRequestDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoColaboradorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoPrestadorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.NotificacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.DocumentoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.GeografiaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoColaboradorEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoPrestadorEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoServicoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.NotificacaoEntityRepository;
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
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MissaoServicoServiceWrite {

  private static final String ESTADO_ATIVO = "A";
  private static final String ESTADO_INATIVO = "I";
  private static final Integer DESTINO_NACIONAL = 1;
  private static final Integer DESTINO_ESTRANGEIRO = 2;
  private static final String ETAPA_1 = "ETAPA_1_SUBMISSAO_AUTORIZACAO";
  private static final String ETAPA_2 = "ETAPA_2_ANALISE_RH";

  private final MissaoServicoEntityRepository missaoServicoRepository;
  private final MissaoColaboradorEntityRepository missaoColaboradorRepository;
  private final MissaoPrestadorEntityRepository missaoPrestadorRepository;
  private final GeografiaEntityRepository geografiaRepository;
  private final FuncionarioEntityRepository funcionarioRepository;
  private final DocumentoEntityRepository documentoRepository;
  private final NotificacaoEntityRepository notificacaoRepository;
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

  @Transactional
  public ResponseEntity<Map<String, ?>> salvarAnalise(SaveAnaliseProcessoMissaoServicoCommand command) {
    var uuid = parseUuid(command != null ? command.getUuid() : null, "uuid");
    var dto = command != null ? command.getMissaoanaliserequest() : null;
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    var missao = missaoServicoRepository.findByUuidOrThrow(uuid);

    validarAnalise(dto);

    var prestadoresPersistidos = syncPrestadores(missao, dto.getPrestadores());
    if (!prestadoresPersistidos.isEmpty()) {
      missaoPrestadorRepository.saveAll(prestadoresPersistidos);
    }

    persistirNotificacoesAnalise(missao, dto.getNotificacao(), dto.getPrestadores());

    missao.setEtapa(ETAPA_2);
    missaoServicoRepository.save(missao);

    Map<String, Object> resp = new HashMap<>();
    resp.put("id", missao.getUuid() != null ? missao.getUuid().toString() : null);
    return ResponseEntity.ok(resp);
  }

  @Transactional
  public ResponseEntity<Map<String, ?>> salvarSubmissao(SaveSubmissaoServicoCommand command) {
    var uuid = parseUuid(command != null ? command.getUuid() : null, "uuid");
    var dto = command != null ? command.getMissaosubmissaorequest() : null;
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    validarSubmissao(dto);

    var missao = missaoServicoRepository.findByUuidOrThrow(uuid);
    var pais = geografiaRepository.findByIdOrThrow(dto.getPaisDestinoId());

    missao.setPaisDestinoId(pais);
    missao.setFlgDestino(isCaboVerde(pais) ? DESTINO_NACIONAL : DESTINO_ESTRANGEIRO);
    missao.setDescricaoDestino(dto.getDescricaoDestino());
    missao.setDataInicio(dto.getDataInicio());
    missao.setDataFim(dto.getDataFim());
    missao.setNrDias(calcularNrDias(dto.getDataInicio(), dto.getDataFim()));
    missao.setAutorizadoPor(dto.getAutorizadoPor());
    missao.setDataAutorizacao(dto.getDataAutorizacao());
    if (StringUtils.hasText(dto.getEstado())) {
      missao.setEstado(dto.getEstado());
    }
    missao.setEtapa(ETAPA_1);

    missao = missaoServicoRepository.save(missao);

    var colaboradores = syncColaboradores(missao, dto.getColaboradores());
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

  private void validarAnalise(MissaoAnaliseRequestDTO dto) {
    if (CollectionUtils.isEmpty(dto.getPrestadores())) {
      throw IgrpResponseStatusException.badRequest("prestadores é obrigatório");
    }
  }

  private ArrayList<MissaoPrestadorEntity> syncPrestadores(MissaoServicoEntity missao, List<MissaoPrestadorDTO> dtos) {
    var existentes = missaoPrestadorRepository.findAllByMissaoServId_Uuid(missao.getUuid());
    var toSave = new ArrayList<MissaoPrestadorEntity>();

    var incomingByEntId = new HashMap<Long, MissaoPrestadorDTO>();
    for (var p : dtos) {
      if (p == null || p.getEntId() == null)
        continue;
      incomingByEntId.putIfAbsent(p.getEntId(), p);
    }

    if (incomingByEntId.isEmpty()) {
      throw IgrpResponseStatusException.badRequest("prestadores inválido");
    }

    if (!CollectionUtils.isEmpty(existentes)) {
      for (var e : existentes) {
        var dto = e != null ? incomingByEntId.remove(e.getEntId()) : null;
        if (dto != null) {
          e.setNome(dto.getNome());
          e.setEmail(dto.getEmail());
          e.setEstado(ESTADO_ATIVO);
          toSave.add(e);
        } else if (e != null) {
          e.setEstado(ESTADO_INATIVO);
          toSave.add(e);
        }
      }
    }

    for (var dto : incomingByEntId.values()) {
      if (dto == null)
        continue;
      if (!StringUtils.hasText(dto.getNome())) {
        throw IgrpResponseStatusException.badRequest("nome do prestador é obrigatório");
      }
      if (!StringUtils.hasText(dto.getEmail())) {
        throw IgrpResponseStatusException.badRequest("email do prestador é obrigatório");
      }

      var e = new MissaoPrestadorEntity();
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEntId(dto.getEntId());
      e.setNome(dto.getNome());
      e.setEmail(dto.getEmail());
      e.setMissaoServId(missao);
      e.setEstado(ESTADO_ATIVO);
      toSave.add(e);
    }

    return toSave;
  }

  private void persistirNotificacoesAnalise(
      MissaoServicoEntity missao,
      MissaoNotificacaoRequestDTO notificacao,
      List<MissaoPrestadorDTO> prestadores) {
    if (notificacao == null)
      return;
    if (!StringUtils.hasText(notificacao.getAssunto()) && !StringUtils.hasText(notificacao.getCorpoEmail()))
      return;
    if (CollectionUtils.isEmpty(prestadores))
      return;

    var toSave = new ArrayList<NotificacaoEntity>();
    for (var p : prestadores) {
      if (p == null || !StringUtils.hasText(p.getEmail()))
        continue;
      var n = new NotificacaoEntity();
      n.setUuid(UuidCreator.getTimeOrderedEpoch());
      n.setReferenciaId(missao.getId());
      n.setReferenciaName(TableName.RH_T_MISSAO_SERVICO.name());
      n.setReferenciaUuid(missao.getUuid());
      n.setAssunto(notificacao.getAssunto());
      n.setMessage(notificacao.getCorpoEmail());
      n.setEmail(p.getEmail());
      n.setNomeReceptor(p.getNome());
      toSave.add(n);
    }

    if (!toSave.isEmpty()) {
      notificacaoRepository.saveAll(toSave);
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
    if (pais == null)
      return false;
    var nome = pais.getNome();
    var nomeOficial = pais.getNomeOficial();
    return (StringUtils.hasText(nome) && "cabo verde".equalsIgnoreCase(nome.trim()))
        || (StringUtils.hasText(nomeOficial) && "cabo verde".equalsIgnoreCase(nomeOficial.trim()));
  }

  private ArrayList<MissaoColaboradorEntity> persistirColaboradores(
      java.util.List<MissaoColaboradorRequestDTO> colaboradoresDto,
      MissaoServicoEntity missao) {
    var result = new ArrayList<MissaoColaboradorEntity>();
    var seen = new HashSet<UUID>();

    for (var c : colaboradoresDto) {
      if (c == null || c.getColaboradorId() == null)
        continue;
      if (!seen.add(c.getColaboradorId()))
        continue;

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

  private ArrayList<MissaoColaboradorEntity> syncColaboradores(
      MissaoServicoEntity missao,
      java.util.List<MissaoColaboradorRequestDTO> colaboradoresDto) {
    var existentes = missaoColaboradorRepository.findAllByMissaoServId_Uuid(missao.getUuid());
    var toSave = new ArrayList<MissaoColaboradorEntity>();

    var incoming = new HashMap<UUID, MissaoColaboradorRequestDTO>();
    for (var c : colaboradoresDto) {
      if (c == null || c.getColaboradorId() == null)
        continue;
      incoming.putIfAbsent(c.getColaboradorId(), c);
    }

    if (incoming.isEmpty()) {
      throw IgrpResponseStatusException.badRequest("colaboradores inválido");
    }

    if (!CollectionUtils.isEmpty(existentes)) {
      for (var e : existentes) {
        var funUuid = e != null && e.getFunId() != null ? e.getFunId().getUuid() : null;
        var dto = funUuid != null ? incoming.remove(funUuid) : null;
        if (dto != null) {
          e.setEstado(ESTADO_ATIVO);
          var fun = funcionarioRepository.findByUuidOrThrow(funUuid);
          e.setNumDocumento(parseLong(fun.getNumDocumento()));
          toSave.add(e);
        } else if (e != null) {
          e.setEstado(ESTADO_INATIVO);
          toSave.add(e);
        }
      }
    }

    for (var funUuid : incoming.keySet()) {
      var fun = funcionarioRepository.findByUuidOrThrow(funUuid);
      var e = new MissaoColaboradorEntity();
      e.setUuid(UuidCreator.getTimeOrderedEpoch());
      e.setEstado(ESTADO_ATIVO);
      e.setFunId(fun);
      e.setMissaoServId(missao);
      e.setNumDocumento(parseLong(fun.getNumDocumento()));
      toSave.add(e);
    }

    return toSave;
  }

  private Long parseLong(String raw) {
    if (!StringUtils.hasText(raw))
      return null;
    try {
      return Long.valueOf(raw.trim());
    } catch (Exception e) {
      return null;
    }
  }

  private void persistirDocumentos(MissaoSubmissaoRequestDTO dto, MissaoServicoEntity missao) {
    if (dto.getDocumentos() == null)
      return;

    var existentes = documentoRepository.findAllByReferenciaNameAndReferenciaUuid(
        TableName.RH_T_MISSAO_SERVICO.name(),
        missao.getUuid());

    var lista = documentoMapper.syncDocumentos(
        existentes != null ? existentes : new ArrayList<>(),
        dto.getDocumentos(),
        TableName.RH_T_MISSAO_SERVICO.name(),
        missao.getId(),
        missao.getUuid(),
        1L,
        null);

    if (lista != null && !lista.isEmpty()) {
      lista.forEach(d -> {
        if (d.getUuid() == null)
          d.setUuid(UuidCreator.getTimeOrderedEpoch());
        if (d.getEstado() == null)
          d.setEstado(Estado.A);
      });
      documentoRepository.saveAll(lista);
    }
  }

  private UUID parseUuid(String raw, String field) {
    try {
      return UUID.fromString(raw);
    } catch (Exception e) {
      throw IgrpResponseStatusException.badRequest("UUID inválido para " + field + ": " + raw);
    }
  }
}
