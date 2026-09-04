package cv.inps.rh.funcionario.application.service;

import cv.inps.rh.funcionario.application.commands.ValidaDadosPessoaisCommand;
import cv.inps.rh.funcionario.application.rules.ColaboradorValidationRules;
import cv.inps.rh.funcionario.application.rules.FuncionarioRules;
import cv.inps.rh.funcionario.infrastructure.mappers.ContactoMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.DadosContratuaisMapper;
import cv.inps.rh.funcionario.infrastructure.mappers.FuncionarioMapper;
import cv.inps.rh.shared.application.dto.SuccessResponseDTO;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import cv.inps.rh.shared.infrastructure.persistence.repository.ValidacaoEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ValidarDadosPessoaisService {

  //In this service we can validate or update the personal data of a funcionario for further validation

  private final FuncionarioEntityRepository funcionarioEntityRepository;
  private final FuncionarioMapper funcionarioMapper;
  private final FuncionarioRules funcionarioRules;
  private final DadosContratuaisMapper contratuaisEntityMapper;
  private final ValidacaoEntityRepository validacaoEntityRepository;
  private final ContactoMapper contactoMapper;
  private final ColaboradorValidationRules colaboradorValidationRules;

  @Transactional
  public SuccessResponseDTO executar(ValidaDadosPessoaisCommand command) {

    var dto = command.getValidacaodadospessoais();
    var dadosPessoaisReqDTO = dto.getDadosPessoais();
    //var estadoValidacao = dto.getValidar();

    var funcionarioPublicId = IdentificadorUnico.from(command.getIdFuncionario()).valor();
    var funcionario = funcionarioEntityRepository.findByUuidOrThrow(funcionarioPublicId);
    funcionarioRules.garantirEditavel(funcionario.getEstado());

    colaboradorValidationRules.validarDadosPessoais(dadosPessoaisReqDTO, funcionario.getUuid());

    var alertas = funcionarioRules.validarContactosDuplicados(
        dadosPessoaisReqDTO != null ? dadosPessoaisReqDTO.getContactos() : null,
        funcionario.getUuid()
    );

    var contactos = contactoMapper.syncContactos(
        funcionario.getContactos(),
        dadosPessoaisReqDTO != null ? dadosPessoaisReqDTO.getContactos() : null,
        funcionario
    );
    funcionario.setContactos(contactos);
    funcionario = funcionarioMapper.toUpdateEntity(funcionario, dadosPessoaisReqDTO);

    // =====================================================
    // 3) Se já existe pendente → só mudar estado
    // =====================================================
   /* if (temPendentes) {

      var novoEstado = (estadoValidacao == EstadoValidacao.SIM)
          ? Estado.A
          : Estado.I;

      mudarEstado(funcionario, novoEstado);

      funcionarioEntityRepository.save(funcionario);
      return dto;
    }*/


    // 4) Se NÃO existe pendente → criar o registo
    //funcionario.setEstado(Estado.A);
    //funcionario.setEstadoValidacao(Estado.P.name()); // novo pendente

   // var tipoRel = funcionarioRules.getTipoRelacionamentoAtual(funcionario.getUuid());

   /* var validacao = contratuaisEntityMapper
        .toValidacaoInsert(TipoAcao.UPDATE.name(), Referencia.DADOS_PESSOAIS.name(), Estado.P);

    validacao.setFunId(funcionario);
    validacao.setTiprelId(tipoRel);

    funcionario.getValidacoes().add(validacao);*/

    funcionarioEntityRepository.saveAndFlush(funcionario);

   /* validacaoEntityRepository
        .findByFunId_UuidAndEstadoAndTipoAccaoAndReferenciaName(
            funcionario.getUuid(),
            Estado.P,
            TipoAcao.UPDATE.name(),
            Referencia.DADOS_PESSOAIS.name()
        )
        .ifPresent(v -> {
          v.setReferenciaId(saved.getId());
          validacaoEntityRepository.save(v);
        });*/

    return new SuccessResponseDTO(true, funcionario.getUuid().toString(), "Dados pessoais actualizados.", alertas);
  }


  /*private void mudarEstado(FuncionarioEntity funcionarioEntity, Estado estado) {
    if (funcionarioEntity == null) return;
    funcionarioEntity.setEstadoValidacao(estado != null ? estado.name() : null);

    var documentoPessoal = funcionarioEntity.getDocumentoPessoal();
    if (documentoPessoal != null) documentoPessoal.setEstado(estado);

    var endereco = funcionarioEntity.getEndereco();
    if (endereco != null) endereco.setEstado(estado);

    var contactos = funcionarioEntity.getContactos();
    if (contactos != null) {
      contactos.stream()
          .filter(c -> c != null && c.getEstado() == Estado.P)
          .forEach(c -> c.setEstado(estado));
    }

    funcionarioRules.getValidacaoPendente(funcionarioEntity.getUuid(), TipoAcao.UPDATE, Referencia.DADOS_PESSOAIS)
        .ifPresent(v -> v.setEstado(estado));

  }*/


}
