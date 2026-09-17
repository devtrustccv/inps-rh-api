package cv.inps.rh.shared.security;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Activa os componentes do iGRP Auth ({@code cv.igrp.framework.auth}).
 *
 * <p>A lib so auto-configura o {@code ApiClient}; o {@code AuthorizationSyncRunner} (registo do
 * recurso e das permissoes no Acesso), o {@code igrpAuthorization} e a method-security sao
 * {@code @Component}/{@code @Configuration} e so entram com component scan. No inss_core_service
 * esse scan vem, por arrasto, do {@code @ComponentScan("cv.igrp.framework")} da lib de relatorios
 * Jasper, que o RH nao usa.</p>
 */
@Configuration
@ComponentScan("cv.igrp.framework.auth")
public class IgrpAuthConfig {
}
