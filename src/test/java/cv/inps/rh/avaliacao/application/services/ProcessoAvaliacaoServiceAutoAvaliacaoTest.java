package cv.inps.rh.avaliacao.application.services;

import cv.inps.rh.avaliacao.application.dto.AtitudePessoalAvaliacaoDTO;
import cv.inps.rh.avaliacao.application.dto.AvaliacaoDTO;
import cv.inps.rh.avaliacao.application.dto.CompetenciaComportAvaliacaoDTO;
import cv.inps.rh.avaliacao.application.dto.CompetenciaTecAvaliacaoDTO;
import cv.inps.rh.avaliacao.application.dto.ObjectivoAvaliacaoDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoAtitudePessoalEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoCompetenciaEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.AvaliacaoObjectivoEntity;
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
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProcessoAvaliacaoServiceAutoAvaliacaoTest {

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
  void gravarAutoAvaliacaoAtualizaAutoCamposSemRecalculo() {
    var avaliacaoUuid = UUID.randomUUID();

    var avaliacao = new AvaliacaoEntity();
    avaliacao.setUuid(avaliacaoUuid);
    avaliacao.setAno(2026);
    avaliacao.setSemestre("1");
    when(avaliacaoRepository.findByUuidOrThrow(avaliacaoUuid)).thenReturn(avaliacao);

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

    var dto = new AvaliacaoDTO();
    var oDto1 = new ObjectivoAvaliacaoDTO();
    oDto1.setNumero(1);
    oDto1.setRealizado("REAL-1");
    oDto1.setAvaliacao(4);
    var oDto2 = new ObjectivoAvaliacaoDTO();
    oDto2.setNumero(2);
    oDto2.setRealizado("REAL-2");
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

    var resp = service.gravarAutoAvaliacao(avaliacaoUuid.toString(), dto);

    assertNotNull(resp.getBody());
    assertEquals(avaliacaoUuid, resp.getBody().get("id"));

    assertEquals("REAL-1", o1.getAutoRealizado());
    assertEquals(BigDecimal.valueOf(4), o1.getAutoAvaliacao());
    assertEquals("REAL-2", o2.getAutoRealizado());
    assertEquals(BigDecimal.valueOf(2), o2.getAutoAvaliacao());

    assertEquals(BigDecimal.valueOf(5), cComport.getAutoAvaliacao());
    assertEquals(BigDecimal.valueOf(3), cTec.getAutoAvaliacao());
    assertEquals(BigDecimal.valueOf(4), at.getAutoAvaliacao());

    assertNull(avaliacao.getAvaliacaoObjectivo());
    assertNull(avaliacao.getAvaliacaoCompetencia());
    assertNull(avaliacao.getAvaliacaoAtitudePess());
    assertNull(avaliacao.getAvaliacaoQualitativa());
  }
}
