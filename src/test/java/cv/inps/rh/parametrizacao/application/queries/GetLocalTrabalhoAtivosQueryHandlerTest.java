package cv.inps.rh.parametrizacao.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetLocalTrabalhoAtivosQueryHandlerTest {

  @InjectMocks
  private GetLocalTrabalhoAtivosQueryHandler getLocalTrabalhoAtivosQueryHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandleGetLocalTrabalhoAtivosQuery() {

    // Example:
    // Given
    // GetLocalTrabalhoAtivosQuery query = new GetLocalTrabalhoAtivosQuery(...);
    //
    // When
    // ResponseEntity<List<ParametrizacaoDTO>> response = getLocalTrabalhoAtivosQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
