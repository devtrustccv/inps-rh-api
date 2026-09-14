package cv.inps.rh.missaoservico.application.services;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.missaoservico.application.commands.SaveProcessoPrestadoresCommand;
import cv.inps.rh.missaoservico.application.constants.EtapaProcesso;
import cv.inps.rh.missaoservico.application.constants.TipoProcesso;
import cv.inps.rh.missaoservico.application.dto.MissaoNotificacaoRequestDTO;
import cv.inps.rh.shared.application.constants.custom.TableName;
import cv.inps.rh.shared.application.services.EmailService;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;
import cv.inps.rh.shared.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

import static cv.inps.rh.missaoservico.application.services.MissaoProcessoSupport.ESTADO_ATIVO;
import static cv.inps.rh.missaoservico.application.services.MissaoProcessoSupport.ESTADO_INATIVO;

/** Escrita das etapas dos processos de missão (modelo por processo, spec 14/09). */
@RequiredArgsConstructor
@Service
public class MissaoProcessoServiceWrite {

  private static final Logger LOGGER = LoggerFactory.getLogger(MissaoProcessoServiceWrite.class);

  private static final int MAX_PRESTADORES = 3;
  private static final String TIPO_NOTIF_PEDIDO_PROPOSTA = "MISSAO_PRESTADOR";

  private final MissaoProcessoSupport support;
  private final ProcessoEtapaGuard guard;
  private final MissaoProcessoEntityRepository missaoProcessoRepository;
  private final MissaoPrestadorEntityRepository missaoPrestadorRepository;
  private final MissaoRequisicaoEntityRepository missaoRequisicaoRepository;
  private final ParamPrestadorEntityRepository paramPrestadorRepository;
  private final ParamPrestadorDetEntityRepository paramPrestadorDetRepository;
  private final NotificacaoEntityRepository notificacaoRepository;
  private final EmailService emailService;

  // ---------------------------------------------------------------------------------------------
  // Etapa Prestadores Serviço
  // ---------------------------------------------------------------------------------------------

  /**
   * Selecção de 1 a 3 prestadores parametrizados para o processo. A lista enviada é a selecção
   * completa: quem sai é inactivado — excepto se já tiver requisição emitida.
   *
   * <p>NEXT envia o pedido de proposta a todos os emails activos de cada prestador e avança para
   * Emissão de Requisição. Num NEXT com o processo já adiante, só os prestadores acrescentados
   * nesta gravação são notificados — os restantes já receberam o pedido.
   */
  @Transactional
  public ResponseEntity<Map<String, ?>> salvarPrestadores(SaveProcessoPrestadoresCommand command) {
    var missaoUuid = IdentificadorUnico.from(command != null ? command.getUuid() : null).valor();
    var dto = command.getProcessoprestadoresrequest();
    if (dto == null) {
      throw IgrpResponseStatusException.badRequest("Payload inválido");
    }

    var processo = support.processo(missaoUuid, command.getTipoProcesso(), true);
    var missao = processo.getMissaoServId();
    var avancar = support.isNext(dto.getProcessoEtapaAction());
    guard.exigirEtapa(processo, EtapaProcesso.PRESTADOR_SERVICO, avancar);

    var selecionados = resolverPrestadoresParam(dto.getPrestadores());

    var existentes = missaoPrestadorRepository.findAllByMissaoProcessoId_IdOrderByIdAsc(processo.getId());
    var porParam = new HashMap<Long, MissaoPrestadorEntity>();
    for (var e : existentes) {
      if (e.getParamPrestId() != null) {
        porParam.putIfAbsent(e.getParamPrestId().getId(), e);
      }
    }

    var toSave = new LinkedHashSet<MissaoPrestadorEntity>();
    for (var e : existentes) {
      var paramId = e.getParamPrestId() != null ? e.getParamPrestId().getId() : null;
      var mantido = paramId != null && selecionados.containsKey(paramId) && porParam.get(paramId) == e;
      if (mantido || !ESTADO_ATIVO.equals(e.getEstado()))
        continue;
      if (missaoRequisicaoRepository.existsByMissaoPrestId_IdAndEstado(e.getId(), ESTADO_ATIVO)) {
        throw IgrpResponseStatusException.badRequest(
            "O prestador " + e.getNome() + " já tem requisição emitida neste processo e não pode ser retirado");
      }
      e.setEstado(ESTADO_INATIVO);
      toSave.add(e);
    }

    var novosOuReactivados = new ArrayList<MissaoPrestadorEntity>();
    var ativos = new ArrayList<MissaoPrestadorEntity>();
    for (var param : selecionados.values()) {
      var e = porParam.get(param.getId());
      if (e == null) {
        e = new MissaoPrestadorEntity();
        e.setUuid(UuidCreator.getTimeOrderedEpoch());
        e.setMissaoServId(missao);
        e.setMissaoProcessoId(processo);
        e.setParamPrestId(param);
        e.setEstado(ESTADO_ATIVO);
        novosOuReactivados.add(e);
      } else if (!ESTADO_ATIVO.equals(e.getEstado())) {
        e.setEstado(ESTADO_ATIVO);
        novosOuReactivados.add(e);
      }
      // Fotografia do prestador no momento da selecção (colunas do modelo antigo, ainda NOT NULL)
      e.setEntId(param.getEntId());
      e.setNome(param.getNome());
      e.setEmail(param.getEmail());
      toSave.add(e);
      ativos.add(e);
    }

    missaoPrestadorRepository.saveAll(toSave);

    if (avancar) {
      var etapaAntes = processo.getEtapa();
      guard.avancarApos(processo, EtapaProcesso.PRESTADOR_SERVICO);
      missaoProcessoRepository.save(processo);

      var aNotificar = EtapaProcesso.PRESTADOR_SERVICO.name().equals(etapaAntes) ? ativos : novosOuReactivados;
      notificarPedidoProposta(missao, processo, aNotificar, dto.getNotificacao());
    }

    return ResponseEntity.ok(Map.of(
        "id", processo.getUuid().toString(),
        "etapa", processo.getEtapa()));
  }

