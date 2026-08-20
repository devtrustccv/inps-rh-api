package cv.inps.rh.shared.infrastructure.audit;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Limpa o {@link ValidacaoAuditContext} no fim de cada request. O write-service já limpa no seu
 * {@code finally}, mas como o holder é {@link ThreadLocal} sobre um pool de threads reutilizadas,
 * esta rede de segurança impede que uma validação "vaze" para o request seguinte caso algo falhe
 * antes do finally.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ValidacaoAuditContextFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    try {
      filterChain.doFilter(request, response);
    } finally {
      ValidacaoAuditContext.clear();
    }
  }
}
