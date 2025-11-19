package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidarRegistoColaboradorCommand;
import cv.inps.rh.funcionario.infrastructure.mappers.*;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
  private final ContratuaisEntityMapper contratuaisEntityMapper;


  @PersistenceContext
  private EntityManager entityManager;

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

    var tiposRelacionamento = funcionarioMapper.getTipoRelacionamentoAtual(funcionario);
    contratuaisEntityMapper.toUpdateRelacionamento(tiposRelacionamento, registroColaborador.getDadosContratuais());


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


    funcionarioEntityRepository.save(funcionario);

    return java.util.Map.of(
        "id", funcionario.getId(),
        "uuid", funcionario.getUuid() != null ? funcionario.getUuid().toString() : null
    );

  }
}
