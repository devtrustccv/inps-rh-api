package cv.inps.rh.funcionario.application.service.detalhe;

import cv.inps.rh.shared.application.detalhe.Campos;
import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.CarreiraEntity_;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoHistoricoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ContratoHistoricoEntity_;
import cv.inps.rh.shared.infrastructure.persistence.entity.DadosBancariosEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DadosBancariosEntity_;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefPagamentoEntity_;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.DefinicaoRemuneracaoEntity_;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessoDisciplinarEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.ProcessoDisciplinarEntity_;
import cv.inps.rh.shared.infrastructure.persistence.entity.SituacaoLaboralEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SituacaoLaboralEntity_;
import cv.inps.rh.shared.infrastructure.persistence.entity.SubstituicaoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.SubstituicaoEntity_;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.TiposRelacionamentoEntity_;
import org.springframework.data.util.Lazy;
import org.springframework.stereotype.Component;

/**
 * Declaração dos campos da grelha "Detalhe de alterações" para todos os módulos do DOSSIÊ, num só
 * sítio. Cada método devolve a declaração de uma entidade: que campos entram, com que rótulo e por
 * que ordem — <b>fonte única</b>, sem segunda lista para manter em sincronia.
 *
 * <p>Substitui os {@code ValidacaoDetalheDescriptor}, que declaravam o mesmo por <em>string</em> em
 * duas estruturas separadas ({@code camposNegocio()} + {@code rotulos()}). O metamodel JPA
 * ({@code hibernate-jpamodelgen}) torna erro de compilação o que antes esvaziava a grelha em
 * silêncio. Dois casos reais apanhados na migração:
 * <ul>
 *   <li>o descritor da CARREIRA declarava {@code "tipoCarreira"} — campo que <b>não existe</b> na
 *       entidade (chama-se {@code tipoSituacao}); a grelha nunca o mostrou e ninguém deu por isso;</li>
 *   <li>o mesmo descritor declarava {@code "carrPccsId"} entre campos escalares, sem marcar que é FK.</li>
 * </ul>
 *
 * <p><b>Lazy obrigatório:</b> os {@code SingularAttribute} do metamodel são {@code volatile} e só
 * ficam preenchidos quando o {@code EntityManagerFactory} arranca. Num campo {@code static} vinham a
 * {@code null}.
 */
@Component
public class DossierCampos {

  public static final String T_CARREIRA = "RH_T_CARREIRA";
  public static final String T_TIPREL = "RH_T_TIPOS_RELACIONAMENTO";
  public static final String T_SUBSTITUICAO = "RH_T_SUBSTITUICAO";
  public static final String T_DADOS_BANCARIOS = "RH_T_DADOS_BANCARIOS";
  public static final String T_SITUACAO_LABORAL = "RH_T_SITUACAO_LABORAL";
  public static final String T_PROCESSO_DISCIPLINAR = "RH_T_PROCESSO_DISCIPLINAR";
  public static final String T_DEF_REMUNERACOES = "RH_T_DEF_REMUNERACOES";
  public static final String T_DEF_PAGAMENTOS = "RH_T_DEF_PAGAMENTOS";
  public static final String T_CONTRATO_HISTORICO = "RH_T_CONTRATO_HISTORICO";
  // Registo de colaborador: o dossie atravessa estas tabelas na mesma validacao.
  public static final String T_FUNCIONARIOS = "RH_T_FUNCIONARIOS";
  public static final String T_CONTRATO_VINCULO = "RH_T_CONTRATO_VINCULO";
  public static final String T_CONTACTO = "RH_T_CONTACTO";
  public static final String T_ENDERECO = "RH_T_ENDERECO";
  public static final String T_FAMILIARES = "RH_T_FAMILIARES";
  public static final String T_HABILITACOES = "RH_T_HABILITACOES_LITERARIAS";
  public static final String T_DOCUMENTO_PESSOAL = "RH_T_DOCUMENTO_PESSOAL";

  // ---------------------------------------------------------------------------------------------

