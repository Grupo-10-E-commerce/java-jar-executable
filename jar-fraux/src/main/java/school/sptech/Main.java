package school.sptech;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import school.sptech.client.S3Provider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

public class Main {

    public static void main(String[] args) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ConexaoBD conexaoBD = new ConexaoBD();
        JdbcTemplate connection = conexaoBD.getConnection();

        S3Client s3Client = new S3Provider().getS3Client();
        String bucketName = "bucket-fraux-teste";

        System.out.println("\n===============================================");
        System.out.println("🚀 INICIANDO PROCESSO DE SINCRONIZAÇÃO COM O S3");
        System.out.println("===============================================\n");

        // 🪣 Criação de bucket
        try {
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.createBucket(createBucketRequest);
            sucesso("Bucket criado com sucesso: " + bucketName);
            logBd(connection, 0, "Criação de bucket", "INFO", "Bucket criado com sucesso: " + bucketName);
        } catch (S3Exception e) {
            aviso("Bucket já existe ou erro ao criar: " + e.getMessage());
            logBd(connection, 0, "Criação de bucket", "ERRO", e.getMessage());
        }

        // 📦 Listagem de buckets
        try {
            List<Bucket> buckets = s3Client.listBuckets().buckets();
            System.out.println("\n📂 Buckets encontrados:");
            for (Bucket bucket : buckets) {
                System.out.println("   - " + bucket.name());
            }
            logBd(connection, 0, "Listagem de buckets", "INFO", "Buckets listados com sucesso");
        } catch (S3Exception e) {
            erro("Erro ao listar buckets: " + e.getMessage());
            logBd(connection, 0, "Listagem de buckets", "ERRO", e.getMessage());
        }

        // 📜 Listagem de objetos no bucket
        try {
            ListObjectsRequest requisicao = ListObjectsRequest.builder()
                    .bucket(bucketName)
                    .build();

            List<S3Object> objects = s3Client.listObjects(requisicao).contents();
            System.out.println("\n📋 Objetos no bucket '" + bucketName + "':");
            for (S3Object object : objects) {
                System.out.println("   - " + object.key());
            }
            logBd(connection, 0, "Listagem de objetos", "INFO", "Listagem de " + objects.size() + " objetos concluída");
        } catch (S3Exception e) {
            erro("Erro ao listar objetos: " + e.getMessage());
            logBd(connection, 0, "Listagem de objetos", "ERRO", e.getMessage());
        }

        // 📤 Upload do arquivo
        try {
            String uniqueFileName = UUID.randomUUID().toString();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .build();

            File file = new File("credit_card_fraud_dataset.xlsx");
            s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));

            sucesso("Arquivo '" + file.getName() + "' enviado com sucesso como: " + uniqueFileName);
            logBd(connection, 0, "Upload de arquivo", "SUCESSO", "Arquivo " + uniqueFileName + " enviado");
        } catch (S3Exception e) {
            erro("Erro ao fazer upload: " + e.getMessage());
            logBd(connection, 0, "Upload de arquivo", "ERRO", e.getMessage());
        }

        // 📥 Download de arquivos
        InputStream inputStream = null;
        try {
            ListObjectsRequest requisicao = ListObjectsRequest.builder()
                    .bucket(bucketName)
                    .build();
            List<S3Object> objects = s3Client.listObjects(requisicao).contents();

            for (S3Object object : objects) {
                GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                        .bucket(bucketName)
                        .key(object.key())
                        .build();

                inputStream = s3Client.getObject(getObjectRequest, ResponseTransformer.toInputStream());
                Files.copy(inputStream, new File(object.key()).toPath());
                sucesso("Arquivo baixado: " + object.key());
                logBd(connection, 0, "Download de arquivo", "SUCESSO", "Arquivo baixado: " + object.key());
            }
        } catch (IOException | S3Exception e) {
            erro("Erro ao baixar arquivos: " + e.getMessage());
            logBd(connection, 0, "Download de arquivo", "ERRO", e.getMessage());
        }

        // 💾 Extraindo compras do Excel
        LeitorExcel leitorExcel = new LeitorExcel();
        List<Compra> comprasList = leitorExcel.extrairCompras("credit_card_fraud_dataset.xlsx");

        System.out.println("\n🧾 Compras sendo extraídas: " + comprasList.size());
        logBd(connection, 0, "Leitura Excel", "INFO", "Extraídas " + comprasList.size() + " compras do Excel");

        for (Compra compra : comprasList) {
            if (connection.query("SELECT * FROM compra WHERE id_compra = ?",
                    new BeanPropertyRowMapper<>(Compra.class),
                    compra.getId_compra()).isEmpty()) {

                Integer TransactionID = compra.getId_compra();
                LocalDateTime TransactionDate = LocalDateTime.parse(compra.getData_hora_transacao().substring(0, 19), formatter);
                Double Amount = compra.getValor_transacao();
                Integer MerchantID = compra.getId_empresa();
                String TransactionType = compra.getTipo_transacao();
                String Location = compra.getCidade();
                Integer IsFraud = compra.getFraude();

                connection.update("INSERT INTO compra VALUES (DEFAULT,?,?,?,?,?,?,?)",
                        TransactionID, MerchantID, TransactionDate, Amount, TransactionType, Location, IsFraud);

                sucesso("Compra " + TransactionID + " inserida no banco com sucesso.");
                logBd(connection, TransactionID, "Inserção de compra", "SUCESSO", "Compra " + TransactionID + " inserida");
            } else {
                aviso("Compra " + compra.getId_compra() + " já existe no banco.");
                logBd(connection, compra.getId_compra(), "Verificação de compra", "INFO", "Compra já existente");
            }
        }

        System.out.println("\n🏁 Processo concluído com sucesso!");
        System.out.println("===============================================\n");
    }

    // ======== FUNÇÕES DE LOG NO BANCO ========
    private static void logBd(JdbcTemplate connection, int idCompra, String acao, String nivel, String mensagem) {
        String sql = "INSERT INTO log (id_compra_log, data_hora, acao, nivel_severidade, mensagem) VALUES (?, ?, ?, ?, ?)";
        connection.update(sql, idCompra, LocalDateTime.now(), acao, nivel, mensagem);
    }

    // ======== FUNÇÕES ESTÉTICAS NO TERMINAL ========
    private static void sucesso(String msg) {
        System.out.println("✅ " + msg);
    }

    private static void aviso(String msg) {
        System.out.println("⚠️ " + msg);
    }

    private static void erro(String msg) {
        System.out.println("❌ " + msg);
    }


}
