package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import cv.inps.rh.missaoservico.application.constants.EtapaProcesso;
import cv.inps.rh.missaoservico.application.constants.TipoProcesso;
import cv.inps.rh.missaoservico.application.dto.MissaoNotificacaoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoProcessoResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoProcessoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoColaboradorEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoProcessoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamNotificacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/** Apoio comum aos ecrãs dos processos de missão: resolução do processo e conteúdo das notificações. */
@Component
@RequiredArgsConstructor
public class MissaoProcessoSupport {

  public static final String ESTADO_ATIVO = "A";
  public static final String ESTADO_INATIVO = "I";

  private static final String ACTION_NEXT = "NEXT";

  private final MissaoProcessoEntityRepository missaoProcessoRepository;
  private final MissaoColaboradorEntityRepository missaoColaboradorRepository;
  private final ParamNotificacaoEntityRepository paramNotificacaoRepository;

  public record Conteudo(String assunto, String corpo) {}

  /**
   * Processo {@code tipoProcesso} da missão. Nas escritas recusa missões canceladas — o processo
   * pode ainda estar activo, mas a missão já não admite alterações.
   */
  public MissaoProcessoEntity processo(UUID missaoUuid, String tipoProcesso, boolean escrita) {
    var tipo = TipoProcesso.fromCodeOrThrow(tipoProcesso != null ? tipoProcesso.trim().toUpperCase() : null);
    var processo = missaoProcessoRepository.findByMissaoAndTipoOrThrow(missaoUuid, tipo.name());
    if (escrita && ESTADO_INATIVO.equals(processo.getMissaoServId().getEstado())) {
      throw IgrpResponseStatusException.badRequest("A missão está cancelada e não admite alterações");
    }
    return processo;
  }

  public boolean isNext(ProcessStepAction action) {
    return action != null && ACTION_NEXT.equals(action.getCode());
  }

  public String nrMissaoFormatado(MissaoServicoEntity missao) {
    if (missao == null || missao.getNrMissao() == null)
      return null;
    return missao.getAno() != null ? missao.getNrMissao() + "/" + missao.getAno() : String.valueOf(missao.getNrMissao());
  }

  public MissaoProcessoResponseDTO toProcessoDto(MissaoProcessoEntity p) {
    var dto = new MissaoProcessoResponseDTO();
    dto.setId(p.getId());
    dto.setUuid(p.getUuid());
    dto.setTipoProcesso(p.getTipoProcesso());
    dto.setTipoProcessoDesc(TipoProcesso.fromCodeOrThrow(p.getTipoProcesso()).getDescricao());
    dto.setEtapa(p.getEtapa());
    var etapa = EtapaProcesso.fromCode(p.getEtapa());
    dto.setEtapaDesc(etapa != null ? etapa.getDescricao() : p.getEtapa());
    dto.setEstado(p.getEstado());
    return dto;
  }

  /** Variáveis disponíveis nos templates das notificações da missão: {nrMissao}, {destino}, … */
  public Map<String, String> varsMissao(MissaoServicoEntity missao, TipoProcesso tipo) {
    var nrColaboradores = missaoColaboradorRepository.findAllByMissaoServId_Uuid(missao.getUuid()).stream()
        .filter(c -> ESTADO_ATIVO.equals(c.getEstado()))
        .count();
    var vars = new LinkedHashMap<String, String>();
    vars.put("nrMissao", nrMissaoFormatado(missao));
    vars.put("destino", missao.getDescricaoDestino() != null ? missao.getDescricaoDestino() : "");
    vars.put("dataInicio", missao.getDataInicio() != null ? missao.getDataInicio().toString() : "");
    vars.put("dataFim", missao.getDataFim() != null ? missao.getDataFim().toString() : "");
    vars.put("nrDias", missao.getNrDias() != null ? String.valueOf(missao.getNrDias()) : "");
    vars.put("nrColaboradores", String.valueOf(nrColaboradores));
    vars.put("tipoProcesso", tipo.getDescricao());
    return vars;
  }

  /**
   * Conteúdo do pedido de proposta aos prestadores (template MISSAO_PRESTADOR). O que o utilizador
   * editou prevalece; sem edição usa-se o template activo; sem template, um texto por defeito.
   */
  public Conteudo conteudoPedidoProposta(Map<String, String> vars, MissaoNotificacaoRequestDTO editado) {
    var template = paramNotificacaoRepository.findFirstByTipoNotificacaoAndEstadoOrderByIdDesc("MISSAO_PRESTADOR", ESTADO_ATIVO);

    var assunto = editado != null && StringUtils.hasText(editado.getAssunto())
        ? editado.getAssunto()
        : template.map(t -> substituir(t.getAssunto(), vars))
            .filter(StringUtils::hasText)
            .orElse("Pedido de Proposta - " + vars.get("tipoProcesso") + " - Missão Nº " + vars.get("nrMissao"));

    var corpo = editado != null && StringUtils.hasText(editado.getCorpoEmail())
        ? editado.getCorpoEmail()
        : template.map(t -> substituir(t.getCorpo(), vars))
            .filter(StringUtils::hasText)
            .orElse(corpoPedidoPropostaPorDefeito(vars));

    return new Conteudo(assunto, corpo);
  }

  private String corpoPedidoPropostaPorDefeito(Map<String, String> vars) {
    return "Exmo(a) Sr(a),\n\n"
        + "Solicita-se o envio de proposta (fatura proforma) de " + vars.get("tipoProcesso")
        + " para a missão de serviço com os seguintes dados:\n"
        + "- Nº Missão: " + vars.get("nrMissao") + "\n"
        + "- Destino: " + vars.get("destino") + "\n"
        + "- Data de partida: " + vars.get("dataInicio") + "\n"
        + "- Data de regresso: " + vars.get("dataFim") + "\n"
        + "- Nº de colaboradores: " + vars.get("nrColaboradores") + "\n\n"
        + "Aguardamos a vossa proposta.\n\nCom os melhores cumprimentos,\nINPS - Recursos Humanos";
  }

  private String substituir(String template, Map<String, String> vars) {
    if (template == null)
      return null;
    for (var entry : vars.entrySet()) {
      template = template.replace("{" + entry.getKey() + "}", entry.getValue() != null ? entry.getValue() : "");
    }
    return template;
  }
}