  private final Lazy<Campos<CarreiraEntity>> carreira = Lazy.of(() ->
      Campos.de(CarreiraEntity.class)
          .campo(CarreiraEntity_.tipoSituacao, "Tipo de carreira")
          .referencia(CarreiraEntity_.carrPccsId, "Carreira (PCCS)")
          .referencia(CarreiraEntity_.categoriaId, "Categoria")
          .referencia(CarreiraEntity_.escalaoId, "Escalão")
          .referencia(CarreiraEntity_.cargoId, "Cargo")
          .montante(CarreiraEntity_.salario, "Salário")
          .campo(CarreiraEntity_.flgProcessa, "Processa salário")
          .data(CarreiraEntity_.dataInicio, "Data início")
          .data(CarreiraEntity_.dataFim, "Data fim")
          .campo(CarreiraEntity_.obs, "Observações"));

  /** Alteração de Escalão/Cargo — o movimento altera o próprio tiprel. */
  private final Lazy<Campos<TiposRelacionamentoEntity>> escalao = Lazy.of(() ->
      Campos.de(TiposRelacionamentoEntity.class)
          .referencia(TiposRelacionamentoEntity_.escalaoId, "Escalão")
          .referencia(TiposRelacionamentoEntity_.cargoId, "Cargo")
          .montante(TiposRelacionamentoEntity_.salario, "Salário")
          .campo(TiposRelacionamentoEntity_.moeda, "Moeda")
          .campo(TiposRelacionamentoEntity_.tipoSituacao, "Tipo de alteração")
          .data(TiposRelacionamentoEntity_.dataInicio, "Data início")
          .data(TiposRelacionamentoEntity_.dataFim, "Data fim")
          .campo(TiposRelacionamentoEntity_.obs, "Observações"));

  private final Lazy<Campos<SubstituicaoEntity>> substituicao = Lazy.of(() ->
      Campos.de(SubstituicaoEntity.class)
          // O rótulo do tiprel é o NOME do funcionário — resolvido pelo override tipado do
          // ReferenciaNomeResolver, que já existia para este caso.
          .referencia(SubstituicaoEntity_.substituidoTiprelId, "Colaborador substituído")
          .campo(SubstituicaoEntity_.motivo, "Motivo")
          .data(SubstituicaoEntity_.dataInicio, "Data início")
          .data(SubstituicaoEntity_.dataFim, "Data fim")
          .campo(SubstituicaoEntity_.obs, "Observações"));

  private final Lazy<Campos<DadosBancariosEntity>> dadosBancarios = Lazy.of(() ->
      Campos.de(DadosBancariosEntity.class)
          .referencia(DadosBancariosEntity_.rhbId, "Entidade bancária")
          .campo(DadosBancariosEntity_.numConta, "Nº de conta")
          .campo(DadosBancariosEntity_.nib, "NIB")
          .data(DadosBancariosEntity_.dataInicio, "Data início")
          .data(DadosBancariosEntity_.dataFim, "Data fim"));

  private final Lazy<Campos<SituacaoLaboralEntity>> situacaoLaboral = Lazy.of(() ->
      Campos.de(SituacaoLaboralEntity.class)
          .referencia(SituacaoLaboralEntity_.situacaoLaboralId, "Situação laboral")
          // ParamSituacaoDetalheEntity chama "motivo" ao seu rótulo — ver GETTERS_CANDIDATOS.
          .referencia(SituacaoLaboralEntity_.motivoSitLabId, "Motivo")
          .data(SituacaoLaboralEntity_.dataInicio, "Data início")
          .data(SituacaoLaboralEntity_.dataFim, "Data fim")
          .campo(SituacaoLaboralEntity_.obs, "Observações"));

  private final Lazy<Campos<ProcessoDisciplinarEntity>> processoDisciplinar = Lazy.of(() ->
      Campos.de(ProcessoDisciplinarEntity.class)
          .campo(ProcessoDisciplinarEntity_.numProceso, "Nº processo")
          .campo(ProcessoDisciplinarEntity_.entidade, "Entidade")
          .campo(ProcessoDisciplinarEntity_.tpProcesso, "Tipo de processo")
          .campo(ProcessoDisciplinarEntity_.penaDiscp, "Pena disciplinar")
          .data(ProcessoDisciplinarEntity_.dateInicPd, "Data início processo")
          .data(ProcessoDisciplinarEntity_.dateFimPd, "Data fim processo")
          .data(ProcessoDisciplinarEntity_.dateInicPena, "Data início pena")
          .data(ProcessoDisciplinarEntity_.dateFimPena, "Data fim pena")
          .campo(ProcessoDisciplinarEntity_.numOrdemServ, "Nº ordem de serviço")
          .data(ProcessoDisciplinarEntity_.dataOrdemServ, "Data ordem de serviço"));

