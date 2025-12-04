package cv.inps.rh.configuracao.domain.service.engine;

import com.fasterxml.jackson.databind.ObjectMapper;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

public abstract class ConfigurationProcess<T> {

  @Autowired
  protected Validator validator;

  @Autowired
  protected ObjectMapper jsonMapper;

  public final Object doCreate(Object payload) {
    T value = jsonMapper.convertValue(payload, getType());
    validate(value);
    return create(value);
  }

  public final void doUpdate(String id, Object payload) {
    T value = jsonMapper.convertValue(payload, getType());
    validate(value);
    update(id, value);
  }

  protected abstract Class<T> getType();

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
