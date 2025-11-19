package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.springframework.dao.EmptyResultDataAccessException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;


public class SlackLogger extends DatabaseLogger {

    private final HttpClient httpClient;
    private final String webhookUrl;
    private final Integer idEmpresa;
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper mapper;

    public SlackLogger(JdbcTemplate jdbcTemplate, String webhookUrl, Integer idEmpresa) {
        super(jdbcTemplate);
        this.httpClient = HttpClient.newHttpClient();
        this.webhookUrl = webhookUrl;
        this.idEmpresa = idEmpresa;
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = new ObjectMapper();
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

    private boolean slackEstadoAtivo() {
        try {
            String sql = "SELECT slack_notificacoes_ativas FROM empresa WHERE id_empresa = ?";
                    Boolean ativo = jdbcTemplate.queryForObject(sql, Boolean.class, idEmpresa);

            return Boolean.TRUE.equals(ativo);
        } catch (EmptyResultDataAccessException e) {
            System.out.println("[WARN] Empresa " + idEmpresa + " não encontrada ao verificar estado do Slack.");
            return true;
        } catch (Exception e) {
            System.out.println("[WARN] Erro ao consultar estado do Slack no banco: " + e.getMessage());
            return true;
        }
    }

    private void enviarParaSlack(String nivel, String acao, String mensagem) {
        if (webhookUrl == null || webhookUrl.isBlank()) {
            System.out.println("[WARN] Webhook do Slack não configurado. Mensagem: " + mensagem);
            return;
        }

        if(!slackEstadoAtivo()) {
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

    private String criarJsonSlack(String texto) throws JsonProcessingException{
        return mapper.writeValueAsString(Map.of("text", texto));
    }
}
