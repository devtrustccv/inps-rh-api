package cv.inps.rh.parametrizacao.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetVinculosAtivosQueryHandlerTest {

  @InjectMocks
  private GetVinculosAtivosQueryHandler getVinculosAtivosQueryHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandleGetVinculosAtivosQuery() {

    // Example:
    // Given
    // GetVinculosAtivosQuery query = new GetVinculosAtivosQuery(...);
    //
    // When
    // ResponseEntity<List<ParametrizacaoDTO>> response = getVinculosAtivosQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
