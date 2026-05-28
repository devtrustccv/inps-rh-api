import java.sql.*;
import java.nio.file.*;

/**
 * Corrige a função CALCULO_HORA_EXTRA no package RH_PROCESSAMENTO_SALARIAL_DB.
 *
 * Bugs corrigidos:
 *  1. V_SAL_DIARIO calculado antes de V_HORAS_DIARIAS ser carregado (NULL division)
 *  2. DIARIA VARCHAR2 "08:00" não pode ser atribuído a NUMBER — conversão explícita
 *  3. V_DIAS_UTES declarado como NUMBER mas IS_DIA_UTEL retorna VARCHAR2 ('S'/'N')
 *  4. v_valor_mes e v_valor_total não inicializados (NULL + x = NULL)
 *  5. V_VALOR_HORAS não é resetado a 0 no início de cada iteração do loop
 *  6. Feriado query usa SYSDATE em vez de v_data (a data corrente do loop)
 *  7. ANO_REFERENTE usa P_DATA_INICIO em vez de v_data
 *  8. Bloco BEGIN da query de feriados não tem EXCEPTION handler
 *  9. HE_VALOR_DUTIL/DNUTIL multiplicados directamente (ex: 50) sem dividir por 100
 * 10. Função não tem RETURN — lança ORA-06503 em runtime
 */
public class FixCalcHoraExtra {

    static final String URL  = "jdbc:oracle:thin:@62.84.179.137:1521:xe";
    static final String USER = "INPSRH";
    static final String PASS = "Pa$$w0rd";

