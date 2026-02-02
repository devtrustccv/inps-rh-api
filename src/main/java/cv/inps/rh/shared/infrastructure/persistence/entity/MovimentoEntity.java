/* THIS FILE WAS GENERATED AUTOMATICALLY BY iGRP STUDIO. */
/* DO NOT MODIFY IT BECAUSE IT COULD BE REWRITTEN AT ANY TIME */

package cv.inps.rh.shared.infrastructure.persistence.entity;

import cv.igrp.framework.stereotype.IgrpEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;


@Getter
@Setter
@IgrpEntity
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "VW_RH_MOVIMENTOS_PICAGEM")
public class MovimentoEntity  {

    @Id
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

  
    @Column(name="dt_movimento")
    private LocalDate dtMovimento;

  
    @Column(name="hora_movimento")
    private String horaMovimento;

  
    @Column(name="id_colaborador")
    private Long idColaborador;

  
    @Column(name="nome_colaborador")
    private String nomeColaborador;

  
    @Column(name="tp_movimento")
    private String tpMovimento;

  
    @Column(name="tp_movimento_desc")
    private String tpMovimentoDesc;

  
    @Column(name="verify_mode")
    private Long verifyMode;

  
    @Column(name="in_out_mode")
    private Long inOutMode;

  
    @Column(name="work_code")
    private short workCode;

  
    @Column(name="dt_registo")
    private LocalDate dtRegisto;

  
    @Column(name="usr_registo")
    private Long usrRegisto;

  
    @Column(name="data_hora")
    private String dataHora;

  
    @Column(name="processado")
    private Integer processado;

  
    @Column(name="tp_movimento_maquina")
    private String tpMovimentoMaquina;

  
    @Column(name="tp_movimento_maquina_desc")
    private String tpMovimentoMaquinaDesc;

  
    @Column(name="id_equip_contr_acesso")
    private Long idEquipContrAcesso;

  
    @Column(name="local_movimento")
    private String localMovimento;

  
}