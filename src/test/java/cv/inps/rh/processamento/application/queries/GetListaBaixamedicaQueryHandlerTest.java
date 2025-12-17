package cv.inps.rh.processamento.application.queries;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class GetListaBaixamedicaQueryHandlerTest {

  @InjectMocks
  private GetListaBaixamedicaQueryHandler getListaBaixamedicaQueryHandler;

  @BeforeEach
  void setUp() {
  }

  @Test
  void testHandleGetListaBaixamedicaQuery() {
    // Example:
    // Given
    // GetListaBaixamedicaQuery query = new GetListaBaixamedicaQuery(...);
    //
    // When
    // ResponseEntity<WrapperListaBaixaMedicaDTO> response = getListaBaixamedicaQueryHandler.handle(query);
    //
    // Then
    // assertNotNull(response);
    // assertEquals(..., response.getBody());
  }

}
