package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.shared.application.detalhe.Campos;
import cv.inps.rh.shared.infrastructure.persistence.entity.MobilidadeEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.MobilidadeEntity_;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

/**
 * Que campos da MOBILIDADE entram na grelha "Detalhe de alterações", com que rótulo e por que ordem.
 * É a <b>fonte única</b>: acrescentar ou tirar um campo da grelha é mexer só aqui.
 *
 * <p>Fora, por omissão: {@code estado} (workflow), {@code funId}/{@code mobId} (estruturais) e os
 * carimbos de auditoria.
 *
 * <p>Substitui o {@code MobilidadeValidacaoDetalheDescriptor}, que declarava os mesmos campos em duas
 * estruturas separadas e por <em>string</em> — onde renomear um campo da entidade esvaziava a grelha
 * sem erro nenhum. Aqui o metamodel torna isso um erro de compilação.
 */
@Component
public class MobilidadeCampos implements Supplier<Campos<MobilidadeEntity>> {

  // Lazy de propósito: os SingularAttribute do metamodel são volatile e só ficam preenchidos quando
  // o EntityManagerFactory arranca. Num campo static (ou num @PostConstruct precoce) vinham a null.
  private final Lazy<Campos<MobilidadeEntity>> campos = Lazy.of(() ->
      Campos.de(MobilidadeEntity.class)
          .campo(MobilidadeEntity_.tipoSituacao, "Tipo de mobilidade")
          .referencia(MobilidadeEntity_.instidId, "Direcção")
          .referencia(MobilidadeEntity_.secaoId, "Secção")
          .referencia(MobilidadeEntity_.localTrabId, "Local de trabalho")
          .data(MobilidadeEntity_.dataInicio, "Data início")
          .data(MobilidadeEntity_.dataFim, "Data fim")
          .campo(MobilidadeEntity_.obs, "Observações"));

  @Override
  public Campos<MobilidadeEntity> get() {
    return campos.get();
  }
}
