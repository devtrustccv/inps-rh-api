package cv.inps.rh.avaliacao.application.services;

import cv.inps.rh.avaliacao.application.dto.AtitudePessoalAvaliacaoDTO;
import cv.inps.rh.avaliacao.application.dto.AvaliacaoDTO;
import cv.inps.rh.avaliacao.application.dto.CompetenciaComportAvaliacaoDTO;
import cv.inps.rh.avaliacao.application.dto.CompetenciaTecAvaliacaoDTO;
import cv.inps.rh.avaliacao.application.dto.ObjectivoAvaliacaoDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoAtitudePessoalEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoCompetenciaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoObjectivoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamEscalaAvaliacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamObjetivoDetEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamObjetivoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoAtitudePessoalEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoCompetenciaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AvaliacaoObjectivoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamEscalaAvaliacaoEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ParamObjetivoDetEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProcessoAvaliacaoServiceAvaliacaoTest {

  @Mock
  private AvaliacaoEntityRepository avaliacaoRepository;

  @Mock
  private AvaliacaoObjectivoEntityRepository objectivoRepository;

  @Mock
  private AvaliacaoCompetenciaEntityRepository competenciaRepository;

  @Mock
  private AvaliacaoAtitudePessoalEntityRepository atitudeRepository;

  @Mock
  private ParamObjetivoDetEntityRepository objetivoDetRepository;

  @Mock
  private ParamEscalaAvaliacaoEntityRepository escalaRepository;

  @InjectMocks
  private ProcessoAvaliacaoService service;

  @Test
  void gravarAvaliacaoAtualizaCamposEAvaliaGlobalConformeFormulario() {
    var avaliacaoUuid = UUID.randomUUID();

    var avaliacao = new AvaliacaoEntity();
    avaliacao.setUuid(avaliacaoUuid);
    avaliacao.setAno(2026);
    avaliacao.setSemestre("1");
    when(avaliacaoRepository.findByUuidOrThrow(avaliacaoUuid)).thenReturn(avaliacao);
    when(avaliacaoRepository.save(any(AvaliacaoEntity.class))).thenAnswer(inv -> inv.getArgument(0));

    var o1 = new AvaliacaoObjectivoEntity();
    o1.setNumeroOrdem(1);
    o1.setPonderacao(BigDecimal.valueOf(50));
    o1.setEstado("A");

    var o2 = new AvaliacaoObjectivoEntity();
    o2.setNumeroOrdem(2);
    o2.setPonderacao(BigDecimal.valueOf(50));
    o2.setEstado("A");

    when(objectivoRepository.findAllByAvaliacaoObj_Uuid(avaliacaoUuid)).thenReturn(List.of(o1, o2));
    when(objectivoRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

    var cComport = new AvaliacaoCompetenciaEntity();
    cComport.setNumeroOrdem(1);
    cComport.setComponente("COMPETENCIA_COMPORTAMENTAL");
    cComport.setPonderacao(BigDecimal.valueOf(100));
    cComport.setEstado("A");

    var cTec = new AvaliacaoCompetenciaEntity();
    cTec.setNumeroOrdem(1);
    cTec.setComponente("COMPETENCIA_TECNICA");
    cTec.setPonderacao(BigDecimal.valueOf(100));
    cTec.setEstado("A");

    when(competenciaRepository.findAllByAvaliacao_Uuid(avaliacaoUuid)).thenReturn(List.of(cComport, cTec));
    when(competenciaRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

    var pAt = new ParamObjetivoEntity();
    pAt.setNumeroOrdem(1);
    var at = new AvaliacaoAtitudePessoalEntity();
    at.setParamObjetivo(pAt);
    at.setPonderacao(BigDecimal.valueOf(100));
    at.setEstado("A");

    when(atitudeRepository.findAllByAvaliacao_Uuid(avaliacaoUuid)).thenReturn(List.of(at));
    when(atitudeRepository.saveAll(any())).thenAnswer(inv -> inv.getArgument(0));

    var det = new ParamObjetivoDetEntity();
    det.setAno(2026);
    det.setPonderacaoObjetivo(BigDecimal.valueOf(50));
    det.setPonderacaoCompetencia(BigDecimal.valueOf(30));
    det.setPonderacaoAtitudePess(BigDecimal.valueOf(20));
    when(objetivoDetRepository.findTopByAnoOrderByIdDesc(2026)).thenReturn(Optional.of(det));

    var e = new ParamEscalaAvaliacaoEntity();
    e.setEstado(Estado.A);
    e.setQuantitativaDe(BigDecimal.valueOf(3));
    e.setQuantitativaAte(BigDecimal.valueOf(4.99));
    e.setQualitativa("Bom");
    when(escalaRepository.findAll()).thenReturn(List.of(e));

    var dto = new AvaliacaoDTO();

    var oDto1 = new ObjectivoAvaliacaoDTO();
    oDto1.setNumero(1);
    oDto1.setRealizado("R1");
    oDto1.setAvaliacao(4);
    var oDto2 = new ObjectivoAvaliacaoDTO();
    oDto2.setNumero(2);
    oDto2.setRealizado("R2");
    oDto2.setAvaliacao(2);
    dto.setObjectivos(List.of(oDto1, oDto2));

    var cc = new CompetenciaComportAvaliacaoDTO();
    cc.setNumeroOrdem(1);
    cc.setAvaliacao(5);
    dto.setCompetenciasComportamentais(List.of(cc));

    var ct = new CompetenciaTecAvaliacaoDTO();
    ct.setNumeroOrdem(1);
    ct.setAvaliacao(3);
    dto.setCompetenciasTecnicas(List.of(ct));

    var ap = new AtitudePessoalAvaliacaoDTO();
    ap.setNumeroOrdem(1);
    ap.setAvaliacao(4);
    dto.setAtitudesPessoais(List.of(ap));

    var resp = service.gravarAvaliacao(avaliacaoUuid.toString(), dto);

    assertNotNull(resp.getBody());
    assertEquals(avaliacaoUuid, resp.getBody().get("id"));

    assertEquals("R1", o1.getRealizado());
    assertEquals(BigDecimal.valueOf(4), o1.getAvaliacao());
    assertEquals("R2", o2.getRealizado());
    assertEquals(BigDecimal.valueOf(2), o2.getAvaliacao());

    assertEquals(BigDecimal.valueOf(5), cComport.getAvaliacaoProcessual());
    assertEquals(BigDecimal.valueOf(3), cTec.getAvaliacaoProcessual());
    assertEquals(BigDecimal.valueOf(4), at.getAvaliacaoProcessual());

    assertEquals(new BigDecimal("1.50"), avaliacao.getAvaliacaoObjectivo());
    assertEquals(new BigDecimal("2.40"), avaliacao.getAvaliacaoCompetencia());
    assertEquals(new BigDecimal("0.80"), avaliacao.getAvaliacaoAtitudePess());
    assertEquals("Bom", avaliacao.getAvaliacaoQualitativa());
    assertEquals("P", avaliacao.getEstado());
  }
}

