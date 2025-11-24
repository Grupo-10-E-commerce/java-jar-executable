package school.sptech;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

public class SlackLogger {

    private final HttpClient httpClient;
    private final String webhookUrl;
    private final Integer idEmpresa;
    private final JdbcTemplate jdbcTemplate;
    private final DatabaseLogger databaseLogger; // agregação aquiii!!
    private final ObjectMapper mapper;

    public SlackLogger(JdbcTemplate jdbcTemplate, String webhookUrl, Integer idEmpresa) {
        this.httpClient = HttpClient.newHttpClient();
        this.webhookUrl = webhookUrl;
        this.idEmpresa = idEmpresa;
        this.jdbcTemplate = jdbcTemplate;
        this.databaseLogger = new DatabaseLogger(jdbcTemplate);
        this.mapper = new ObjectMapper();
    }

    public void logInfo(String acao, String mensagem) {
        salvarLogSlack("INFO", acao, mensagem);
        enviarParaSlack("INFO", acao, mensagem);
    }

    public void logWarn(String acao, String mensagem) {
        salvarLogSlack("WARN", acao, mensagem);
        enviarParaSlack("WARN", acao, mensagem);
    }

    public void logError(String acao, String mensagem) {
        salvarLogSlack("ERROR", acao, mensagem);
        enviarParaSlack("ERROR", acao, mensagem);
    }

    public void notificarResumoFraudes(Integer totalFraudes, Double totalPrejuizo) {
        double prejuizo = totalPrejuizo != null ? totalPrejuizo : 0.0;

        String acao = "RESUMO_FRAUDES_GERAL";
        String mensagem = String.format(
                "Quantidade de fraudes detectadas: %d | Prejuízo total: R$ %.2f",
                totalFraudes, prejuizo
        );
        logInfo(acao, mensagem);
    }

    public void notificarResumoAlerta(AlertaPersonalizado alerta) {
        String acao = "RESUMO_ALERTA";

        String mensagem = """
                :bar_chart: Resumo do alerta "%s"
                • Filtros: %s
                • Total de fraudes: %d
                • Prejuízo total: R$ %.2f
                """.formatted(
                alerta.getNomeAlerta(),
                alerta.getDescricaoFiltros(),
                alerta.getTotalFraudes(),
                alerta.getTotalPrejuizo()
        );

        logInfo(acao, mensagem);
    }

    // sem usar por enquanto porque manda mensagem a cada fraude encontrada e pode ser q o chat do slack fique floodado...
    public void notificarAlerta(AlertaPersonalizado alerta, Compra compra) {
        String acao = "ALERTA_PERSONALIZADO";

        String mensagem = """
                :rotating_light: Alerta "%s" disparado
                • ID Compra: %d
                • Valor: R$ %.2f
                • Cidade: %s
                • Data: %s
                """.formatted(
                alerta.getNomeAlerta(),
                compra.getId_compra(),
                compra.getValor_transacao(),
                compra.getCidade(),
                compra.getData_hora_transacao()
        );

        logInfo(acao, mensagem);
    }

    private void salvarLogSlack(String nivel, String acao, String mensagem) {
        try {
            String sql = "INSERT INTO slack_log (id_empresa, data_hora, acao, nivel, mensagem) " +
                    "VALUES (?, ?, ?, ?, ?)";
            jdbcTemplate.update(sql,
                    idEmpresa,
                    LocalDateTime.now(),
                    acao,
                    nivel,
                    mensagem
            );
        } catch (Exception e) {
            System.out.println("[WARN] Falha ao salvar log do Slack no banco: " + e.getMessage());
        }
    }


    private boolean slackEstadoAtivo() {
        try {
            String sql = "SELECT notificacoes_ativas FROM slack_config WHERE id_empresa = ?";
            Boolean ativo = jdbcTemplate.queryForObject(sql, Boolean.class, idEmpresa);
            return ativo;
        } catch (EmptyResultDataAccessException e) {
            System.out.println("[WARN] Configuração de Slack para empresa " + idEmpresa + " não encontrada.");
            return false;
        } catch (Exception e) {
            System.out.println("[WARN] Erro ao consultar estado do Slack no banco: " + e.getMessage());
            return false;
        }
    }

    private void enviarParaSlack(String nivel, String acao, String mensagem) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            System.out.println("[WARN] Webhook do Slack não configurado. Mensagem: " + mensagem);
            return;
        }

        if (!slackEstadoAtivo()) {
            System.out.println("[INFO] Slack DESATIVADO no banco. Mensagem NÃO enviada: " + mensagem);
            return;
        }

        try {
            String texto = "[" + nivel + "] " + acao + " - " + mensagem;

            String json = criarJsonSlack(texto);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(webhookUrl))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json, StandardCharsets.UTF_8))
                    .build();

            httpClient.send(request, HttpResponse.BodyHandlers.discarding());
        } catch (Exception e) {
            System.out.println("[ERRO] Falha ao enviar mensagem para o Slack: " + e.getMessage());
        }
    }

    private String criarJsonSlack(String texto) throws JsonProcessingException {
        return mapper.writeValueAsString(Map.of("text", texto));
    }
}
