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
  public Object create(Object payload, String configurationType) {

    LOGGER.debug("CREATE - CONFIGURATION TYPE: {}, PAYLOAD: {}", configurationType, payload);

    return serviceProcessFactory.getServiceProcess(configurationType).doCreate(payload);
  }

  @Override
  public void update(String id, Object payload, String configurationType) {

    LOGGER.debug("UPDATE - CONFIGURATION TYPE: {}, ID: {}, PAYLOAD: {}", configurationType, id, payload);

    serviceProcessFactory.getServiceProcess(configurationType).doUpdate(id, payload);
  }

  @Override
  public Object read(String id, String configurationType) {

    LOGGER.debug("READ - CONFIGURATION TYPE: {}, ID: {}", configurationType, id);

    return serviceProcessFactory.getServiceProcess(configurationType).read(id);
  }

  @Override
  public List<Object> list(Map<String, String> filters, String configurationType) {

    LOGGER.debug("LIST - CONFIGURATION TYPE: {}, FILTERS: {}", configurationType, filters);

    return serviceProcessFactory.getServiceProcess(configurationType).list(filters);
  }

  @Override
  public void delete(String id, String configurationType) {

    LOGGER.debug("DELETE - CONFIGURATION TYPE: {}, ID: {}", configurationType, id);

    serviceProcessFactory.getServiceProcess(configurationType).delete(id);
  }
}
