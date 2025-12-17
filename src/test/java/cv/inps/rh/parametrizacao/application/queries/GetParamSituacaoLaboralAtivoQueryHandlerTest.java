package cv.inps.rh.parametrizacao.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetParamSituacaoLaboralAtivoQueryHandlerTest {

  @InjectMocks
  private GetParamSituacaoLaboralAtivoQueryHandler getParamSituacaoLaboralAtivoQueryHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandleGetParamSituacaoLaboralAtivoQuery() {

    // Example:
    // Given
    // GetParamSituacaoLaboralAtivoQuery query = new GetParamSituacaoLaboralAtivoQuery(...);
    //
    // When
    // ResponseEntity<List<ParametrizacaoDTO>> response = getParamSituacaoLaboralAtivoQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
