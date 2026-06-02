package cv.inps.rh.shared.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Entity
@Immutable
@Table(name = "RH_V_MAPA_PESSOAL")
public class RhVMapaPessoalEntity {

    @Id
    @Column(name = "FUN_ID")
    private Long funId;

    @Column(name = "NOME")
    private String nome;

    @Column(name = "ESTADO", length = 1)
    private String estado;

    @Column(name = "GENERO", length = 1)
    private String genero;

    @Column(name = "DIRECAO")
    private String direcao;

    @Column(name = "SECCAO")
    private String seccao;

    @Column(name = "LOCAL")
    private String local;

    @Column(name = "CARREIRA")
    private String carreira;

    @Column(name = "DATA_NASCIMENTO")
    private LocalDate dataNascimento;

    @Column(name = "IDADE")
    private Integer idade;

    @Column(name = "DATA_ENTRADA")
    private LocalDate dataEntrada;

    @Column(name = "ANOS_INPS")
    private Integer anosInps;

    @Column(name = "CARGO")
    private String cargo;

    @Column(name = "ESCALAO_ACTUAL")
    private String escalaoActual;

    @Column(name = "SALARIO_ATUAL", precision = 15, scale = 2)
    private BigDecimal salarioAtual;

    @Column(name = "ESCALAO_CATEGORIA")
    private String escalaoCategoria;

    @Column(name = "SALARIO_CATEGORIA", precision = 15, scale = 2)
    private BigDecimal salarioCategoria;

    @Column(name = "SALARIO_FUNCAO", precision = 15, scale = 2)
    private BigDecimal salarioFuncao;

    @Column(name = "VINCULO_LABORAL")
    private String vinculoLaboral;

    @Column(name = "HABILITACAO_LITERARIA")
    private String habilitacaoLiteraria;

    @Column(name = "CURSO")
    private String curso;

    @Column(name = "EMAIL")
    private String email;
}
