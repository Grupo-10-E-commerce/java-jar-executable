package school.sptech;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import school.sptech.client.S3Provider;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        ConexaoBD conexaoBD = new ConexaoBD();
        JdbcTemplate connection = conexaoBD.getConnection();
        DatabaseLogger databaseLogger = new DatabaseLogger(connection); // agregação agui
        LoggerCron loggerCron = new LoggerCron(connection);
        Integer idEmpresa = 1;

        S3Client s3Client = new S3Provider().getS3Client();
        String bucketName = "teste-bucket-sptech";
        final String keyName = "credit_card_fraud_dataset.xlsx";
        String webhookUrl = "url-webhook";

        SlackLogger slackLogger = new SlackLogger(connection, webhookUrl, idEmpresa);

        System.out.println("\n===============================================");
        System.out.println("INICIANDO PROCESSO DE SINCRONIZACAO COM O S3");
        System.out.println("===============================================\n");

        loggerCron.executarLog();

        // ===== Ler o Excel diretamente do S3  =====
        List<Compra> comprasList;
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(keyName)
                    .build();

            try (InputStream inputStream = s3Client.getObject(
                    getObjectRequest, ResponseTransformer.toInputStream())) {

                LeitorExcel leitorExcel = new LeitorExcel();
                comprasList = leitorExcel.extrairCompras(inputStream);
            }

            System.out.println("\nCompras sendo extraídas: " + comprasList.size());
            logBd(databaseLogger, null, "Leitura Excel", NivelSeveridade.INFO,
                    "Extraídas " + comprasList.size() + " compras do Excel");

        } catch (S3Exception e) {
            erro("Erro ao obter arquivo do S3: " + e.getMessage());
            logBd(databaseLogger, null, "Leitura Excel", NivelSeveridade.ERROR, e.getMessage());
            return;
        } catch (IOException e) {
            erro("Erro ao ler o Excel: " + e.getMessage());
            logBd(databaseLogger, null, "Leitura Excel", NivelSeveridade.ERROR, e.getMessage());
            return;
        }

        // ===== Inserções no banco =====
        for (Compra compra : comprasList) {
            if (connection.query("SELECT * FROM compra WHERE id_compra = ?",
                    new BeanPropertyRowMapper<>(Compra.class),
                    compra.getId_compra()).isEmpty()) {

                Integer TransactionID = compra.getId_compra();
                LocalDateTime TransactionDate = LocalDateTime.parse(
                        compra.getData_hora_transacao().substring(0, 19), formatter);
                Double Amount = compra.getValor_transacao();
                Integer MerchantID = compra.getId_empresa(); // desconsiderando por enquanto...
                String TransactionType = compra.getTipo_transacao();
                String Location = compra.getCidade();
                Integer IsFraud = compra.getFraude();

                connection.update("INSERT INTO compra (id_compra, id_empresa, data_hora_transacao, valor_transacao, tipo_transacao, cidade, fraude) VALUES (?, ?, ?, ?, ?, ?, ?)",
                        TransactionID, idEmpresa, TransactionDate, Amount, TransactionType, Location, IsFraud);

                sucesso("Compra " + TransactionID + " inserida no banco com sucesso.");
                logBd(databaseLogger, TransactionID, "Inserção de compra", NivelSeveridade.SUCESSO,
                        "Compra " + TransactionID + " inserida");
            } else {
                aviso("Compra " + compra.getId_compra() + " já existe no banco.");
                logBd(databaseLogger, compra.getId_compra(), "Verificação de compra", NivelSeveridade.INFO,
                        "Compra já existente");
            }
        }

        String sqlAlertas = "SELECT * FROM alerta_personalizado WHERE id_empresa = ? AND ativo = 1";
        List<AlertaPersonalizado> alertas = connection.query(sqlAlertas, new BeanPropertyRowMapper<>(AlertaPersonalizado.class), idEmpresa);

        int totalFraudesGeral = 0;
        double totalPrejuizoGeral = 0.0;

        for (Compra compra : comprasList) {
            if(compra.getFraude() != null && compra.getFraude() == 1){
                totalFraudesGeral++;
                if(compra.getValor_transacao() != null){
                    totalPrejuizoGeral += compra.getValor_transacao();
                }
            }
            for (AlertaPersonalizado alerta : alertas) {
                if(alerta.slackParams(compra, formatter)) {
                    alerta.registrarCompra(compra);
                }
            }
        }

        for (AlertaPersonalizado alerta : alertas) {
            if(alerta.getTotalFraudes() > 0) {
                slackLogger.notificarResumoAlerta(alerta);
            }
        }

        slackLogger.notificarResumoFraudes(totalFraudesGeral, totalPrejuizoGeral);


        System.out.println("\nProcesso concluído com sucesso!");
        System.out.println("===============================================\n");
    }


    private static void logBd(DatabaseLogger logger, Integer idCompra, String acao, NivelSeveridade nivel, String mensagem) {
        String sql = "INSERT INTO log (id_compra_log, data_hora, acao, nivel_severidade, mensagem) VALUES (?, ?, ?, ?, ?)";
        logger.getJdbcTemplate().update(sql, idCompra, LocalDateTime.now(), acao, nivel.getCodigo(), mensagem);
    }

    private static void sucesso(String msg) {
        System.out.println("[OK] " + msg);
    }

    private static void aviso(String msg) {
        System.out.println("[INFO] " + msg);
    }

    private static void erro(String msg) {
        System.err.println("[ERRO] " + msg);
    }
}