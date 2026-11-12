package cv.inps.rh.shared.domain.service.documento;

import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamDocOutputEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ResponsavelEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamDocOutputEntityRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Motor dos Documentos de Saída (Configuração → Documentos Output): carrega o documento ativo de um tipo e
 * substitui os marcadores {@code ${nome}} do título, corpo e "Assinado por" pelos valores que o chamador
 * passar — de onde vierem (BD, cálculo, texto fixo). Regras dos marcadores em {@link MarcadoresTexto}.
 *
 * <p>Guia de uso para programadores: {@code docs/guia_documentos_saida_marcadores.html}.</p>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DocumentoSaidaRenderer {

  private static final Logger LOGGER = LoggerFactory.getLogger(DocumentoSaidaRenderer.class);

  private final ParamDocOutputEntityRepository repository;

  /** Renderiza o documento ativo do tipo; marcadores sem valor saem como "—" (com aviso no log). */
  public DocumentoRenderizado render(String tipoDocumento, Map<String, ?> valores) {
    return render(documentoAtivo(tipoDocumento), valores);
  }

  /** Igual a {@link #render(String, Map)}, mas um marcador sem valor (nem valor por defeito) dá 400. */
  public DocumentoRenderizado renderEstrito(String tipoDocumento, Map<String, ?> valores) {
    var doc = render(tipoDocumento, valores);
    if (!doc.getMarcadoresEmFalta().isEmpty())
      throw IgrpResponseStatusException.badRequest("Faltam valores para os marcadores "
          + doc.getMarcadoresEmFalta() + " do documento " + tipoDocumento + ".");
    return doc;
  }

  /** Renderiza um documento já carregado (ex.: para pré-visualizar uma versão que ainda não está ativa). */
  public DocumentoRenderizado render(ParamDocOutputEntity doc, Map<String, ?> valores) {
    var emFalta = new LinkedHashSet<String>();
    var titulo = MarcadoresTexto.substituir(doc.getTitulo(), valores, false, emFalta);
    var corpo = MarcadoresTexto.substituir(doc.getCorpo(), valores, false, emFalta);
    var corpoHtml = MarcadoresTexto.substituir(doc.getCorpo(), valores, true, null);
    var assinadoPor = MarcadoresTexto.substituir(doc.getAssinadoPor(), valores, false, emFalta);

    if (!emFalta.isEmpty())
      LOGGER.warn("Documento de saída {} (id {}): marcadores sem valor {} — saíram como '{}'.",
          doc.getTipoDocumento(), doc.getId(), emFalta, MarcadoresTexto.SEM_VALOR);

    return new DocumentoRenderizado(doc.getTipoDocumento(), titulo, corpo, corpoHtml, assinadoPor,
        nomeResponsavel(doc.getResponsavel()), Set.copyOf(emFalta));
  }

  /** Marcadores usados no documento ativo do tipo (título, corpo e "Assinado por"). */
  public Set<String> marcadores(String tipoDocumento) {
    return marcadores(documentoAtivo(tipoDocumento));
  }

  public Set<String> marcadores(ParamDocOutputEntity doc) {
    var out = new LinkedHashSet<String>();
    out.addAll(MarcadoresTexto.marcadoresDe(doc.getTitulo()));
    out.addAll(MarcadoresTexto.marcadoresDe(doc.getCorpo()));
    out.addAll(MarcadoresTexto.marcadoresDe(doc.getAssinadoPor()));
    return out;
  }

  public ParamDocOutputEntity documentoAtivo(String tipoDocumento) {
    return repository.findByTipoDocumentoAndEstado(tipoDocumento, Estado.A.name())
        .orElseThrow(() -> IgrpResponseStatusException.notFound(
            "Não há Documento de Saída ativo para o tipo " + tipoDocumento + "."));
  }

  private static String nomeResponsavel(ResponsavelEntity responsavel) {
    return Optional.ofNullable(responsavel)
        .map(ResponsavelEntity::getFunId)
        .map(FuncionarioEntity::getNome)
        .orElse(null);
  }
}
