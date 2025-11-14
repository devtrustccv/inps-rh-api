package cv.inps.rh.configuracao.domain.service.configurationengine;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class Configuration implements IConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(Configuration.class);

  private final ConfigurationFactory serviceProcessFactory;

  @Override
  public Object create(Object objectBody, String configurationType) {

    LOGGER.debug("CONFIGURATION TYPE: {}, PAYLOAD: {}", configurationType, objectBody);

    return serviceProcessFactory.getServiceProcess(configurationType).create(objectBody);
  }

  @Override
  public Object update(String id, Object objectBody, String configurationType) {

    LOGGER.debug("CONFIGURATION TYPE: {}, PAYLOAD: {}", configurationType, objectBody);

    return serviceProcessFactory.getServiceProcess(configurationType).update(id, objectBody);
  }

  @Override
  public List<Object> list(Map<String, String> filters, String configurationType) {

    LOGGER.debug("CONFIGURATION TYPE: {}, FILTERS: {}", configurationType, filters);

    return serviceProcessFactory.getServiceProcess(configurationType).list(filters);
  }

  @Override
  public void delete(String id, String configurationType) {

    LOGGER.debug("CONFIGURATION TYPE: {}, ID: {}", configurationType, id);

    serviceProcessFactory.getServiceProcess(configurationType).delete(id);
  }
}
