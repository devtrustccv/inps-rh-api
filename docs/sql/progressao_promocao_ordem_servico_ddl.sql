-- =============================================================================
-- Progressão / Promoção — Ordem de Serviço na validação (2026-11-20)
--
-- Spec PROGRESSÃO E PROMOÇÃO (Lista Confirmados > Anexar ordem de Serviço):
-- o anexar passa a fazer update em RH_T_VAL_EVOLUCAO_CARREIRA.ORDEM_SERVICO_ID.
--
-- POR APLICAR EM: desenvolvimento, staging, produção (Flyway desligado).
-- =============================================================================

ALTER TABLE RH_T_VAL_EVOLUCAO_CARREIRA
    ADD (ORDEM_SERVICO_ID NUMBER);

ALTER TABLE RH_T_VAL_EVOLUCAO_CARREIRA
    ADD CONSTRAINT FK_VAL_EVOL_CARR_ORDEM_SERV
        FOREIGN KEY (ORDEM_SERVICO_ID) REFERENCES RH_T_ORDEM_SERVICO (ID);