    // Versão corrigida e completa da função (substitui integralmente a existente)
    static final String CORRECTED_FUNCTION =
        " FUNCTION CALCULO_HORA_EXTRA (P_TIPREL_ID NUMBER,P_DATA_INICIO DATE,P_DATA_FIM DATE,P_DIAS_APLICADA VARCHAR2, P_HORAS_DIARIA NUMBER)\n" +
        "   RETURN NUMBER\n" +
        "   IS\n" +
        "\n" +
        "     V_LOCAL           NUMBER;\n" +
        "     V_HE_VALOR_DUTIL  NUMBER;\n" +
        "     V_HE_VALOR_DNUTIL NUMBER;\n" +
        "     v_geo             SIPSGLOBAL.GLB_T_GEOGRAFIA%ROWTYPE;\n" +
        "     V_COUNT           NUMBER;\n" +
        "     v_data            DATE := P_DATA_INICIO;\n" +
        "     V_SALARIO         NUMBER;\n" +
        "     V_HORAS_DIARIAS   NUMBER;\n" +
        "     V_SAL_DIARIO      NUMBER;\n" +
        "     V_VALOR_HORAS     NUMBER;\n" +
        "     v_valor_mes       NUMBER := 0;\n" +
        "     v_valor_total     NUMBER := 0;\n" +
        "     v_mes_atual       VARCHAR2(10);\n" +
        "     V_DIAS_UTES       VARCHAR2(1);\n" +    // FIX 3: era NUMBER
        " BEGIN\n" +
        "\n" +
        "     v_mes_atual := TO_CHAR(v_data, 'YYYYMM');\n" +
        "\n" +
        "     -- FIX 1+2: buscar parametros ANTES de calcular V_SAL_DIARIO;\n" +
        "     --           converter DIARIA 'HH:MM' (VARCHAR2) para horas (NUMBER)\n" +
        "     BEGIN\n" +
        "         SELECT HE_VALOR_DUTIL,\n" +
        "                HE_VALOR_DNUTIL,\n" +
        "                TO_NUMBER(SUBSTR(DIARIA, 1, 2)) + TO_NUMBER(NVL(SUBSTR(DIARIA, 4, 2), '0')) / 60\n" +
        "         INTO   V_HE_VALOR_DUTIL, V_HE_VALOR_DNUTIL, V_HORAS_DIARIAS\n" +
        "         FROM   RH_T_ASSIDUIDADE_PARAMETRO\n" +
        "         WHERE  ESTADO = 'A'\n" +
        "         AND    ROWNUM = 1;\n" +
        "     EXCEPTION WHEN OTHERS THEN\n" +
        "         V_HORAS_DIARIAS   := 8;\n" +
        "         V_HE_VALOR_DUTIL  := 50;\n" +
        "         V_HE_VALOR_DNUTIL := 75;\n" +
        "     END;\n" +
        "\n" +
        "     V_SALARIO    := GET_SALARIO_BASE (P_TIPREL_ID, P_DATA_INICIO);\n" +
        "     V_SAL_DIARIO := V_SALARIO / NULLIF(V_HORAS_DIARIAS, 0);\n" +
        "\n" +
        "     BEGIN\n" +
        "         SELECT E.*\n" +
        "          INTO  v_geo\n" +
        "          FROM  rh_t_tipos_relacionamento A,\n" +
        "                RH_T_MOBILIDADE B,\n" +
        "                rh_t_param_local_trab C,\n" +
        "                SIPSGLOBAL.GLB_T_UPS D,\n" +
        "                SIPSGLOBAL.GLB_T_GEOGRAFIA E\n" +
        "          WHERE A.MOB_ID = B.ID\n" +
        "          AND   B.LOCAL_TRAB_ID = C.ID\n" +
        "          AND   C.UPS_ID = D.ID\n" +
        "          AND   D.ID_GEOGRAFIA = E.ID\n" +
        "          AND   A.ID = P_TIPREL_ID;\n" +
        "     EXCEPTION WHEN OTHERS THEN\n" +
        "         NULL;\n" +
        "     END;\n" +
        "\n" +
        "  WHILE v_data <= P_DATA_FIM\n" +
        "   LOOP\n" +
        "     -- FIX 5: resetar V_VALOR_HORAS e V_COUNT em cada iteracao\n" +
        "     V_VALOR_HORAS := 0;\n" +
        "     V_COUNT       := 0;\n" +
        "\n" +
        "     -- FIX 6+7+8: usar v_data (nao SYSDATE), ano de v_data, exception handler\n" +
        "     BEGIN\n" +
        "          SELECT COUNT(1)\n" +
        "          INTO V_COUNT\n" +
        "          FROM RH_T_PARAM_FERIADO A\n" +
        "          WHERE ((FIXO_ANO = 'NAO' AND DATA_ESPECIFICA = TRUNC(v_data))\n" +
        "             OR  (FIXO_ANO = 'SIM' AND DIA = TO_CHAR(v_data,'DD') AND MES = TO_CHAR(v_data,'MM')))\n" +
        "          AND (A.ANO_REFERENTE = 'TODOS' OR A.ANO_REFERENTE = TO_CHAR(v_data,'YYYY'))\n" +
        "          AND (A.GEOGR_ID IS NULL OR A.GEOGR_ID = v_geo.CONCELHO);\n" +
        "     EXCEPTION WHEN OTHERS THEN V_COUNT := 0;\n" +
        "     END;\n" +
        "\n" +
        "     V_DIAS_UTES := IS_DIA_UTEL(v_data, NULL);\n" +
        "\n" +
        "     -- FIX 9: dividir HE_VALOR_DUTIL/DNUTIL por 100 (sao percentagens: 50 = 50%)\n" +
        "     IF  P_DIAS_APLICADA = 'DIAS_NAO_UTEIS' THEN\n" +
        "       IF V_DIAS_UTES = 'N' OR V_COUNT > 0 THEN\n" +
        "         V_VALOR_HORAS := (V_SAL_DIARIO * (V_HE_VALOR_DNUTIL / 100) * NVL(P_HORAS_DIARIA, 0));\n" +
        "       END IF;\n" +
        "     ELSIF P_DIAS_APLICADA = 'DIAS_UTEIS_NAO_UTEIS' THEN\n" +
        "       IF V_DIAS_UTES = 'N' OR V_COUNT > 0 THEN\n" +
        "         V_VALOR_HORAS := (V_SAL_DIARIO * (V_HE_VALOR_DNUTIL / 100) * NVL(P_HORAS_DIARIA, 0));\n" +
        "       ELSE\n" +
        "         V_VALOR_HORAS := (V_SAL_DIARIO * (V_HE_VALOR_DUTIL / 100) * NVL(P_HORAS_DIARIA, 0));\n" +
        "       END IF;\n" +
        "     ELSIF P_DIAS_APLICADA = 'DIAS_UTEIS' THEN\n" +
        "       IF V_DIAS_UTES = 'S' AND V_COUNT = 0 THEN\n" +
        "         V_VALOR_HORAS := (V_SAL_DIARIO * (V_HE_VALOR_DUTIL / 100) * NVL(P_HORAS_DIARIA, 0));\n" +
        "       END IF;\n" +
        "     END IF;\n" +
        "\n" +
        "         IF TO_CHAR(v_data,'YYYYMM') <> v_mes_atual THEN\n" +
        "             v_mes_atual := TO_CHAR(v_data,'YYYYMM');\n" +
        "             v_valor_mes := 0;\n" +
        "          END IF;\n" +
        "\n" +
        "          v_valor_mes   := v_valor_mes   + V_VALOR_HORAS;\n" +
        "          v_valor_total := v_valor_total + V_VALOR_HORAS;\n" +
        "\n" +
        "     v_data := v_data + 1;\n" +
        "   END LOOP;\n" +
        "\n" +
        "    -- FIX 10: RETURN em falta\n" +
        "    RETURN NVL(v_valor_total, 0);\n" +
        "\n" +
        "    END CALCULO_HORA_EXTRA;\n";

