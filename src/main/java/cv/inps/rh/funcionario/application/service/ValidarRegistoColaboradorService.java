package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarRegistoColaboradorCommand;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.application.constants.custom.Referencia;
import cv.inps.rh.shared.application.constants.custom.TipoAcao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import cv.inps.rh.shared.infrastructure.persistence.entity.OrdemServicoEntity;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class ValidarRegistoColaboradorService {

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioMapper funcionarioMapper;
  private final ContactoMapper contactoMapper;
  private final FamiliarMapper familiarMapper;
  private final HabilitacaoLiterariaMapper habilitacaoLiterariaMapper;
  private final FormacaoFeitaMapper formacaoFeitaMapper;
  private final ExperienciaProfissionalMapper experienciaProfissionalMapper;
  private final DocumentoMapper documentoMapper;
  private final DadosBancariosMapper dadosBancariosMapper;
  private final DefinicaoRemuneracaoMapper definicaoRemuneracaoMapper;
  private final DefPagamentoMapper defPagamentoMapper;
  private final ContratoMapper contratoMapper;
  private final CarreiraMapper carreiraMapper;
  private final MobilidadeMapper mobilidadeMapper;
  private final RegimeTrabalhoMapper regimeTrabalhoMapper;
  private final DadosContratuaisMapper dadosContratuaisMapper;
  private final FuncionarioRules funcionarioRules;



  @Transactional
  public Map<String, ?> validarRegistoColaborador(ValidarRegistoColaboradorCommand command) {

    var registroColaborador = command.getFuncionariorequest();
    var dadosContratuais = registroColaborador.getDadosContratuais();

    var funcionarioPublicId = IdentificadorUnico.from(command.getId()).valor();

    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);

    if(!funcionarioRules.temValidacaoPendente(funcionario.getUuid(), TipoAcao.INSERT, Referencia.REGISTO_COLABORADOR)){
       throw IgrpResponseStatusException.badRequest("funcionario nao tem validacao pendente para o tipo de acao: INSERT e referencia: REGISTO_COLABORADOR");
    }

    var dadosPessoaisReqDTO = registroColaborador.getDadosPessoais();
    funcionario = funcionarioMapper.toUpdateEntity(funcionario, dadosPessoaisReqDTO);

    var contactos = contactoMapper.syncContactos(funcionario.getContactos(), dadosPessoaisReqDTO != null ? dadosPessoaisReqDTO.getContactos() : null);
    var familiares = familiarMapper.syncFamiliares(funcionario.getFamiliares(), registroColaborador.getFamiliares());

    var dadosAcademicosProf = registroColaborador.getDadosAcademicosProf();

    var habilitacoesLiterarias = habilitacaoLiterariaMapper.syncHabilitacoes(funcionario.getHabilitacoesLiterarias(), dadosAcademicosProf.getHabilitacoesLiterarias());
    var formacoesFeitas = formacaoFeitaMapper.syncFormacoes(funcionario.getFormacoesFeitas(), dadosAcademicosProf.getFormacoesFeitas());
    var experienciasProfissionais = experienciaProfissionalMapper.syncExperiencias(funcionario.getExperienciasProfissionais(), dadosAcademicosProf.getExperienciasProfssionais());


    var documentos = documentoMapper.syncDocumentos(funcionario.getDocumentos(), registroColaborador.getAnexos());
    var dadosBancarios = dadosBancariosMapper.syncBancarios(funcionario.getDadosBancarios(), registroColaborador.getDadosBancarios());

    var tiposRelacionamento = funcionarioRules.getTipoRelacionamentoAtual(funcionario);
    dadosContratuaisMapper.toUpdateRelacionamento(tiposRelacionamento, dadosContratuais);


    var contrato = tiposRelacionamento.getContrVinculoId();
    contratoMapper.toUpdateEntity(contrato, dadosContratuais);

    var mobilidade = tiposRelacionamento.getMobId();
    mobilidadeMapper.toUpdateEntity(mobilidade, dadosContratuais);

    var carreira = tiposRelacionamento.getCarreiraId();
    carreiraMapper.toUpdateEntity(carreira, dadosContratuais);

    var regime = tiposRelacionamento.getRegimeId();
    regimeTrabalhoMapper.toUpdateEntity(regime, dadosContratuais);


    var definicoesRemuneracoes =
        definicaoRemuneracaoMapper.syncRemuneracoes(funcionario.getDefinicoesRenumeracoes(), dadosContratuais.getSubsidios());
    var definicoesPagamentos =
        defPagamentoMapper.syncPagamentos(funcionario.getDefinicoesPagamentos(), dadosContratuais.getEncargosDescontos());


    funcionario.setContactos(contactos);
    funcionario.setFamiliares(familiares);
    funcionario.setDocumentos(documentos);
    funcionario.setDadosBancarios(dadosBancarios);
    funcionario.setDefinicoesRenumeracoes(definicoesRemuneracoes);
    funcionario.setDefinicoesPagamentos(definicoesPagamentos);
    funcionario.setHabilitacoesLiterarias(habilitacoesLiterarias);
    funcionario.setFormacoesFeitas(formacoesFeitas);
    funcionario.setExperienciasProfissionais(experienciasProfissionais);

    if(registroColaborador.getValidar()!=null){
      var estado = registroColaborador.getValidar().equals(EstadoValidacao.SIM) ? Estado.A : Estado.I;
      if(estado.equals(Estado.A)){
        OrdemServicoEntity ordemServicoEntity = new OrdemServicoEntity();
        ordemServicoEntity.setFunId(funcionario);
        ordemServicoEntity.setTiprelId(tiposRelacionamento);
        ordemServicoEntity.setReferente(Referencia.REGISTO_COLABORADOR.name());
        ordemServicoEntity.setDescricao("Registro de colaborador");
        ordemServicoEntity.setNuOrdem("1"); // todo fix later
        ordemServicoEntity.setEstado(Estado.A);
        funcionario.getOrdemServicos().add(ordemServicoEntity);

      }
      mudaEstado(funcionario, estado);
    }

    funcionarioEntityRepository.save(funcionario);

    return java.util.Map.of(
        "id", funcionario.getId(),
        "uuid", funcionario.getUuid()
    );

  }

  private void mudaEstado(FuncionarioEntity funcionarioEntity, Estado estado) {
    if (funcionarioEntity == null) return;
    funcionarioEntity.setEstado(estado);
    funcionarioEntity.setEstadoValidacao(estado != null ? estado.name() : null);

    var documentoPessoal = funcionarioEntity.getDocumentoPessoal();
    if (documentoPessoal != null) documentoPessoal.setEstado(estado);

    var endereco = funcionarioEntity.getEndereco();
    if (endereco != null) endereco.setEstado(estado);

    var contactos = funcionarioEntity.getContactos();
    if (contactos != null) contactos.forEach(c -> { if (c != null) c.setEstado(estado); });

    var familiares = funcionarioEntity.getFamiliares();
    if (familiares != null) familiares.forEach(f -> { if (f != null) f.setEstado(estado); });

    var documentos = funcionarioEntity.getDocumentos();
    if (documentos != null) documentos.forEach(d -> { if (d != null) d.setEstado(estado); });

    var bancarios = funcionarioEntity.getDadosBancarios();
    if (bancarios != null) bancarios.forEach(b -> { if (b != null) b.setEstado(estado); });

    var habilitacoes = funcionarioEntity.getHabilitacoesLiterarias();
    if (habilitacoes != null) habilitacoes.forEach(h -> { if (h != null) h.setEstado(estado); });

    var formacoes = funcionarioEntity.getFormacoesFeitas();
    if (formacoes != null) formacoes.forEach(f -> { if (f != null) f.setEstado(estado); });

    var experiencias = funcionarioEntity.getExperienciasProfissionais();
    if (experiencias != null) experiencias.forEach(e -> { if (e != null) e.setEstado(estado); });

    var remuneracoes = funcionarioEntity.getDefinicoesRenumeracoes();
    if (remuneracoes != null) remuneracoes.forEach(r -> { if (r != null) r.setEstado(estado); });

    var pagamentos = funcionarioEntity.getDefinicoesPagamentos();
    if (pagamentos != null) pagamentos.forEach(p -> { if (p != null) p.setEstado(estado); });

    var tr = funcionarioRules.getTipoRelacionamentoAtual(funcionarioEntity);
    if (tr != null) {
      tr.setEstado(estado);

      var contrato = tr.getContrVinculoId();
      if (contrato != null) {
        contrato.setEstado(estado);
        contrato.getSituacoesLaborais().stream()
            .filter(o -> o.getEstado() == Estado.P)
            .findFirst().ifPresent(situacaoLaboralEntity -> situacaoLaboralEntity.setEstado(estado));
      }

      var mob = tr.getMobId();
      if (mob != null) mob.setEstado(estado);

      var carreira = tr.getCarreiraId();
      if (carreira != null) carreira.setEstado(estado);

      var regime = tr.getRegimeId();
      if (regime != null) regime.setEstado(estado);
    }


    funcionarioEntity.getValidacoes().stream()
        .filter(v -> v.getEstado() == Estado.P)
        .filter(v -> Referencia.REGISTO_COLABORADOR.name().equals(v.getReferenciaName()) && TipoAcao.INSERT.name().equals(v.getTipoAccao()))
        .findFirst()
        .ifPresent(v -> v.setEstado(estado));


  }


  //TODO 1.4-Caso for alterado o valor do salário ou data inicio de função ou data fim de função,
  // deve fazer atualização na tabela RH_T_DEF_REMUNERACAO  onde o TM_ID=GET_MOVIMENTO_SALL e TIPREL_ID=ID de RH_T_TIPOS_RELACIONAMENTO
  //•	VALOR = novo valor de salario do formulario
  //•	DATA_INICIO = Data inicio de função
  //•	DATA_FIM =  Data fim de função

  //TODO mudar estado de RH_T_REMU_TIPREL e RH_T_PAG_TIPREL

}
