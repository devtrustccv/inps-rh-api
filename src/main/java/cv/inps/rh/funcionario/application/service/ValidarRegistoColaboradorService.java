package cv.inps.rh.funcionario.application.service;

import com.github.f4b6a3.uuid.UuidCreator;
import cv.inps.rh.funcionario.application.commands.ValidarRegistoColaboradorCommand;
import cv.inps.rh.funcionario.infrastructure.mappers.FuncionarioMapper;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.application.constants.EstadoValidacao;
import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.persistence.repository.FuncionarioEntityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import cv.inps.rh.funcionario.application.dto.FuncionarioRequestDTO;
import cv.inps.rh.funcionario.application.dto.DadosPessoaisReqDTO;
import cv.inps.rh.funcionario.application.dto.DadosContratuaisReqDTO;
import cv.inps.rh.shared.infrastructure.persistence.entity.*;

  @Service
  @RequiredArgsConstructor
  public class ValidarRegistoColaboradorService {

    private final FuncionarioEntityRepository funcionarioEntityRepository;
    private final FuncionarioMapper funcionarioMapper;
    @PersistenceContext
    private EntityManager entityManager;

  public Map<String,?> validarRegistoColaborador(ValidarRegistoColaboradorCommand command) {

    var registroColaborador = command.getFuncionariorequest();
    var funcionarioPublicId = IdentificadorUnico.from(command.getId()).getValor();

    Estado novoEstado = command.getFuncionariorequest().getValidar() == EstadoValidacao.SIM ? Estado.A : Estado.I;

    var funcionario = funcionarioEntityRepository.findByUuid(funcionarioPublicId)
        .orElseThrow(() -> IgrpResponseStatusException.notFound("funcionario nao encontrado com id"+command.getId()));

    var dadosPessoais = registroColaborador.getDadosPessoais();

   return null;

  }
}
