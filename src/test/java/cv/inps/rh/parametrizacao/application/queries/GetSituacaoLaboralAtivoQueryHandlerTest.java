package cv.inps.rh.parametrizacao.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetSituacaoLaboralAtivoQueryHandlerTest {

  @InjectMocks
  private GetParamSituacaoLaboralAtivoQueryHandler getSituacaoLaboralAtivoQueryHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandleGetSituacaoLaboralAtivoQuery() {

    // Example:
    // Given
    // GetSituacaoLaboralAtivoQuery query = new GetSituacaoLaboralAtivoQuery(...);
    //
    // When
    // ResponseEntity<List<ParametrizacaoDTO>> response = getSituacaoLaboralAtivoQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
