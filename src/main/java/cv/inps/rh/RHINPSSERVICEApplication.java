package cv.inps.rh;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.domain.models.Funcionario;
import cv.inps.rh.funcionario.domain.repository.FuncionarioRepository;
import cv.inps.rh.shared.config.ApplicationAuditorAware;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditAware", dateTimeProviderRef = "auditDateTimeProvider")
public class RHINPSSERVICEApplication {

  private final FuncionarioEntityRepository funcionarioRepository;

  public RHINPSSERVICEApplication(FuncionarioEntityRepository funcionarioRepository) {
    this.funcionarioRepository = funcionarioRepository;
  }

  @Bean
  public AuditorAware<String> auditAware() {
    return new ApplicationAuditorAware();
  }

  @Bean
  public DateTimeProvider auditDateTimeProvider() {
    return () -> Optional.of(LocalDateTime.now());
  }

  public static void main(String[] args) {
    SpringApplication.run(RHINPSSERVICEApplication.class, args);

  }

}
