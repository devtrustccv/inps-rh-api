package cv.inps.rh.shared.application.detalhe;

/**
 * Uma alteração detectada entre dois estados: o campo, o rótulo que o aprovador vê, e os dois
 * valores. Para FKs guarda-se também o id, que é o que distingue de facto as duas referências (o
 * nome é só apresentação e pode mudar depois).
 */
public record CampoAlterado(
    String campo,
    String rotulo,
    String valorAnterior,
    Long valorAnteriorId,
    String valorNovo,
    Long valorNovoId,
    int ordem,
    Tipo tipo) {

  /**
   * VALOR — escalar (texto, data, montante). REFERENCIA — FK resolvida para nome. INICIAL — o campo
   * não tinha valor antes; distingue "criado com Direção X" de "Direção X → Y".
   */
  public enum Tipo {
    VALOR, REFERENCIA, INICIAL
  }
}
