-- Competências comportamentais/técnicas e atitudes pessoais dos Componentes de Avaliação eram
-- gravadas com abrangência INPS; são avaliadas por colaborador, por isso a abrangência é INDIVIDUAL.
-- O serviço passou a gravar INDIVIDUAL (ComponenteAvaliacaoService.ABRANGENCIA_DEFAULT); isto
-- corrige as linhas já existentes.
UPDATE RH_T_PARAM_OBJETIVO
   SET ABRAGENCIA = 'INDIVIDUAL'
 WHERE COMPONENTE IN ('COMPETENCIA_COMPORTAMENTAL', 'COMPETENCIA_TECNICA', 'ATITUDE_PESSOAL')
   AND ABRAGENCIA = 'INPS';
