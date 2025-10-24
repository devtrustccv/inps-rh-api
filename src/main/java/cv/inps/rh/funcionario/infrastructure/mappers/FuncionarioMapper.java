package cv.inps.rh.funcionario.infrastructure.mappers;

import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDTO;
import cv.inps.rh.funcionario.application.dto.FuncionarioResponseDetailsDTO;
import cv.inps.rh.funcionario.domain.models.Contacto;
import cv.inps.rh.funcionario.domain.models.Endereco;
import cv.inps.rh.funcionario.domain.models.Familiar;
import cv.inps.rh.funcionario.domain.models.Funcionario;
import cv.inps.rh.shared.domain.models.IdentificadorUnico;
import cv.inps.rh.shared.infrastructure.mappers.EstadoMapper;
import cv.inps.rh.shared.infrastructure.mappers.GeografiaMapper;
import cv.inps.rh.shared.infrastructure.mappers.TipoDocumentoMapper;
import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FuncionarioMapper {

  private final TipoDocumentoMapper tipoDocumentoMapper;
  private final GeografiaMapper geografiaMapper;
  private final EstadoMapper estadoMapper;
  private final ContactoMapper contactoMapper;
  private final EnderecoMapper enderecoMapper;
  private final FamiliarMapper familiarMapper;


  /** Converts JPA entity to domain Funcionario */
  public Funcionario toDomain(FuncionarioEntity entity) {
    if (entity == null) return null;

    List<Contacto> contactos = entity.getContactos() != null
        ? entity.getContactos().stream()
        .map(contactoMapper::toDomain)
        .collect(Collectors.toCollection(ArrayList::new))
        : new ArrayList<>();

    List<Endereco> enderecos = entity.getEnderecos() != null
        ? entity.getEnderecos().stream()
        .map(enderecoMapper::toDomain)
        .collect(Collectors.toCollection(ArrayList::new))
        : new ArrayList<>();

    List<Familiar> familiares = entity.getFamiliares()!=null
        ? entity.getFamiliares().stream().map(familiarMapper::toDomain)
        .collect(Collectors.toCollection(ArrayList::new))
        : new ArrayList<>();

    return Funcionario.rebuild(
        entity.getId(),
        entity.getUuid(),
        entity.getTipoDocumentoId() != null ? tipoDocumentoMapper.toDomain(entity.getTipoDocumentoId()) : null,
        entity.getNumDocumento(),
        entity.getNome(),
        entity.getFotografia(),
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
        estadoMapper.fromString(entity.getEstadoValidacao()),
        contactos,
        enderecos,
        familiares
    );
  }

  public Funcionario toDomainLigth(FuncionarioEntity entity) {
    if (entity == null) return null;

    return Funcionario.rebuildLight(
        entity.getId(),
        entity.getUuid(),
        entity.getTipoDocumentoId() != null ? tipoDocumentoMapper.toDomain(entity.getTipoDocumentoId()) : null,
        entity.getNumDocumento(),
        entity.getNome(),
        entity.getFotografia(),
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
    entity.setFotografia(funcionario.getFotografia());
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


    if (funcionario.getContactos() != null) {
      var contactosEntities = funcionario.getContactos().stream()
          .map(contactoMapper::toEntity)
          .collect(Collectors.toList());

       contactosEntities.forEach(c -> c.setFunId(entity)); // garante o relacionamento
      entity.setContactos(contactosEntities);
    }

    if(funcionario.getEnderecos() != null){
      var enderecosEntities = funcionario.getEnderecos().stream()
          .map(enderecoMapper::toEntity)
          .collect(Collectors.toList());

      enderecosEntities.forEach(e -> e.setFunId(entity));
      entity.setEnderecos(enderecosEntities);
    }

    if(funcionario.getFamiliares() != null){
      var familiaresEntities = funcionario.getFamiliares().stream()
          .map(familiarMapper::toEntity)
          .collect(Collectors.toList());

      familiaresEntities.forEach(f -> f.setFunId(entity));
      entity.setFamiliares(familiaresEntities);
    }

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
    dto.setUrlFoto(funcionario.getFotografia());
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


  public FuncionarioResponseDetailsDTO toResponseDetailsDTO(Funcionario funcionario) {
    if (funcionario == null) return null;

    var dto = new FuncionarioResponseDetailsDTO();
    dto.setId(funcionario.getId() != null ? funcionario.getId() : null);
    dto.setUuid(funcionario.getUuid() != null ? funcionario.getUuid().toString() : null);
    dto.setTipoDocumentoId(funcionario.getTipoDocumento() != null ? funcionario.getTipoDocumento().getId().intValue() : null);
    dto.setTipoDocumentoDesc(funcionario.getTipoDocumento() != null ? funcionario.getTipoDocumento().getNome() : null);
    dto.setNumDocumento(funcionario.getNumeroDocumento());
    dto.setNome(funcionario.getNomeCompleto());
    dto.setUrlFoto(funcionario.getFotografia());
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


    // ---- Contactos ----
    if (funcionario.getContactos() != null && !funcionario.getContactos().isEmpty()) {
      dto.setContactos(contactoMapper.toDTOList(funcionario.getContactos()));
    }

    // ---- Endereços ----
    if (funcionario.getEnderecos() != null && !funcionario.getEnderecos().isEmpty()) {
      dto.setEnderecos(enderecoMapper.toDTOList(funcionario.getEnderecos()));
    }


    if(funcionario.getFamiliares() != null && !funcionario.getFamiliares().isEmpty()){
      dto.setFamiliares(familiarMapper.toResponseDTOList(funcionario.getFamiliares()));
    }

    return dto;
  }


}
