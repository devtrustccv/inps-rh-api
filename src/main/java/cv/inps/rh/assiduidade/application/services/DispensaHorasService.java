package cv.inps.rh.assiduidade.application.services;

import cv.inps.rh.assiduidade.application.dto.HorasDispensaStatusDTO;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.repository.AssiduidadeParametroEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.DispensaEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.FaltaEntityRepository;
import cv.inps.rh.shared.application.constants.Estado;
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

    /** Valor de "nenhum pedido a excluir" — a query compara sempre, sem null. */
    private static final Long SEM_PEDIDO_A_EXCLUIR = -1L;

    private final DispensaEntityRepository dispensaRepository;
    private final AssiduidadeParametroEntityRepository assiduidadeParametroEntityRepository;
    private final FaltaEntityRepository faltaEntityRepository;

    public HorasDispensaStatusDTO getHorasStatus(UUID funcionarioUuid, LocalDate dataReferencia) {
      return getHorasStatus(funcionarioUuid, dataReferencia, null, null);
    }

    public HorasDispensaStatusDTO getHorasStatus(
        UUID funcionarioUuid, LocalDate dataReferencia, Long dispensaIdExcluir) {
      return getHorasStatus(funcionarioUuid, dataReferencia, dispensaIdExcluir, null);
    }

    /**
     * @param dispensaIdExcluir dispensa a não contar nas horas usadas — necessário ao
     *                          editar, senão a própria dispensa em edição contava contra
     *                          o saldo e o total ficava duplicado.
     * @param pedidoIdExcluir   pedido de falta a não reservar. Obrigatório no despacho: a
     *                          falta que está a ser aprovada ainda está em {@code P} e sem
     *                          esta exclusão reservaria horas contra si própria.
     */
    public HorasDispensaStatusDTO getHorasStatus(
        UUID funcionarioUuid, LocalDate dataReferencia, Long dispensaIdExcluir, Long pedidoIdExcluir) {

      var inicioMes = dataReferencia.withDayOfMonth(1);
      var fimMes = dataReferencia.withDayOfMonth(dataReferencia.lengthOfMonth());

      // Só as APROVADAS consomem saldo (decisão de negócio, 10/09): uma dispensa pendente ainda
      // não reserva as horas, e uma rejeitada ou eliminada devolve-as. Sem este filtro, uma
      // dispensa posta a 'E' pelo eliminar continuava a comer as horas do mês.
      var listaMes = dispensaRepository.findAllByPedidoId_FunId_UuidAndDataInicioBetweenAndEstado(
          funcionarioUuid, inicioMes, fimMes, Estado.A);

      int usadasMin = 0;
      for (var d : listaMes) {
        if (dispensaIdExcluir != null && dispensaIdExcluir.equals(d.getId()))
          continue;
        usadasMin += TimeUtils.diffMinutes(d.getHoraInicio(), d.getHoraFim());
      }

      // Reserva: as faltas ainda pendentes de despacho que tencionam deduzir em dispensa já
      // comprometem as horas do mês. Sem isto, dois pedidos pendentes viam ambos as 4h livres
      // e o segundo a ser despachado ia ao vencimento sem aviso no registo.
      //
      // Reserva-se a ausência inteira do dia, que é o máximo que a falta pode vir a consumir.
      // É deliberadamente conservador: a cobertura real é min(disponível, ausência) e só se
      // conhece no despacho, mas reservar a menos deixaria voltar a surpresa que isto resolve.
      var pendentes = faltaEntityRepository.findPendentesDeducaoDispensaNoPeriodo(
          funcionarioUuid, inicioMes, fimMes,
          pedidoIdExcluir != null ? pedidoIdExcluir : SEM_PEDIDO_A_EXCLUIR);

      int reservadasMin = 0;
      for (var f : pendentes)
        reservadasMin += TimeUtils.parseHorasFlexivel(
            TimeUtils.intervalFormatToHHmm(f.getHorasAusencia()));

      int disponiveisMin = TimeUtils.parseHorasFlexivel(
          assiduidadeParametroEntityRepository.findActiveTDispensa().orElse(null));

      int restantesMin = Math.max(0, disponiveisMin - usadasMin - reservadasMin);

      var dto = new HorasDispensaStatusDTO();
      dto.setHorasDisponiveis(TimeUtils.formatMinutesToHHmm(disponiveisMin));
      dto.setHorasUsadas(TimeUtils.formatMinutesToHHmm(usadasMin));
      dto.setHorasReservadas(TimeUtils.formatMinutesToHHmm(reservadasMin));
      dto.setHorasRestantes(TimeUtils.formatMinutesToHHmm(restantesMin));
      dto.setHorasDisponiveisMinutos(disponiveisMin);
      dto.setHorasUsadasMinutos(usadasMin);
      dto.setHorasReservadasMinutos(reservadasMin);
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

      // As reservadas entram na conta: horas comprometidas por uma falta à espera de despacho
      // não estão disponíveis para uma dispensa nova. Somá-las aqui é o que mantém a regra
      // depois de terem sido separadas do campo "usadas", que só conta o já consumido.
      int totalMin = status.getHorasUsadasMinutos()
          + status.getHorasReservadasMinutos()
          + minutosSolicitados;

      if (totalMin > status.getHorasDisponiveisMinutos())
        throw IgrpResponseStatusException.badRequest(String.format(
            "Horas de dispensa insuficientes: o colaborador tem direito a %s por mês, "
                + "já usou %s, tem %s reservadas por faltas pendentes e está a pedir %s (total %s).",
            status.getHorasDisponiveis(),
            status.getHorasUsadas(),
            status.getHorasReservadas(),
            TimeUtils.formatMinutesToHHmm(minutosSolicitados),
            TimeUtils.formatMinutesToHHmm(totalMin)));

      return status;
    }

}
