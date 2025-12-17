package cv.inps.rh.configuracao.domain.service.engine;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class ConfigurationFactory {

  private final ApplicationContext applicationContext;

  public ConfigurationFactory(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
  }

  @SuppressWarnings("unchecked")
  public <T> ConfigurationProcess<T> getServiceProcess(String configurationType) {
    return (ConfigurationProcess<T>) applicationContext.getBean(configurationType, ConfigurationProcess.class);
  }
}
