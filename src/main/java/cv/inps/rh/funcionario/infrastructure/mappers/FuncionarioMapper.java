package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.domain.models.Funcionario;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.mappers.EstadoMapper;
import cv.inps.rh.shared.infrastructure.mappers.GeografiaMapper;
import cv.inps.rh.shared.infrastructure.mappers.TipoDocumentoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FuncionarioMapper {

  private final TipoDocumentoMapper tipoDocumentoMapper;
  private final GeografiaMapper geografiaMapper;
  private final EstadoMapper estadoMapper;


  /** Converts JPA entity to domain Funcionario */
  public Funcionario toDomain(FuncionarioEntity entity) {
    if (entity == null) return null;

    return Funcionario.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getTipoDocumentoId() != null ? tipoDocumentoMapper.toDomain(entity.getTipoDocumentoId()) : null,
        entity.getNumDocumento(),
        entity.getNome(),
        entity.getUrlFotografia(),
        entity.getDataNascimento(),
        entity.getSexo(),
        entity.getNmMae(),
        entity.getNmPai(),
        entity.getEstadoCivil(),
        entity.getNacionalidade(),
        entity.getLocNascId() != null ? geografiaMapper.rebuild(entity.getLocNascId()) : null,
        entity.getNif(),
        entity.getNuSegInps(),
        entity.getEntId(),
        entity.getIdColaborador(),
        entity.getEstado(),
        estadoMapper.fromString(entity.getEstadoValidacao())
    );
  }

  /** Converts domain Funcionario to JPA entity */
  public FuncionarioEntity toEntity(Funcionario funcionario) {
    if (funcionario == null) return null;

    FuncionarioEntity entity = new FuncionarioEntity();
    entity.setId(funcionario.getId());
    entity.setUuid(funcionario.getUuid().getValor());
    entity.setTipoDocumentoId(funcionario.getTipoDocumento() != null ? tipoDocumentoMapper.toEntity(funcionario.getTipoDocumento()) : null);
    entity.setNumDocumento(funcionario.getNumeroDocumento());
    entity.setNome(funcionario.getNomeCompleto());
    entity.setUrlFotografia(funcionario.getUrlFotografia());
    entity.setDataNascimento(funcionario.getDataNascimento());
    entity.setSexo(funcionario.getSexo());
    entity.setNmMae(funcionario.getNomeMae());
    entity.setNmPai(funcionario.getNomePai());
    entity.setEstadoCivil(funcionario.getEstadoCivil());
    entity.setNacionalidade(funcionario.getNacionalidade());
    entity.setLocNascId(funcionario.getLocalNascimento() != null ? geografiaMapper.toEntity(funcionario.getLocalNascimento()) : null);
    entity.setNif(funcionario.getNumeroFiscal());
    entity.setNuSegInps(funcionario.getNumeroSegurancaSocial());
    entity.setEntId(funcionario.getEntidadeId());
    entity.setIdColaborador(funcionario.getColaboradorId());
    entity.setEstado(funcionario.getEstado());
    entity.setEstadoValidacao(funcionario.getEstadoValidacao().name());
    return entity;
  }
}