    public static void main(String[] args) throws Exception {
        Class.forName("oracle.jdbc.OracleDriver");
        Connection conn = DriverManager.getConnection(URL, USER, PASS);
        Statement st = conn.createStatement();

        // 1. Fetch full package body from the original TXT backup (clean source)
        //    and splice the corrected CALCULO_HORA_EXTRA into the live package
        ResultSet rs = st.executeQuery(
            "SELECT text FROM all_source " +
            "WHERE owner='INPSRH' AND name='TXT_PROCESSAMENTO_SALARIAL_DB' " +
            "AND type='PACKAGE BODY' ORDER BY line");

        StringBuilder sb = new StringBuilder();
        while (rs.next()) sb.append(rs.getString(1));
        String body = sb.toString();
        // Skip line 1 — it's the "PACKAGE BODY TXT_... AS/IS" declaration
        int firstNL = body.indexOf('\n');
        if (firstNL >= 0) body = body.substring(firstNL + 1);
        System.out.println("Package body fetched (body content): " + body.length() + " chars");

        // 2. Locate CALCULO_HORA_EXTRA section boundaries
        String startMarker = "FUNCTION CALCULO_HORA_EXTRA ";
        String endMarker   = "FUNCTION CALCULO_FALTA_DIARIO ";

        int start = body.indexOf(startMarker);
        int end   = body.indexOf(endMarker);

        if (start < 0 || end < 0 || start >= end) {
            System.out.println("ERROR: Could not locate function boundaries.");
            System.out.println("  startMarker found: " + (start >= 0));
            System.out.println("  endMarker found:   " + (end >= 0));
            conn.close();
            return;
        }

        // Keep the separator line (dashes) that sits between the two functions
        // Find the last "----" block before the endMarker
        String between = body.substring(start, end);
        int lastDash = between.lastIndexOf("---");
        String separator = lastDash >= 0 ? between.substring(lastDash) : "\n";

        System.out.println("Replacing function from char " + start + " to " + end);
        System.out.println("Separator: [" + separator.trim().substring(0, Math.min(40, separator.trim().length())) + "...]");

        // 3. Build new body: before + corrected function + separator kept + from endMarker
        //    Also fix the package closing identifier (TXT_... -> RH_...)
        String tail = body.substring(end)
            .replace("END txt_PROCESSAMENTO_SALARIAL_DB;", "END RH_PROCESSAMENTO_SALARIAL_DB;");

        String newBody =
            "CREATE OR REPLACE PACKAGE BODY INPSRH.RH_PROCESSAMENTO_SALARIAL_DB AS\n" +
            body.substring(0, start) +
            CORRECTED_FUNCTION +
            "\n" + separator +
            tail;

        // 4. Write to file for inspection
        Path outFile = Path.of("c:/Users/ivanick.santos/Nick-personal/personal-workspace/projects/RH_INPS_SERVICE/scripts/fix_calculo_hora_extra.sql");
        Files.writeString(outFile, newBody);
        System.out.println("Fixed SQL written to: " + outFile + " (" + newBody.length() + " chars)");

        // 5. Execute
        System.out.println("Executing CREATE OR REPLACE PACKAGE BODY...");
        conn.setAutoCommit(true);
        try {
            st.execute(newBody);
            System.out.println("DDL executed.");

            // Check status
            ResultSet status = conn.createStatement().executeQuery(
                "SELECT object_type, status FROM all_objects " +
                "WHERE owner='INPSRH' AND object_name='RH_PROCESSAMENTO_SALARIAL_DB' " +
                "AND object_type='PACKAGE BODY'");
            if (status.next())
                System.out.println("Status: " + status.getString(1) + " -> " + status.getString(2));

            // Check for compilation errors
            ResultSet errs = conn.createStatement().executeQuery(
                "SELECT line, position, text FROM all_errors " +
                "WHERE owner='INPSRH' AND name='RH_PROCESSAMENTO_SALARIAL_DB' " +
                "AND type='PACKAGE BODY' ORDER BY line");
            boolean hasErrors = false;
            while (errs.next()) {
                if (!hasErrors) { System.out.println("Compilation errors:"); hasErrors = true; }
                System.out.println("  L" + errs.getInt(1) + ":" + errs.getInt(2) + " " + errs.getString(3));
            }
            if (!hasErrors) System.out.println("No compilation errors. SUCCESS.");

        } catch (SQLException e) {
            System.out.println("SQL Error: " + e.getMessage());
        }
        conn.close();
    }
}
