package cv.inps.rh.funcionario.application.service.registodetalhe;

import cv.inps.rh.shared.infrastructure.persistence.entity.FuncionarioEntity;
import lombok.Getter;
import org.javers.core.metamodel.annotation.Id;
import org.javers.core.metamodel.annotation.TypeName;

import java.time.LocalDate;

/**
 * Snapshot só-leitura dos campos-núcleo do funcionário (RH_T_FUNCIONARIOS) para o "Detalhe de
 * alterações" do registo. Existe porque o {@code FuncionarioEntity} é <em>ShallowReference</em> no
 * JaVers (para a auditoria dos outros módulos não arrastar o grafo do funcionário) — logo os seus
 * escalares nunca são diffados. Auditamos este POJO dedicado em vez de mexer nessa config global.
 *
 * <p>Identidade = id do funcionário (as duas versões — baseline no CORRIGIR e diff no reenvio —
 * partilham-no, produzindo o diff). Campos guardados já LEGÍVEIS (a naturalidade guarda o NOME, não o
 * id), pelo que a grelha os mostra diretamente.
 */
@Getter
@TypeName("RegistoFuncionarioSnapshot")
public final class FuncionarioDadosPessoaisSnapshot {

  @Id
  private final Long id;
  private final String nome;
  private final Long nif;
  private final String nomeMae;
  private final String nomePai;
  private final LocalDate dataNascimento;
  private final String genero;
  private final String estadoCivil;
  private final String nacionalidade;
  private final String naturalidade;
  private final String localidade;
  private final String numSegurado;

  private FuncionarioDadosPessoaisSnapshot(FuncionarioEntity f) {
    this.id = f.getId();
    this.nome = f.getNome();
    this.nif = f.getNif();
    this.nomeMae = f.getNmMae();
    this.nomePai = f.getNmPai();
    this.dataNascimento = f.getDataNascimento();
    this.genero = f.getSexo();
    this.estadoCivil = f.getEstadoCivil();
    this.nacionalidade = f.getNacionalidade();
    this.naturalidade = f.getLocNascId() != null ? f.getLocNascId().getNome() : null;
    this.localidade = f.getLocalidade();
    this.numSegurado = f.getNuSegInps();
  }

  public static FuncionarioDadosPessoaisSnapshot of(FuncionarioEntity funcionario) {
    return funcionario == null ? null : new FuncionarioDadosPessoaisSnapshot(funcionario);
  }
}
