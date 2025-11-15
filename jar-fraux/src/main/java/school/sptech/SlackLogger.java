package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

public class SlackLogger extends DatabaseLogger {

    private final HttpClient httpClient;
    private final String webhookUrl;

    public SlackLogger(JdbcTemplate jdbcTemplate, String webhookUrl) {
        super(jdbcTemplate);
        this.httpClient = HttpClient.newHttpClient();
        this.webhookUrl = webhookUrl;
    }

    @Override
    public void logInfo(String acao, String mensagem) {
        super.logInfo(acao, mensagem);
        enviarParaSlack("INFO", acao, mensagem);
    }

    @Override
    public void logWarn(String acao, String mensagem) {
        super.logWarn(acao, mensagem);
        enviarParaSlack("WARN", acao, mensagem);
    }

    @Override
    public void logError(String acao, String mensagem) {
        super.logError(acao, mensagem);
        enviarParaSlack("ERROR", acao, mensagem);
    }

    public void notificarResumoFraudes(Integer totalFraudes) {
        String acao = "RESUMO_FRAUDES";
        String mensagem = "Quantidade de fraudes detectadas " + totalFraudes;
        logInfo(acao, mensagem);
    }

    private void enviarParaSlack(String nivel, String acao, String mensagem) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            System.out.println("[WARN] Webhook do Slack não configurado. Mensagem: " + mensagem);
            return;
        }

        try {
            String texto = "[" + nivel + "] " + acao + " - " + mensagem;

            String json = "{\"text\":\"" + escapeJson(texto) + "\"}";

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

    private String escapeJson(String text) {
        return text
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }
}
