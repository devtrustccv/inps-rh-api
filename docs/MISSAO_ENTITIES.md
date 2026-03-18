# Missões - Entidades e Repositórios

## MissaoServicoEntity

Arquivos:
- [MissaoServicoEntity.java](file:///c:/Users/ivanick.santos/Nick-personal/personal-workspace/projects/RH_INPS_SERVICE/src/main/java/cv/inps/rh/shared/infrastructure/persistence/entity/MissaoServicoEntity.java)
- [MissaoServicoEntityRepository.java](file:///c:/Users/ivanick.santos/Nick-personal/personal-workspace/projects/RH_INPS_SERVICE/src/main/java/cv/inps/rh/shared/infrastructure/persistence/repository/MissaoServicoEntityRepository.java)

```java
package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_MISSAO_SERVICO")
public class MissaoServicoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_missao_servico")
    @SequenceGenerator(name = "seq_missao_servico", sequenceName = "SEQ_MISSAO_SERVICO", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @NotNull(message = "nrMissao is mandatory")
    @Column(name = "nr_missao", nullable = false)
    private Long nrMissao;

    @NotNull(message = "paisDestinoId is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pais_destino_id", referencedColumnName = "id", nullable = false)
    private GeografiaEntity paisDestinoId;

    @Column(name = "flg_destino")
    private Integer flgDestino;

    @Column(name = "descricao_destino", length = 200)
    private String descricaoDestino;

    @NotNull(message = "dataInicio is mandatory")
    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @NotNull(message = "nrDias is mandatory")
    @Column(name = "nr_dias", nullable = false)
    private Integer nrDias;

    @NotBlank(message = "autorizadoPor is mandatory")
    @Column(name = "autorizado_por", length = 200, nullable = false)
    private String autorizadoPor;

    @NotNull(message = "dataAutorizacao is mandatory")
    @Column(name = "data_autorizacao", nullable = false)
    private LocalDate dataAutorizacao;

    @NotBlank(message = "etapa is mandatory")
    @Column(name = "etapa", length = 50, nullable = false)
    private String etapa;

    @NotNull(message = "estado is mandatory")
    @Column(name = "estado", length = 1, nullable = false)
    private String estado;

    @Column(name = "motivo_cancelamento", length = 500)
    private String motivoCancelamento;

    @NotNull(message = "uuid is mandatory")
    @Column(name = "uuid", nullable = false, length = 100)
    private UUID uuid;
}
```

```java
package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoServicoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MissaoServicoEntityRepository extends
    JpaRepository<MissaoServicoEntity, Long>,
    JpaSpecificationExecutor<MissaoServicoEntity> {

  Optional<MissaoServicoEntity> findByUuid(UUID uuid);

  default MissaoServicoEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "MissaoServicoEntity not found for id: " + uuid));
  }
}
```

## MissaoPrestadorEntity

Arquivos:
- [MissaoPrestadorEntity.java](file:///c:/Users/ivanick.santos/Nick-personal/personal-workspace/projects/RH_INPS_SERVICE/src/main/java/cv/inps/rh/shared/infrastructure/persistence/entity/MissaoPrestadorEntity.java)
- [MissaoPrestadorEntityRepository.java](file:///c:/Users/ivanick.santos/Nick-personal/personal-workspace/projects/RH_INPS_SERVICE/src/main/java/cv/inps/rh/shared/infrastructure/persistence/repository/MissaoPrestadorEntityRepository.java)

```java
package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.application.constants.Estado;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_MISSAO_PRESTADOR")
public class MissaoPrestadorEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_missao_prestador")
    @SequenceGenerator(name = "seq_missao_prestador", sequenceName = "SEQ_MISSAO_PRESTADOR", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @NotNull(message = "entId is mandatory")
    @Column(name = "ent_id", nullable = false)
    private Long entId;

    @NotBlank(message = "nome is mandatory")
    @Column(name = "nome", length = 200, nullable = false)
    private String nome;

    @Email
    @NotBlank(message = "email is mandatory")
    @Column(name = "email", length = 100, nullable = false)
    private String email;

    @NotNull(message = "missaoServId is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missao_serv_id", referencedColumnName = "id", nullable = false)
    private MissaoServicoEntity missaoServId;


    @Column(name = "estado", length = 1, nullable = false)
    private String estado;

    @NotNull(message = "uuid is mandatory")
    @Column(name = "uuid", nullable = false, length = 100)
    private UUID uuid;
}
```

```java
package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoPrestadorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MissaoPrestadorEntityRepository extends
    JpaRepository<MissaoPrestadorEntity, Long>,
    JpaSpecificationExecutor<MissaoPrestadorEntity> {

  Optional<MissaoPrestadorEntity> findByUuid(UUID uuid);

  default MissaoPrestadorEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "MissaoPrestadorEntity not found for id: " + uuid));
  }
}
```

## MissaoColaboradorEntity

Arquivos:
- [MissaoColaboradorEntity.java](file:///c:/Users/ivanick.santos/Nick-personal/personal-workspace/projects/RH_INPS_SERVICE/src/main/java/cv/inps/rh/shared/infrastructure/persistence/entity/MissaoColaboradorEntity.java)
- [MissaoColaboradorEntityRepository.java](file:///c:/Users/ivanick.santos/Nick-personal/personal-workspace/projects/RH_INPS_SERVICE/src/main/java/cv/inps/rh/shared/infrastructure/persistence/repository/MissaoColaboradorEntityRepository.java)

```java
package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_MISSAO_COLABORADOR")
public class MissaoColaboradorEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_missao_colaborador")
    @SequenceGenerator(name = "seq_missao_colaborador", sequenceName = "SEQ_MISSAO_COLABORADOR", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fun_id", referencedColumnName = "id")
    private FuncionarioEntity funId;

    @Column(name = "num_documento")
    private Long numDocumento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missao_serv_id", referencedColumnName = "id")
    private MissaoServicoEntity missaoServId;

    @Column(name = "estado", length = 1)
    private String estado;

    @Column(name = "uuid", length = 100)
    private UUID uuid;
}
```

```java
package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoColaboradorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MissaoColaboradorEntityRepository extends
    JpaRepository<MissaoColaboradorEntity, Long>,
    JpaSpecificationExecutor<MissaoColaboradorEntity> {

  Optional<MissaoColaboradorEntity> findByUuid(UUID uuid);

  default MissaoColaboradorEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "MissaoColaboradorEntity not found for id: " + uuid));
  }
}
```

## MissaoRequisicaoEntity

Arquivos:
- [MissaoRequisicaoEntity.java](file:///c:/Users/ivanick.santos/Nick-personal/personal-workspace/projects/RH_INPS_SERVICE/src/main/java/cv/inps/rh/shared/infrastructure/persistence/entity/MissaoRequisicaoEntity.java)
- [MissaoRequisicaoEntityRepository.java](file:///c:/Users/ivanick.santos/Nick-personal/personal-workspace/projects/RH_INPS_SERVICE/src/main/java/cv/inps/rh/shared/infrastructure/persistence/repository/MissaoRequisicaoEntityRepository.java)

```java
package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_MISSAO_REQUISICAO")
public class MissaoRequisicaoEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_missao_requisicao")
    @SequenceGenerator(name = "seq_missao_requisicao", sequenceName = "SEQ_MISSAO_REQUISICAO", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @NotNull(message = "missaoPrestId is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missao_prest_id", referencedColumnName = "id", nullable = false)
    private MissaoPrestadorEntity missaoPrestId;

    @NotNull(message = "missaoColabId is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missao_colab_id", referencedColumnName = "id", nullable = false)
    private MissaoColaboradorEntity missaoColabId;

    @NotNull(message = "estado is mandatory")
    @Column(name = "estado", length = 1, nullable = false)
    private String estado;

    @Column(name = "uuid", length = 100)
    private UUID uuid;
}
```

```java
package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoRequisicaoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MissaoRequisicaoEntityRepository extends
    JpaRepository<MissaoRequisicaoEntity, Long>,
    JpaSpecificationExecutor<MissaoRequisicaoEntity> {

  Optional<MissaoRequisicaoEntity> findByUuid(UUID uuid);

  default MissaoRequisicaoEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "MissaoRequisicaoEntity not found for id: " + uuid));
  }
}
```

## MissaoLogisticaEntity

Arquivos:
- [MissaoLogisticaEntity.java](file:///c:/Users/ivanick.santos/Nick-personal/personal-workspace/projects/RH_INPS_SERVICE/src/main/java/cv/inps/rh/shared/infrastructure/persistence/entity/MissaoLogisticaEntity.java)
- [MissaoLogisticaEntityRepository.java](file:///c:/Users/ivanick.santos/Nick-personal/personal-workspace/projects/RH_INPS_SERVICE/src/main/java/cv/inps/rh/shared/infrastructure/persistence/repository/MissaoLogisticaEntityRepository.java)

```java
package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import cv.inps.rh.shared.config.AuditEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_MISSAO_LOGISTICA")
public class MissaoLogisticaEntity extends AuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_missao_logistica")
    @SequenceGenerator(name = "seq_missao_logistica", sequenceName = "SEQ_MISSAO_LOGISTICA", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @NotNull(message = "prestadorServId is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_serv_id", referencedColumnName = "id", nullable = false)
    private MissaoPrestadorEntity prestadorServId;

    @Column(name = "nome_seguradora", length = 200)
    private String nomeSeguradora;

    @Column(name = "ent_id")
    private Long entId;

    @Column(name = "valor_total")
    private BigDecimal valorTotal;

    @Column(name = "referencia", length = 100)
    private String referencia;

    @Column(name = "moeda", length = 50)
    private String moeda;

    @Column(name = "lugar_hospedagem", length = 200)
    private String lugarHospedagem;

    @Column(name = "flg_alimentacao", length = 3)
    private String flgAlimentacao;

    @Column(name = "valor_diario")
    private BigDecimal valorDiario;

    @Column(name = "data_inicio")
    private LocalDate dataInicio;

    @Column(name = "data_fim")
    private LocalDate dataFim;

    @Column(name = "nr_dias")
    private Integer nrDias;

    @NotNull(message = "missaoServId is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missao_serv_id", referencedColumnName = "id", nullable = false)
    private MissaoServicoEntity missaoServId;

    @Column(name = "flg_alojamento", length = 3)
    private String flgAlojamento;

    @Column(name = "cab_id")
    private Long cabId;

    @Column(name = "estado_cabimento", length = 100)
    private String estadoCabimento;

    @NotNull(message = "estado is mandatory")
    @Column(name = "estado", length = 1, nullable = false)
    private String estado;

    @NotNull(message = "uuid is mandatory")
    @Column(name = "uuid", nullable = false, length = 100)
    private UUID uuid;
}
```

```java
package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MissaoLogisticaEntityRepository extends
    JpaRepository<MissaoLogisticaEntity, Long>,
    JpaSpecificationExecutor<MissaoLogisticaEntity> {

  Optional<MissaoLogisticaEntity> findByUuid(UUID uuid);

  default MissaoLogisticaEntity findByUuidOrThrow(UUID uuid) {
    return findByUuid(uuid)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "MissaoLogisticaEntity not found for id: " + uuid));
  }
}
```

## MissaoLogisticaDetEntity

Arquivos:
- [MissaoLogisticaDetEntity.java](file:///c:/Users/ivanick.santos/Nick-personal/personal-workspace/projects/RH_INPS_SERVICE/src/main/java/cv/inps/rh/shared/infrastructure/persistence/entity/MissaoLogisticaDetEntity.java)
- [MissaoLogisticaDetEntityRepository.java](file:///c:/Users/ivanick.santos/Nick-personal/personal-workspace/projects/RH_INPS_SERVICE/src/main/java/cv/inps/rh/shared/infrastructure/persistence/repository/MissaoLogisticaDetEntityRepository.java)

```java
package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "RH_T_MISSAO_LOGISTICA_DET")
public class MissaoLogisticaDetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_missao_logistica_det")
    @SequenceGenerator(name = "seq_missao_logistica_det", sequenceName = "SEQ_MISSAO_LOGISTICA_DET", allocationSize = 1)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @NotNull(message = "missaoLogistId is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missao_logist_id", referencedColumnName = "id", nullable = false)
    private MissaoLogisticaEntity missaoLogistId;

    @NotNull(message = "missaoColabId is mandatory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missao_colab_id", referencedColumnName = "id", nullable = false)
    private MissaoColaboradorEntity missaoColabId;

    @NotNull(message = "estado is mandatory")
    @Column(name = "estado", length = 3, nullable = false)
    private String estado;
}
```

```java
package cv.inps.rh.shared.infrastructure.persistence.repository;

import cv.inps.rh.shared.domain.exceptions.IgrpResponseStatusException;
import cv.inps.rh.shared.infrastructure.persistence.entity.MissaoLogisticaDetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

@Repository
public interface MissaoLogisticaDetEntityRepository extends
    JpaRepository<MissaoLogisticaDetEntity, Long>,
    JpaSpecificationExecutor<MissaoLogisticaDetEntity> {

  default MissaoLogisticaDetEntity findByIdOrThrow(Long id) {
    return findById(id)
        .orElseThrow(() -> IgrpResponseStatusException.of(HttpStatus.NOT_FOUND, "MissaoLogisticaDetEntity not found for id: " + id));
  }
}
```

