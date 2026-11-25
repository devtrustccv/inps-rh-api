-- =============================================================================
-- Schema diff (Hibernate scripts.action=update) — entities vs BD de dev
-- Gerado a 2026-11-25 (develop ab2bf9dc). SQL que o ddl-auto=update executaria; NAO APLICADO.
-- So mostra o que FALTA (tabelas, colunas, sequencias, FKs); tipos diferentes nao aparecem aqui
-- (ver schema_validate_dev_2026-11-25.sql). Tipos tal como o Hibernate os propoe (timestamp/255 char),
-- nao na convencao da casa.
--
-- Em falta de verdade: RH_T_EVOLUCAO_CARREIRA (DATA_ALTERACAO, USER_ALTERACAO_NAME),
-- RH_T_PAG_TIPREL, RH_T_REMUN_TIPREL, RH_T_VAL_EVOLUCAO_CARREIRA.ORDEM_SERVICO_ID, SEQ_DIRECAO.
-- As "add constraint ... foreign key" sao FKs que o Hibernate gostaria de ter: o validate nao as
-- verifica e nao partem nada (muitas apontam para SIPSGLOBAL).
-- =============================================================================

    alter table rh_t_evolucao_carreira 
       add user_alteracao_name varchar2(255 char);

    alter table rh_t_evolucao_carreira 
       add data_alteracao timestamp(6);

    create table rh_t_pag_tiprel (
        id number(19,0) not null,
        user_registo_name varchar2(255 char) not null,
        user_registo_id number(19,0) not null,
        data_registo timestamp(6) not null,
        user_alteracao_name varchar2(255 char),
        user_alteracao_id number(19,0),
        data_alteracao timestamp(6),
        estado varchar2(255 char) check (estado in ('I','A','E','P','C')),
        obs varchar2(4000 char),
        uuid varchar2(255 char),
        pag_id number(19,0) not null,
        tiprel_id number(19,0),
        primary key (id)
    );

    create table rh_t_remun_tiprel (
        id number(19,0) not null,
        user_registo_name varchar2(255 char) not null,
        user_registo_id number(19,0) not null,
        data_registo timestamp(6) not null,
        user_alteracao_name varchar2(255 char),
        user_alteracao_id number(19,0),
        data_alteracao timestamp(6),
        estado varchar2(255 char) not null check (estado in ('I','A','E','P','C')),
        obs varchar2(4000 char),
        uuid varchar2(255 char),
        rem_id number(19,0),
        tiprel_id number(19,0),
        primary key (id)
    );

    alter table rh_t_val_evolucao_carreira 
       add ordem_servico_id number(19,0);

    create sequence seq_direcao start with 1 increment by 1;

    alter table rh_equip_contr_acesso 
       add constraint FKis1y5bmy34ug8246lxtm37t17 
       foreign key (id_ups) 
       references sipsglobal.glb_t_ups;

    alter table rh_t_ausencia 
       add constraint FK1rw3xwgealns224fofh659n9r 
       foreign key (fun_id) 
       references rh_t_funcionarios;

    alter table rh_t_avd 
       add constraint FKleqvuba360w9xqlf0yq93d64e 
       foreign key (instit_id) 
       references rh_t_direcao;

    alter table rh_t_avd 
       add constraint FKr3aqnjfp9hksxw0hoeoxoxa7g 
       foreign key (seccao_id) 
       references rh_t_secao;

    alter table rh_t_dados_bancarios 
       add constraint FKns0skh8k9qjk1f8gsos0ta5qh 
       foreign key (rhb_id) 
       references rh_t_banco;

    alter table rh_t_def_pagamentos 
       add constraint FKkfq7upqdv4ltdyv89mv2f3fww 
       foreign key (rhb_id) 
       references rh_t_banco;

    alter table rh_t_documento 
       add constraint FK7tls3mjrh2e79d4wm1fqlvxji 
       foreign key (tp_documento_id) 
       references rh_t_tipos_documentos;

    alter table rh_t_documento_pessoal 
       add constraint FKdgg4yaa0fq31y6r5tgdo853mi 
       foreign key (loc_emiss_id) 
       references sipsglobal.glb_t_geografia;

    alter table rh_t_estabelecimento 
       add constraint FKam67trgm9wwt8jqissrme2j21 
       foreign key (pais_id) 
       references sipsglobal.glb_t_geografia;

    alter table rh_t_experiencia_prof 
       add constraint FK7s31els4rsudd0rx0dxjf081b 
       foreign key (pais_id) 
       references sipsglobal.glb_t_geografia;

    alter table rh_t_formacao_feitos 
       add constraint FKpi5vgaisyeci7iqpwv9d8catg 
       foreign key (pais_id) 
       references sipsglobal.glb_t_geografia;

    alter table rh_t_funcionarios 
       add constraint FK56831e5n9j9ugmy1oq6x6xov1 
       foreign key (nacion_id) 
       references sipsglobal.glb_t_geografia;

    alter table rh_t_missao_servico 
       add constraint FKortoh7f7p25n6ynqea26o6sl3 
       foreign key (concelho_id) 
       references sipsglobal.glb_t_geografia;

    alter table rh_t_missao_servico 
       add constraint FK1nxf9t1vf2lsdf949jiah2ja3 
       foreign key (ilha_id) 
       references sipsglobal.glb_t_geografia;

    alter table rh_t_mobilidade 
       add constraint FKhoeiu79hbpth8ucypnwtsjrop 
       foreign key (mob_id) 
       references rh_t_mobilidade;

    alter table rh_t_pag_tiprel 
       add constraint FKs23jfqto74aa4dlwbxpvmkiit 
       foreign key (pag_id) 
       references rh_t_def_pagamentos;

    alter table rh_t_pag_tiprel 
       add constraint FKhg1ap0pyf6ujl8xv5bc8myde2 
       foreign key (tiprel_id) 
       references rh_t_tipos_relacionamento;

    alter table rh_t_param_local_trab 
       add constraint FKegvle4iuxia78lsp4xxnqxuw7 
       foreign key (pais_id) 
       references sipsglobal.glb_t_geografia;

    alter table rh_t_param_manual_func 
       add constraint FKnabxdvu4epyqbeuago4ui1qmf 
       foreign key (instit_id) 
       references rh_t_direcao;

    alter table rh_t_param_manual_func 
       add constraint FKh1ib8g31tflb9x2m4ogy7r8rq 
       foreign key (seccao_id) 
       references rh_t_secao;

    alter table rh_t_param_objetivo 
       add constraint FKkn6rf7w13jqe91yoidc7v60rq 
       foreign key (instit_id) 
       references rh_t_direcao;

    alter table rh_t_param_objetivo 
       add constraint FKftosj0ts3nqfsvk9ilx6jq7gy 
       foreign key (seccao_id) 
       references rh_t_secao;

    alter table rh_t_param_prestador 
       add constraint FK60m9uuf8ienbctfjha3jcem5r 
       foreign key (ilha_id) 
       references sipsglobal.glb_t_geografia;

    alter table rh_t_proc_funcionarios 
       add constraint FKoeeasdfrtwapdlnymp00ilv6f 
       foreign key (rhb_id) 
       references rh_t_dados_bancarios;

    alter table rh_t_remun_tiprel 
       add constraint FKr64g7veh6bkyaj2eruulj9cyb 
       foreign key (rem_id) 
       references rh_t_def_remuneracoes;

    alter table rh_t_remun_tiprel 
       add constraint FKqkvondngp1sywlk6o0o2s1nd7 
       foreign key (tiprel_id) 
       references rh_t_tipos_relacionamento;

    alter table rh_t_responsavel 
       add constraint FKa2rjjqmow3dwgprwp9umbxmcp 
       foreign key (instit_id) 
       references rh_t_direcao;
