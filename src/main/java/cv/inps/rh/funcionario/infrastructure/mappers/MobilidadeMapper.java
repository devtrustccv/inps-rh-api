package cv.inps.rh.funcionario.infrastructure.mappers;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.funcionario.application.dto.MobilidadeDTO;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.infrastructure.persistence.entity.DirecaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MobilidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ParamLocalTrabEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SecaoEntity;
import cv.inps.rh.shared.util.ValidationUtil;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class MobilidadeMapper {

  private final EntityManager entityManager;

  public MobilidadeDTO mobilidadeDTO(MobilidadeEntity mobilidade) {

    var dto = new MobilidadeDTO();

    // Posição em vigor: vai toda no lado sem sufixo. Não há Destino — isto não é um movimento por
    // concretizar, é onde o colaborador está.
    aplicarPosicao(dto, mobilidade);

    dto.setTipoMobilidade(mobilidade.getTipoSituacao());
    dto.setDataInicio(mobilidade.getDataInicio());
    dto.setDataFim(mobilidade.getDataFim());
    aplicarEstado(dto, mobilidade);

    return dto;

  }

  /**
   * Detalhe/editar de uma mobilidade específica. A forma do response é governada pelo <b>estado</b> do
   * registo:
   *
   * <ul>
   *   <li><b>Por validar (P/C)</b> — há um movimento por concretizar. Os campos <b>sem sufixo</b>
   *       levam a posição de onde se parte (o pai, {@code RH_T_MOBILIDADE.MOB_ID}: onde o colaborador
   *       está enquanto isto não é aplicado) e os campos <b>Destino</b> levam para onde vai (os
   *       valores deste registo). É o que o ecrã de validação precisa para comparar, e o que o
   *       formulário de edição reenvia no PUT.</li>
   *   <li><b>Consolidado (A/I)</b> — já não há para onde ir. Devolve-se apenas o registo pedido, nos
   *       campos sem sufixo, com o lado Destino todo a null. Não interessa se é o vínculo em vigor ou
   *       histórico: é o registo cujo id foi pedido.</li>
   *   <li><b>Por validar sem pai</b> (ex. um INICIO ainda pendente, ou registos gravados antes de
   *       MOB_ID ser preenchido) — não há de onde partir, logo não é um movimento: trata-se como
   *       consolidado, com os valores do próprio registo no lado sem sufixo.</li>
   * </ul>
   *
   * @param atual a mobilidade que está a ser vista/editada
   * @param pai   a mobilidade em vigor antes desta (RH_T_MOBILIDADE.MOB_ID); pode ser null
   */
  public MobilidadeDTO mobilidadeDetalheDTO(MobilidadeEntity atual, MobilidadeEntity pai) {
    var dto = new MobilidadeDTO();

    if (porValidar(atual.getEstado()) && pai != null) {
      // Sem sufixo = de onde parte (o pai); Destino = para onde vai (este registo). Os ids do lado
      // Destino alimentam os selects do formulário; as descrições servem o ecrã de validação, que é
      // só leitura e não carrega as listas.
      aplicarPosicao(dto, pai);

      if (Objects.nonNull(atual.getInstidId())) {
        dto.setDirecaoDestino(atual.getInstidId().getId());
        dto.setDirecaoDestinoDesc(atual.getInstidId().getNome());
      }
      if (Objects.nonNull(atual.getSecaoId())) {
        dto.setSeccaoDestino(atual.getSecaoId().getId());
        dto.setSeccaoDestinoDesc(atual.getSecaoId().getNome());
      }
      if (Objects.nonNull(atual.getLocalTrabId())) {
        dto.setLocalTrabalhoDestino(atual.getLocalTrabId().getId());
        dto.setLocalTrabalhoDestinoDesc(atual.getLocalTrabId().getNome());
      }
    } else {
      aplicarPosicao(dto, atual);
    }

    dto.setTipoMobilidade(atual.getTipoSituacao());
    dto.setDataInicio(atual.getDataInicio());
    dto.setDataFim(atual.getDataFim());
    aplicarEstado(dto, atual);
    return dto;
  }

  /** Registo ainda em ciclo de validação: pendente (P) ou devolvido para correção (C). */
  private boolean porValidar(Estado estado) {
    return Estado.P.equals(estado) || Estado.C.equals(estado);
  }

  /** Preenche o lado sem sufixo com a direção/secção/local de uma mobilidade. */
  private void aplicarPosicao(MobilidadeDTO dto, MobilidadeEntity mobilidade) {
    if (Objects.nonNull(mobilidade.getInstidId())) {
      dto.setDirecaoId(mobilidade.getInstidId().getId());
      dto.setDirecaoDesc(mobilidade.getInstidId().getNome());
    }
    if (Objects.nonNull(mobilidade.getSecaoId())) {
      dto.setSeccaoId(mobilidade.getSecaoId().getId());
      dto.setSeccaoDesc(mobilidade.getSecaoId().getNome() != null ? mobilidade.getSecaoId().getNome() : "");
    }
    if (Objects.nonNull(mobilidade.getLocalTrabId())) {
      dto.setLocalTrabalhoId(mobilidade.getLocalTrabId().getId());
      dto.setLocalTrabalhoDesc(mobilidade.getLocalTrabId().getNome());
    }
  }

  private void aplicarEstado(MobilidadeDTO dto, MobilidadeEntity mobilidade) {
    var estado = mobilidade.getEstado();
    if (estado == null) return;
    dto.setEstado(estado.getCode());
    dto.setEstadoDesc(estado.getDescription());
  }

  public MobilidadeEntity toMobilidade(DadosContratuaisReqDTO dc, Estado estado) {
    if (dc == null) return null;
    var me = new MobilidadeEntity();
    me.setTipoSituacao("NOVO_CONTRATO");
    me.setObs("NOVO_CONTRATO");
    me.setUuid(UuidCreator.getTimeOrderedEpoch());
    me.setLocalTrabId(ValidationUtil.ref(entityManager, ParamLocalTrabEntity.class, dc.getLocalTrabalhoId()));
    me.setSecaoId(ValidationUtil.ref(entityManager, SecaoEntity.class, dc.getSeccaoId()));
    me.setInstidId(ValidationUtil.ref(entityManager, DirecaoEntity.class, dc.getDirecaoId()));
    me.setDataInicio(dc.getDataInicio());
    me.setDataFim(dc.getDataFim());
    me.setEstado(estado);
    return me;
  }

  public void toUpdateEntity(MobilidadeEntity mobilidade, DadosContratuaisReqDTO dc) {
    if (dc == null) return;
    mobilidade.setTipoSituacao("NOVO_CONTRATO");
    mobilidade.setObs("NOVO_CONTRATO");
    mobilidade.setLocalTrabId(ValidationUtil.ref(entityManager, ParamLocalTrabEntity.class, dc.getLocalTrabalhoId()));
    mobilidade.setSecaoId(ValidationUtil.ref(entityManager, SecaoEntity.class, dc.getSeccaoId()));
    mobilidade.setInstidId(ValidationUtil.ref(entityManager, DirecaoEntity.class, dc.getDirecaoId()));
    mobilidade.setDataInicio(dc.getDataInicio());
    mobilidade.setDataFim(dc.getDataFim());
  }

}
