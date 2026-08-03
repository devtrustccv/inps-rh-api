package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.HorasDispensaStatusDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.AssiduidadeParametroEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DispensaEntityRepository;
import cv.inps.rh.shared.util.TimeUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Saldo de horas de dispensa.
 *
 * <p>O direito mensal vem de {@code RH_T_ASSIDUIDADE_PARAMETRO.T_DISPENSA}, que em BD
 * aparece gravado nos dois formatos — como número de horas ({@code "4"}) e como
 * {@code HH:MM} ({@code "04:00"}). {@link #parseHoras} aceita ambos; ler só como
 * {@code HH:MM} fazia o valor "4" cair para zero silenciosamente.
 */
@Service
@RequiredArgsConstructor
public class DispensaHorasService {

    private final DispensaEntityRepository dispensaRepository;
    private final AssiduidadeParametroEntityRepository assiduidadeParametroEntityRepository;

    public HorasDispensaStatusDTO getHorasStatus(UUID funcionarioUuid, LocalDate dataReferencia) {
      return getHorasStatus(funcionarioUuid, dataReferencia, null);
    }

    /**
     * @param dispensaIdExcluir dispensa a não contar nas horas usadas — necessário ao
     *                          editar, senão a própria dispensa em edição contava contra
     *                          o saldo e o total ficava duplicado.
     */
    public HorasDispensaStatusDTO getHorasStatus(
        UUID funcionarioUuid, LocalDate dataReferencia, Long dispensaIdExcluir) {

      var inicioMes = dataReferencia.withDayOfMonth(1);
      var fimMes = dataReferencia.withDayOfMonth(dataReferencia.lengthOfMonth());

      var listaMes = dispensaRepository.findAllByPedidoId_FunId_UuidAndDataBetween(
          funcionarioUuid, inicioMes, fimMes);

      int usadasMin = 0;
      for (var d : listaMes) {
        if (dispensaIdExcluir != null && dispensaIdExcluir.equals(d.getId()))
          continue;
        usadasMin += TimeUtils.diffMinutes(d.getHoraInicio(), d.getHoraFim());
      }

      int disponiveisMin = parseHoras(
          assiduidadeParametroEntityRepository.findActiveTDispensa().orElse(null));

      int restantesMin = Math.max(0, disponiveisMin - usadasMin);

      var dto = new HorasDispensaStatusDTO();
      dto.setHorasDisponiveis(TimeUtils.formatMinutesToHHmm(disponiveisMin));
      dto.setHorasUsadas(TimeUtils.formatMinutesToHHmm(usadasMin));
      dto.setHorasRestantes(TimeUtils.formatMinutesToHHmm(restantesMin));
      dto.setHorasDisponiveisMinutos(disponiveisMin);
      dto.setHorasUsadasMinutos(usadasMin);
      dto.setHorasRestantesMinutos(restantesMin);
      return dto;
    }

    /**
     * Valida se cabem {@code minutosSolicitados} no saldo do mês e devolve o estado.
     *
     * <p>A verificação anterior comparava apenas as horas <em>já usadas</em> com as
     * disponíveis, sem somar as que estavam a ser pedidas: com 0 usadas e 4h de direito,
     * um pedido de 9h passava.
     */
    public HorasDispensaStatusDTO validarSaldo(
        UUID funcionarioUuid, LocalDate dataReferencia, int minutosSolicitados, Long dispensaIdExcluir) {

      var status = getHorasStatus(funcionarioUuid, dataReferencia, dispensaIdExcluir);

      if (status.getHorasDisponiveisMinutos() <= 0)
        throw IgrpResponseStatusException.badRequest(
            "Não há direito de horas de dispensa parametrizado "
                + "(RH_T_ASSIDUIDADE_PARAMETRO.T_DISPENSA) — não é possível registar dispensas.");

      int totalMin = status.getHorasUsadasMinutos() + minutosSolicitados;

      if (totalMin > status.getHorasDisponiveisMinutos())
        throw IgrpResponseStatusException.badRequest(String.format(
            "Horas de dispensa insuficientes: o colaborador tem direito a %s por mês, "
                + "já usou %s e está a pedir %s (total %s).",
            status.getHorasDisponiveis(),
            status.getHorasUsadas(),
            TimeUtils.formatMinutesToHHmm(minutosSolicitados),
            TimeUtils.formatMinutesToHHmm(totalMin)));

      return status;
    }

    /**
     * Aceita {@code "4"}, {@code "4.5"} e {@code "04:00"}.
     *
     * @return minutos; 0 se não for interpretável.
     */
    static int parseHoras(String valor) {
      if (!StringUtils.hasText(valor))
        return 0;

      var v = valor.trim();

      if (v.contains(":"))
        return TimeUtils.hhmmToMinutes(v);

      try {
        return (int) Math.round(Double.parseDouble(v.replace(',', '.')) * 60);
      } catch (NumberFormatException e) {
        return 0;
      }
    }
}
