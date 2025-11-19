package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarRegistoColaboradorCommand;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
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



  @Transactional
  public Map<String, ?> validarRegistoColaborador(ValidarRegistoColaboradorCommand command) {

    var registroColaborador = command.getFuncionariorequest();
    var funcionarioPublicId = IdentificadorUnico.from(command.getId()).getValor();

    var funcionario = funcionarioEntityRepository.findByUuid(funcionarioPublicId)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("funcionario nao encontrado com id" + command.getId()));

    var dadosPessoaisReqDTO = registroColaborador.getDadosPessoais();
    funcionario = funcionarioMapper.toUpdateEntity(funcionario, dadosPessoaisReqDTO);

    var contactos = contactoMapper.syncContactos(funcionario.getContactos(), dadosPessoaisReqDTO != null ? dadosPessoaisReqDTO.getContactos() : null);

    var familiares = familiarMapper.syncFamiliares(funcionario.getFamiliares(), registroColaborador.getFamiliares());

    var da = registroColaborador.getDadosAcademicosProf();

    var habilitacoesLiterarias = habilitacaoLiterariaMapper.syncHabilitacoes(funcionario.getHabilitacoesLiterarias(), da.getHabilitacoesLiterarias());
    var formacoesFeitas = formacaoFeitaMapper.syncFormacoes(funcionario.getFormacoesFeitas(), da.getFormacoesFeitas());
    var experienciasProfissionais = experienciaProfissionalMapper.syncExperiencias(funcionario.getExperienciasProfissionais(), da.getExperienciasProfssionais());


    var documentos = documentoMapper.syncDocumentos(funcionario.getDocumentos(), registroColaborador.getAnexos());
    var dadosBancarios = dadosBancariosMapper.syncBancarios(funcionario.getDadosBancarios(), registroColaborador.getDadosBancarios());

    var tiposRelacionamento = dadosContratuaisMapper.getTipoRelacionamentoAtual(funcionario);
    dadosContratuaisMapper.toUpdateRelacionamento(tiposRelacionamento, registroColaborador.getDadosContratuais());


    var dc = registroColaborador.getDadosContratuais();

    var contrato = tiposRelacionamento.getContratoId();
    contratoMapper.toUpdateEntity(contrato, dc);

    var mobilidade = tiposRelacionamento.getMobId();
    mobilidadeMapper.toUpdateEntity(mobilidade, dc);


    var carreira = tiposRelacionamento.getCarreiraId();
    carreiraMapper.toUpdateEntity(carreira, dc);

    var regime = tiposRelacionamento.getRegimeId();
    regimeTrabalhoMapper.toUpdateEntity(regime, dc);


    var definicoesRemuneracoes = definicaoRemuneracaoMapper.syncRemuneracoes(funcionario.getDefinicoesRenumeracoes(), dc.getSubsidios());
    var definicoesPagamentos = defPagamentoMapper.syncPagamentos(funcionario.getDefinicoesPagamentos(), dc.getEncargosDescontos());


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
        ordemServicoEntity.setReferente("REGISTO_COLABORADOR");
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
        "uuid", funcionario.getUuid() != null ? funcionario.getUuid().toString() : null
    );

  }

  private void mudaEstado(FuncionarioEntity funcionarioEntity, Estado estado) {
    if (funcionarioEntity == null) return;
    funcionarioEntity.setEstado(estado);
    funcionarioEntity.setEstadoValidacao(estado != null ? estado.name() : null);

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

    var tr = dadosContratuaisMapper.getTipoRelacionamentoAtual(funcionarioEntity);
    if (tr != null) {
      tr.setEstado(estado);

      var contrato = tr.getContratoId();
      if (contrato != null) contrato.setEstado(estado);

      var mob = tr.getMobId();
      if (mob != null) mob.setEstado(estado);

      var carreira = tr.getCarreiraId();
      if (carreira != null) carreira.setEstado(estado);

      var regime = tr.getRegimeId();
      if (regime != null) regime.setEstado(estado);
    }

    /*funcionarioEntity.getValidacoes().stream()
        .filter(v -> "REGISTO_COLABORADOR".equals(v.getReferenciaName()) && "INSERT".equals(v.getTipoAccao()))
        .findFirst()
        .ifPresent(v -> v.setEstado(estado));*/

    funcionarioEntity.getValidacoes().stream()
        .filter(v -> v.getEstado() == Estado.P)
        .findFirst()
        .ifPresent(v -> v.setEstado(estado));


    funcionarioEntity.getSituacoesLaborais()
        .stream()
        .filter(o -> o.getEstado() == Estado.P)
        .findFirst().ifPresent(situacaoLaboralEntity -> situacaoLaboralEntity.setEstado(estado));


  }

}