  private final Lazy<Campos<DefinicaoRemuneracaoEntity>> rendimento = Lazy.of(() ->
      Campos.de(DefinicaoRemuneracaoEntity.class)
          .referencia(DefinicaoRemuneracaoEntity_.tmId, "Tipo de movimento")
          .montante(DefinicaoRemuneracaoEntity_.valor, "Valor")
          .montante(DefinicaoRemuneracaoEntity_.percentagem, "Percentagem")
          .campo(DefinicaoRemuneracaoEntity_.moeda, "Moeda")
          .data(DefinicaoRemuneracaoEntity_.dataInicio, "Data início")
          .data(DefinicaoRemuneracaoEntity_.dataFim, "Data fim")
          .campo(DefinicaoRemuneracaoEntity_.obs, "Observações"));

  private final Lazy<Campos<DefPagamentoEntity>> desconto = Lazy.of(() ->
      Campos.de(DefPagamentoEntity.class)
          .referencia(DefPagamentoEntity_.tmId, "Tipo de movimento")
          .montante(DefPagamentoEntity_.valor, "Valor")
          .montante(DefPagamentoEntity_.percentagem, "Percentagem")
          .campo(DefPagamentoEntity_.nib, "NIB")
          .campo(DefPagamentoEntity_.nif, "NIF")
          .data(DefPagamentoEntity_.dataInicio, "Data início")
          .data(DefPagamentoEntity_.dataFim, "Data fim")
          .campo(DefPagamentoEntity_.obs, "Observações"));

  private final Lazy<Campos<ContratoHistoricoEntity>> renovacaoContrato = Lazy.of(() ->
      Campos.de(ContratoHistoricoEntity.class)
          .data(ContratoHistoricoEntity_.dataInicio, "Data início")
          .data(ContratoHistoricoEntity_.dataFim, "Data fim")
          .campo(ContratoHistoricoEntity_.duracao, "Duração (meses)"));

  // ---------------------------------------------------------------------------------------------

  public Campos<CarreiraEntity> carreira() {
    return carreira.get();
  }

  public Campos<TiposRelacionamentoEntity> escalao() {
    return escalao.get();
  }

  public Campos<SubstituicaoEntity> substituicao() {
    return substituicao.get();
  }

  public Campos<DadosBancariosEntity> dadosBancarios() {
    return dadosBancarios.get();
  }

  public Campos<SituacaoLaboralEntity> situacaoLaboral() {
    return situacaoLaboral.get();
  }

  public Campos<ProcessoDisciplinarEntity> processoDisciplinar() {
    return processoDisciplinar.get();
  }

  public Campos<DefinicaoRemuneracaoEntity> rendimento() {
    return rendimento.get();
  }

  public Campos<DefPagamentoEntity> desconto() {
    return desconto.get();
  }

  public Campos<ContratoHistoricoEntity> renovacaoContrato() {
    return renovacaoContrato.get();
  }

  // --- Registo de colaborador: nucleo do dossie + filhos ---

