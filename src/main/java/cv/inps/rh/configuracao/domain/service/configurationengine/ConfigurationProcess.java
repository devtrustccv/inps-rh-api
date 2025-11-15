package cv.inps.rh.configuracao.domain.service.configurationengine;

import com.fasterxml.jackson.databind.ObjectMapper;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import jakarta.validation.Validator;

import java.util.List;
import java.util.Map;

public abstract class ConfigurationProcess<T> {

  protected final Validator validator;
  protected final ObjectMapper jsonMapper;
  private final Class<T> type;

  protected ConfigurationProcess(Validator validator, ObjectMapper jsonMapper, Class<T> type) {
    this.validator = validator;
    this.jsonMapper = jsonMapper;
    this.type = type;
  }

  protected abstract Object create(T payload);

  protected abstract void update(String id, T payload);

  // TODO 15/11/2025 15:07 apply validations for the request classes

  /*public final Object create(T payload) {
    validate(payload);
    return doCreate(payload);
  }

  public final Object update(String id, T payload) {
    validate(payload);
    return doUpdate(id, payload);
  }*/

  public abstract List<Object> list(Map<String, String> filters);

  public abstract void delete(String id);

  private void validate(T payload) {

    var violations = validator.validate(payload);

    if (!violations.isEmpty()) {

      var errors = violations.stream()
          .map(v -> v.getPropertyPath() + ": " + v.getMessage())
          .toList();

      throw IgrpResponseStatusException.badRequest("Validation failed", errors);
    }
  }

}
