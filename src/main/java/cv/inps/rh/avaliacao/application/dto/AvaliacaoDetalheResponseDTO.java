package cv.inps.rh.avaliacao.application.dto;

import cv.igrp.framework.stereotype.IgrpDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@IgrpDTO
public class AvaliacaoDetalheResponseDTO {

  private Long id;
  private String uuid;
  private Long funId;
  private String nomeColaborador;
  private Integer ano;
  private String semestre;
  private Long institId;
  private Long seccaoId;
  private Long cargoId;
  private Long carrPccsId;
  private String estado;
  private BigDecimal avaliacaoFinal;
  private BigDecimal pesoComportamentais;
  private BigDecimal pesoTecnica;
  private BigDecimal avaliacaoObjectivo;
  private BigDecimal avaliacaoCompetencia;
  private BigDecimal avaliacaoAtitudePess;
  private String avaliacaoQualitativa;
  private String observacaoGeral;
  private String descricaoPlano;
  private LocalDate dataInicioEntrevista;
  private String horaInicioEntrevista;
  private String horaFimEntrevista;
  private String parecerColaborador;
  private String justificacaoMotivo;
  private String obsComissaoExec;
}