  private final Lazy<Campos<cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity>> funcionario =
      Lazy.of(() -> Campos.de(cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity.class)
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity_.nome, "Nome")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity_.nif, "NIF")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity_.numDocumento, "N.º de documento")
          .referencia(cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity_.tipoDocumentoId, "Tipo de documento")
          .data(cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity_.dataNascimento, "Data de nascimento")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity_.sexo, "Género")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity_.estadoCivil, "Estado civil")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity_.nacionalidade, "Nacionalidade")
          .referencia(cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity_.locNascId, "Naturalidade")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity_.localidade, "Localidade")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity_.nmPai, "Nome do pai")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity_.nmMae, "Nome da mãe")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity_.nuSegInps, "N.º de segurado"));

  public Campos<cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity> funcionario() {
    return funcionario.get();
  }

  private final Lazy<Campos<cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity>> contrato =
      Lazy.of(() -> Campos.de(cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity.class)
          .referencia(cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity_.tpContratoId, "Tipo de contrato")
          .referencia(cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity_.vinculoId, "Vínculo")
          .data(cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity_.dataInicio, "Data início")
          .data(cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity_.dataFim, "Data fim")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity_.duracao, "Duração (meses)"));

  public Campos<cv.inps.rh.shared.infrastructure.persistence.entity.ContratoEntity> contrato() {
    return contrato.get();
  }

  private final Lazy<Campos<cv.inps.rh.shared.infrastructure.persistence.entity.ContactoEntity>> contacto =
      Lazy.of(() -> Campos.de(cv.inps.rh.shared.infrastructure.persistence.entity.ContactoEntity.class)
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.ContactoEntity_.tipoContacto, "Tipo de contacto")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.ContactoEntity_.contacto, "Contacto"));

  public Campos<cv.inps.rh.shared.infrastructure.persistence.entity.ContactoEntity> contacto() {
    return contacto.get();
  }

  private final Lazy<Campos<cv.inps.rh.shared.infrastructure.persistence.entity.EnderecoEntity>> endereco =
      Lazy.of(() -> Campos.de(cv.inps.rh.shared.infrastructure.persistence.entity.EnderecoEntity.class)
          .referencia(cv.inps.rh.shared.infrastructure.persistence.entity.EnderecoEntity_.paisId, "País")
          .referencia(cv.inps.rh.shared.infrastructure.persistence.entity.EnderecoEntity_.ilhaId, "Ilha")
          .referencia(cv.inps.rh.shared.infrastructure.persistence.entity.EnderecoEntity_.concelhoId, "Concelho")
          .referencia(cv.inps.rh.shared.infrastructure.persistence.entity.EnderecoEntity_.freguesiaId, "Freguesia")
          .referencia(cv.inps.rh.shared.infrastructure.persistence.entity.EnderecoEntity_.zonaId, "Zona")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.EnderecoEntity_.morada, "Morada"));

  public Campos<cv.inps.rh.shared.infrastructure.persistence.entity.EnderecoEntity> endereco() {
    return endereco.get();
  }

  private final Lazy<Campos<cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity>> familiar =
      Lazy.of(() -> Campos.de(cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity.class)
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity_.nome, "Nome")
          .referencia(cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity_.tpDocumentoId, "Tipo de documento")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity_.numDocumento, "N.º de documento")
          .data(cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity_.dataNascimento, "Data de nascimento")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity_.sexo, "Sexo")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity_.dependencia, "Dependência")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity_.membroAgr, "Membro do agregado")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity_.responsavel, "Responsável")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity_.nmPai, "Nome do pai")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity_.nmMae, "Nome da mãe"));

  public Campos<cv.inps.rh.shared.infrastructure.persistence.entity.FamiliarEntity> familiar() {
    return familiar.get();
  }

  private final Lazy<Campos<cv.inps.rh.shared.infrastructure.persistence.entity.HabilitacaoLiterariaEntity>> habilitacao =
      Lazy.of(() -> Campos.de(cv.inps.rh.shared.infrastructure.persistence.entity.HabilitacaoLiterariaEntity.class)
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.HabilitacaoLiterariaEntity_.nivel, "Nível")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.HabilitacaoLiterariaEntity_.area, "Área")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.HabilitacaoLiterariaEntity_.nomeCurso, "Curso")
          .referencia(cv.inps.rh.shared.infrastructure.persistence.entity.HabilitacaoLiterariaEntity_.estabelecimento, "Estabelecimento")
          .referencia(cv.inps.rh.shared.infrastructure.persistence.entity.HabilitacaoLiterariaEntity_.paisId, "País")
          .data(cv.inps.rh.shared.infrastructure.persistence.entity.HabilitacaoLiterariaEntity_.dataInicio, "Data início")
          .data(cv.inps.rh.shared.infrastructure.persistence.entity.HabilitacaoLiterariaEntity_.dataFim, "Data fim")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.HabilitacaoLiterariaEntity_.concluido, "Concluído"));

  public Campos<cv.inps.rh.shared.infrastructure.persistence.entity.HabilitacaoLiterariaEntity> habilitacao() {
    return habilitacao.get();
  }

  private final Lazy<Campos<cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoPessoalEntity>> documentoPessoal =
      Lazy.of(() -> Campos.de(cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoPessoalEntity.class)
          .referencia(cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoPessoalEntity_.tipoDocumentoId, "Tipo de documento")
          .campo(cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoPessoalEntity_.numDocumento, "N.º de documento"));

  public Campos<cv.inps.rh.shared.infrastructure.persistence.entity.DocumentoPessoalEntity> documentoPessoal() {
    return documentoPessoal.get();
  }
}
