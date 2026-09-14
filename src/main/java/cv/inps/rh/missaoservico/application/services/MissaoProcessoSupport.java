package cv.inps.rh.missaoservico.application.services;

import cv.inps.rh.emprestimo.application.constants.ProcessStepAction;
import cv.inps.rh.missaoservico.application.constants.EtapaProcesso;
import cv.inps.rh.missaoservico.application.constants.TipoProcesso;
import cv.inps.rh.missaoservico.application.dto.MissaoColaboradorResponseDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoNotificacaoRequestDTO;
import cv.inps.rh.missaoservico.application.dto.MissaoProcessoResponseDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoColaboradorEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoProcessoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TipoDocumentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoColaboradorEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.MissaoProcessoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamNotificacaoEntityRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Apoio comum aos ecrãs dos processos de missão: resolução do processo e conteúdo das notificações. */
@Component
@RequiredArgsConstructor
public class MissaoProcessoSupport {

  public static final String ESTADO_ATIVO = "A";
  public static final String ESTADO_INATIVO = "I";

  /** REFERENCIA_NAME do PDF gerado da requisição — distinto da proposta anexada (RH_T_MISSAO_REQUISICAO). */
  public static final String REF_DOC_REQUISICAO_PDF = "RH_T_MISSAO_REQUISICAO_PDF";

  private static final String ACTION_NEXT = "NEXT";

  private final MissaoProcessoEntityRepository missaoProcessoRepository;
  private final MissaoColaboradorEntityRepository missaoColaboradorRepository;
  private final ParamNotificacaoEntityRepository paramNotificacaoRepository;
  private final EntityManager entityManager;

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

  public MissaoColaboradorResponseDTO toColaboradorDto(MissaoColaboradorEntity c) {
    var dto = new MissaoColaboradorResponseDTO();
    dto.setId(c.getId());
    dto.setUuid(c.getUuid());
    dto.setEstado(c.getEstado());
    var fun = c.getFunId();
    dto.setNumDocumento(StringUtils.hasText(c.getNumDocumento()) ? c.getNumDocumento() : fun != null ? fun.getNumDocumento() : null);
    dto.setFunId(fun != null ? fun.getId() : null);
    dto.setFunUuid(fun != null ? fun.getUuid() : null);
    dto.setNomeColaborador(fun != null ? fun.getNome() : null);
    return dto;
  }

  /** Tipo de documento pela REFERENCIA de RH_T_TIPOS_DOCUMENTOS — ex.: EMISSAO_REQUISICAO. */
  public Optional<TipoDocumentoEntity> tipoDocumento(String referencia) {
    return entityManager
        .createQuery("select t from TipoDocumentoEntity t where t.referencia = :referencia order by t.id", TipoDocumentoEntity.class)
        .setParameter("referencia", referencia)
        .setMaxResults(1)
        .getResultStream()
        .findFirst();
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
    return conteudo("MISSAO_PRESTADOR", vars, editado,
        "Pedido de Proposta - " + vars.get("tipoProcesso") + " - Missão Nº " + vars.get("nrMissao"),
        corpoPedidoPropostaPorDefeito(vars));
  }

  /**
   * Conteúdo do email da requisição ao prestador (template MISSAO_EMISSAO_REQUISICAO). Além das
   * variáveis da missão aceita {nrRequisicao}, {valorTotal} e {colaboradores}.
   */
  public Conteudo conteudoRequisicao(Map<String, String> vars) {
    return conteudo("MISSAO_EMISSAO_REQUISICAO", vars, null,
        "Requisição " + vars.get("nrRequisicao") + " - Missão Nº " + vars.get("nrMissao"),
        "Exmo(a) Sr(a),\n\n"
            + "Requisita-se o serviço de " + vars.get("tipoProcesso") + " para a missão de serviço Nº "
            + vars.get("nrMissao") + ", conforme a proposta apresentada:\n"
            + "- Nota de encomenda: " + vars.get("nrRequisicao") + "\n"
            + "- Destino: " + vars.get("destino") + "\n"
            + "- Datas: " + vars.get("dataInicio") + " a " + vars.get("dataFim") + "\n"
            + "- Colaboradores: " + vars.get("colaboradores") + "\n"
            + "- Valor total: " + vars.get("valorTotal") + "\n\n"
            + "Com os melhores cumprimentos,\nINPS - Recursos Humanos");
  }

  private Conteudo conteudo(String tipoNotificacao, Map<String, String> vars, MissaoNotificacaoRequestDTO editado,
                            String assuntoPorDefeito, String corpoPorDefeito) {
    var template = paramNotificacaoRepository.findFirstByTipoNotificacaoAndEstadoOrderByIdDesc(tipoNotificacao, ESTADO_ATIVO);

    var assunto = editado != null && StringUtils.hasText(editado.getAssunto())
        ? editado.getAssunto()
        : template.map(t -> substituir(t.getAssunto(), vars)).filter(StringUtils::hasText).orElse(assuntoPorDefeito);

    var corpo = editado != null && StringUtils.hasText(editado.getCorpoEmail())
        ? editado.getCorpoEmail()
        : template.map(t -> substituir(t.getCorpo(), vars)).filter(StringUtils::hasText).orElse(corpoPorDefeito);

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
