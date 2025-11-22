package cv.inps.rh.parametrizacao.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetDominiosQueryHandlerTest {

  @InjectMocks
  private GetDominiosQueryHandler getDominiosQueryHandler;

  @BeforeEach
  void setUp() {

  }

  @Test
  void testHandleGetDominiosQuery() {

    // Example:
    // Given
    // GetDominiosQuery query = new GetDominiosQuery(...);
    //
    // When
    // ResponseEntity<List<DominioDTO>> response = getDominiosQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
