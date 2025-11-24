package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;
import java.time.LocalDateTime;

public class DatabaseLogger {

    private JdbcTemplate jdbcTemplate;

    public DatabaseLogger(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void logInfo(String acao, String mensagem) {
        log(NivelSeveridade.INFO, acao, mensagem);
    }

    public void logWarn(String acao, String mensagem) {
        log(NivelSeveridade.WARN, acao, mensagem);
    }

    public void logError(String acao, String mensagem) {
        log(NivelSeveridade.ERROR, acao, mensagem);
    }

    public void logSucesso(String acao, String mensagem) {
        log(NivelSeveridade.SUCESSO, acao, mensagem);
    }

    protected void log(NivelSeveridade nivel, String acao, String mensagem) {
        try {
            jdbcTemplate.update(
                    "INSERT INTO log (acao, nivel_severidade, mensagem, data_hora) VALUES (?, ?, ?, ?)",
                    acao, nivel.name(), mensagem, LocalDateTime.now()
            );
        } catch (Exception e) {
            // não imprimimos no console; apenas ignoramos se falhar o log
        }
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}