package cv.inps.rh.configuracao.domain.service;

import java.util.List;
import java.util.Map;

public interface IConfiguration {

  Object create(Object objectBody, String configurationType);

  Object update(String id, Object objectBody, String configurationType);

  List<Object> list(Map<String, String> filters, String configurationType);

  void delete(String id, String configurationType);
}
