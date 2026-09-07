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

    if (Objects.nonNull(mobilidade.getInstidId())) {
      dto.setDirecaoOrigemDesc(mobilidade.getInstidId().getNome());
      dto.setDirecaoOrigemId(mobilidade.getInstidId().getId());
    }

    if (Objects.nonNull(mobilidade.getSecaoId()))
      dto.setSeccaoOrigemDesc(mobilidade.getSecaoId().getNome() != null ? mobilidade.getSecaoId().getNome() : "");

    if (Objects.nonNull(mobilidade.getLocalTrabId()))
      dto.setLocalTrabalhoOrigemDesc(mobilidade.getLocalTrabId().getNome());

    dto.setTipoMobilidade(mobilidade.getTipoSituacao());
    dto.setDataInicio(mobilidade.getDataInicio());
    dto.setDataFim(mobilidade.getDataFim());
    aplicarEstado(dto, mobilidade);

    return dto;

  }

  /**
   * Detalhe/editar de uma mobilidade específica.
   *
   * <p>O par Origem/Destino é preenchido por comparação com o <b>pai</b> ({@code MOB_ID}, a
   * mobilidade que estava em vigor quando esta foi registada), dimensão a dimensão:
   *
   * <ul>
   *   <li><b>mudou</b> (valor difere do pai) → Origem = valor do pai, Destino = valor deste registo.
   *       "Destino preenchido" passa a significar exactamente "isto alterou-se", que é o que o ecrã
   *       de validação precisa de destacar.</li>
   *   <li><b>não mudou</b> → Origem = valor deste registo, Destino = null. Evita devolver o mesmo
   *       valor nos dois lados (fazia o ecrã parecer que a dimensão tinha mudado).</li>
   *   <li><b>sem pai</b> (INICIO, NOVO_CONTRATO, registos antigos sem MOB_ID) → não é um movimento,
   *       é uma posição: tudo em Origem, Destino todo null. Fica igual ao {@code /atual}.</li>
   * </ul>
   *
   * <p>Compara-se com o pai em vez de interpretar o {@code tipoSituacao} porque este último é
   * declarativo (o que foi escolhido no ecrã) e pode desalinhar-se dos valores reais — há registos
   * onde as edições acumularam tipos que não descrevem um movimento.
   *
   * @param atual a mobilidade que está a ser vista/editada
   * @param pai   a mobilidade em vigor antes desta (RH_T_MOBILIDADE.MOB_ID); pode ser null
   */
  public MobilidadeDTO mobilidadeDetalheDTO(MobilidadeEntity atual, MobilidadeEntity pai) {
    var dto = new MobilidadeDTO();

    var direcao = atual.getInstidId();
    var secao = atual.getSecaoId();
    var localTrab = atual.getLocalTrabId();

    var direcaoPai = pai != null ? pai.getInstidId() : null;
    var secaoPai = pai != null ? pai.getSecaoId() : null;
    var localTrabPai = pai != null ? pai.getLocalTrabId() : null;

    // Direção. O id do lado escolhido alimenta os selects do formulário; a descrição serve o ecrã de
    // validação, que é só leitura e não carrega as listas.
    if (mudou(direcao != null ? direcao.getId() : null, direcaoPai != null ? direcaoPai.getId() : null)) {
      dto.setDirecaoOrigemId(direcaoPai.getId());
      dto.setDirecaoOrigemDesc(direcaoPai.getNome());
      dto.setDirecaoDestino(direcao.getId());
      dto.setDirecaoDestinoDesc(direcao.getNome());
    } else if (Objects.nonNull(direcao)) {
      dto.setDirecaoOrigemId(direcao.getId());
      dto.setDirecaoOrigemDesc(direcao.getNome());
    }

    // Secção
    if (mudou(secao != null ? secao.getId() : null, secaoPai != null ? secaoPai.getId() : null)) {
      dto.setSeccaoOrigemDesc(secaoPai.getNome());
      dto.setSeccaoDestino(secao.getId());
      dto.setSeccaoDestinoDesc(secao.getNome());
    } else if (Objects.nonNull(secao)) {
      dto.setSeccaoOrigemDesc(secao.getNome() != null ? secao.getNome() : "");
    }

    // Local de trabalho
    if (mudou(localTrab != null ? localTrab.getId() : null, localTrabPai != null ? localTrabPai.getId() : null)) {
      dto.setLocalTrabalhoOrigemDesc(localTrabPai.getNome());
      dto.setLocalTrabalhoDestino(localTrab.getId());
      dto.setLocalTrabalhoDestinoDesc(localTrab.getNome());
    } else if (Objects.nonNull(localTrab)) {
      dto.setLocalTrabalhoOrigemDesc(localTrab.getNome());
    }

    dto.setTipoMobilidade(atual.getTipoSituacao());
    dto.setDataInicio(atual.getDataInicio());
    dto.setDataFim(atual.getDataFim());
    aplicarEstado(dto, atual);
    return dto;
  }

  /**
   * Houve movimento nesta dimensão? Só quando existe pai (há com que comparar), o registo tem valor e
   * os dois diferem. Sem pai não é um movimento — é a posição inicial.
   */
  private boolean mudou(Long valor, Long valorPai) {
    return valor != null && valorPai != null && !Objects.equals(valor, valorPai);
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
