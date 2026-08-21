package cv.inps.rh.shared.application.service;

import java.util.Map;
import java.util.Set;

/**
 * Configuração, por tipo de referência de validação, da grelha "Detalhe de alterações" servida pelo
 * {@link JaversValidacaoDetalheReadService}. Cada módulo de negócio que queira alimentar a grelha via
 * JaVers contribui <b>um</b> bean que implementa esta interface — o read-service descobre-os todos por
 * injeção e indexa-os por {@link #referenciaName()}. Assim, ligar um novo módulo (carreira, contrato…)
 * não exige tocar no serviço partilhado: basta um {@code @Component} pequeno como este.
 *
 * <p>Do lado da ESCRITA, o chamador continua a ter apenas de carimbar o {@code repository.save(...)}
 * com {@code ValidacaoAuditContext.set(validacaoId, validacaoUuid, "RH_T_<TABELA>")} (no REGISTO e na
 * EDIÇÃO), para o auto-audit do JaVers disparar.
 */
public interface ValidacaoDetalheDescriptor {

  /**
   * Valor de {@code RH_T_VALIDACAO.REFERENCIA_NAME} a que este descritor responde (ex.: {@code
   * "MOBILIDADE"}). É a chave de indexação — tem de ser único entre descritores.
   */
  String referenciaName();

  /**
   * Sufixo do nome do tipo JaVers da entidade-alvo (ex.: {@code "MobilidadeEntity"}). Usado para, em
   * conjunto com o {@code referenciaId} da validação, isolar a instância exata e evitar colisões de
   * cdoId entre tipos (ex.: {@code SecaoEntity/634} vs {@code MobilidadeEntity/634}).
   */
  String entityTypeSuffix();

  /**
   * Allow-list dos campos de NEGÓCIO (nomes das propriedades Java da entidade) que a grelha mostra.
   * Lista fechada: tudo o que não estiver aqui — estado de workflow, FKs estruturais, carimbos de
   * auditoria — fica de fora por omissão.
   */
  Set<String> camposNegocio();

  /**
   * Rótulos PT para a coluna "Campo alterado": propriedade Java → rótulo visível. Um campo sem entrada
   * cai para o próprio nome da propriedade (nunca fica em branco).
   */
  Map<String, String> rotulos();
}
