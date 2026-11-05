package cv.inps.rh.processamento.domain.service.processamentosalarial;

import cv.inps.rh.processamento.application.dto.ColaboradoresAumentoRowDTO;
import cv.inps.rh.processamento.application.queries.GetColaboresAumentoSalarialQuery;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.AumentoSalarialEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.AumentoSalarialEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.AumentoSimulacaoEntityRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** A lista de colaboradores só mostra as linhas do aumento pedido. */
@ExtendWith(MockitoExtension.class)
class AumentoSalarialReadServiceTest {

  private static final String UUID = "019aeaec-faba-7224-a402-7c0f512a2192";

  @Mock private AumentoSalarialEntityRepository aumentoRepository;
  @Mock private AumentoSimulacaoEntityRepository simulacaoRepository;
  @InjectMocks private AumentoSalarialReadService service;

  @Test
  void filtraPeloIdDoAumentoEPelosFiltrosOpcionais() {
    var aumento = mock(AumentoSalarialEntity.class);
    when(aumento.getId()).thenReturn(17L);
    when(aumentoRepository.findByUuid(UUID)).thenReturn(Optional.of(aumento));
    var pageable = PageRequest.of(0, 20);
    var linha = new ColaboradoresAumentoRowDTO("Nome", "Carreira", "A1", 100L, 105L);
    when(simulacaoRepository.list(17L, 7L, 8L, pageable)).thenReturn(new PageImpl<>(List.of(linha), pageable, 1));

    var resposta = service.getColaboradores(new GetColaboresAumentoSalarialQuery(UUID, 7L, 8L, 0, 20));

    assertThat(resposta.getContent()).containsExactly(linha);
    verify(simulacaoRepository).list(17L, 7L, 8L, pageable);
  }

  @Test
  void aumentoInexistenteDaErroENaoConsultaAsLinhas() {
    when(aumentoRepository.findByUuid(UUID)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getColaboradores(new GetColaboresAumentoSalarialQuery(UUID, null, null, 0, 20)))
        .isInstanceOf(IgrpResponseStatusException.class);

    verify(simulacaoRepository, never()).list(anyLong(), any(), any(), any());
  }
}