  private LinkedHashMap<Long, ParamPrestadorEntity> resolverPrestadoresParam(List<UUID> uuids) {
    var distintos = uuids == null ? List.<UUID>of() : uuids.stream().filter(Objects::nonNull).distinct().toList();
    if (distintos.isEmpty()) {
      throw IgrpResponseStatusException.badRequest("Selecione pelo menos um prestador");
    }
    if (distintos.size() > MAX_PRESTADORES) {
      throw IgrpResponseStatusException.badRequest(
          "Máximo de " + MAX_PRESTADORES + " prestadores por processo");
    }

    var out = new LinkedHashMap<Long, ParamPrestadorEntity>();
    for (var uuid : distintos) {
      var param = paramPrestadorRepository.findByUuid(uuid)
          .orElseThrow(() -> IgrpResponseStatusException.badRequest("Prestador inválido: " + uuid));
      if (!ESTADO_ATIVO.equals(param.getEstado())) {
        throw IgrpResponseStatusException.badRequest("Prestador inactivo: " + param.getNome());
      }
      out.put(param.getId(), param);
    }
    return out;
  }

  /**
   * Um email — e uma linha em RH_T_NOTIFICACAO — por cada endereço activo de cada prestador
   * (principal + RH_T_PARAM_PRESTADOR_DET). Uma falha de envio não interrompe os restantes.
   */
  private void notificarPedidoProposta(MissaoServicoEntity missao, MissaoProcessoEntity processo,
                                       List<MissaoPrestadorEntity> prestadores, MissaoNotificacaoRequestDTO editado) {
    if (prestadores.isEmpty())
      return;

    var tipo = TipoProcesso.fromCodeOrThrow(processo.getTipoProcesso());
    var conteudo = support.conteudoPedidoProposta(support.varsMissao(missao, tipo), editado);

    var paramIds = prestadores.stream().map(p -> p.getParamPrestId().getId()).toList();
    var extrasPorParam = new HashMap<Long, List<String>>();
    for (var det : paramPrestadorDetRepository.findAllByParamPrestId_IdInAndEstado(paramIds, ESTADO_ATIVO)) {
      extrasPorParam.computeIfAbsent(det.getParamPrestId().getId(), _ -> new ArrayList<>()).add(det.getEmail());
    }

    for (var prest : prestadores) {
      var param = prest.getParamPrestId();
      var destinos = new LinkedHashSet<String>();
      destinos.add(param.getEmail());
      destinos.addAll(extrasPorParam.getOrDefault(param.getId(), List.of()));

      for (var email : destinos) {
        var estado = "Enviado";
        try {
          emailService.sendEmail(email, conteudo.assunto(), conteudo.corpo());
        } catch (Exception ex) {
          LOGGER.warn("Erro ao enviar pedido de proposta para {}: {}", email, ex.getMessage());
          estado = "Erro";
        }

        var n = new NotificacaoEntity();
        n.setUuid(UuidCreator.getTimeOrderedEpoch());
        n.setTipoNotificacao(TIPO_NOTIF_PEDIDO_PROPOSTA);
        n.setReferenciaId(prest.getId());
        n.setReferenciaName(TableName.RH_T_MISSAO_PRESTADOR.name());
        n.setReferenciaUuid(prest.getUuid());
        n.setAssunto(conteudo.assunto());
        n.setMessage(conteudo.corpo());
        n.setEmail(email);
        n.setNomeReceptor(param.getNome());
        n.setDataEnvio(LocalDate.now());
        n.setEstado(estado);
        notificacaoRepository.save(n);
      }
    }
  }
}
