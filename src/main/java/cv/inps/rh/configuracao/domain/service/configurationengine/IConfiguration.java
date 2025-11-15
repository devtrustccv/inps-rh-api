package cv.inps.rh.configuracao.domain.service.configurationengine;

import java.util.List;
import java.util.Map;

public interface IConfiguration {

  Object create(Object objectBody, String configurationType);

  void update(String id, Object objectBody, String configurationType);

  Object read(String id, String configurationType);

  List<Object> list(Map<String, String> filters, String configurationType);

  void delete(String id, String configurationType);
}
