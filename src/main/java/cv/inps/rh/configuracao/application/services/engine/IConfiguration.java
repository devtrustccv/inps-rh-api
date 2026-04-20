package cv.inps.rh.configuracao.application.services.engine;

import java.util.Map;

public interface IConfiguration {

  Object create(Object objectBody, String configurationType);

  void update(String id, Object objectBody, String configurationType);

  Object read(String id, String configurationType);

  Object list(Map<String, String> filters, String configurationType);

  void delete(String id, String configurationType);
}
