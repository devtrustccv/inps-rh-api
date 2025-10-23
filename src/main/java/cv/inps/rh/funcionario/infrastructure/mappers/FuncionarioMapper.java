package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;
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
        entity.getLocNascId() != null ? geografiaMapper.toDomain(entity.getLocNascId()) : null,
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

  public FuncionarioResponseDTO toDTO(Funcionario funcionario) {
    if (funcionario == null) return null;

    var dto = new FuncionarioResponseDTO();
    dto.setId(funcionario.getId() != null ? funcionario.getId() : null);
    dto.setUuid(funcionario.getUuid() != null ? funcionario.getUuid().toString() : null);
    dto.setTipoDocumentoId(funcionario.getTipoDocumento() != null ? funcionario.getTipoDocumento().getId().intValue() : null);
    dto.setTipoDocumentoDesc(funcionario.getTipoDocumento() != null ? funcionario.getTipoDocumento().getNome() : null);
    dto.setNumDocumento(funcionario.getNumeroDocumento());
    dto.setNome(funcionario.getNomeCompleto());
    dto.setUrlFoto(funcionario.getUrlFotografia());
    dto.setDataNascimento(funcionario.getDataNascimento());
    dto.setGenero(funcionario.getSexo());
    dto.setNomeMae(funcionario.getNomeMae());
    dto.setNomePai(funcionario.getNomePai());
    dto.setEstadoCivil(funcionario.getEstadoCivil());
    dto.setNacionalidade(funcionario.getNacionalidade());
    dto.setNaturalidadeId(funcionario.getLocalNascimento() != null ? funcionario.getLocalNascimento().getId() : null);
    dto.setNaturalidadeDesc(funcionario.getLocalNascimento() != null ? funcionario.getLocalNascimento().getNome() : null);
    dto.setNif(funcionario.getNumeroFiscal() != null ? funcionario.getNumeroFiscal().toString() : null);
    dto.setNumSegurado(funcionario.getNumeroSegurancaSocial());
    return dto;
  }


}
