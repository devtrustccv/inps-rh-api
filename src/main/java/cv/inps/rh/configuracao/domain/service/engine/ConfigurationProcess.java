package cv.inps.rh.configuracao.domain.service.engine;

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

  public Object doCreate(Object payload) {
    T value = jsonMapper.convertValue(payload, type);
    validate(value);
    return create(value);
  }

  public void doUpdate(String id, Object payload) {
    T value = jsonMapper.convertValue(payload, type);
    validate(value);
    update(id, value);
  }

  protected abstract Object create(T payload);

  protected abstract Object update(String id, T payload);

  protected abstract Object read(String id);

  public abstract List<Object> list(Map<String, String> filters);

  public abstract void delete(String id);

  private void validate(T payload) {

    var errors = validator.validate(payload)
        .stream()
        .map(v -> v.getPropertyPath() + ": " + v.getMessage())
        .toList();

    if (!errors.isEmpty())
      throw IgrpResponseStatusException.badRequest("Validation errors", errors);
  }

}
