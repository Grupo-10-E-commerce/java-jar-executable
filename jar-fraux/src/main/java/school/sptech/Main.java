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
import java.util.List;
import java.util.UUID;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) throws IOException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        ConexaoBD conexaoBD = new ConexaoBD();
        JdbcTemplate connection = conexaoBD.getConnection();

        S3Client s3Client = new S3Provider().getS3Client();
        String bucketName = "bucket-fraux-teste";

        try {
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.createBucket(createBucketRequest);
            System.out.println("Bucket criado com sucesso: " + bucketName);
        } catch (S3Exception e) {
            System.err.println("Erro ao criar o bucket: " + e.getMessage());
        }

        try {
            List<Bucket> buckets = s3Client.listBuckets().buckets();
            System.out.println("Lista de buckets:");
            for (Bucket bucket : buckets) {
                System.out.println("- " + bucket.name());
            }
        } catch (S3Exception e) {
            System.err.println("Erro ao listar buckets: " + e.getMessage());
        }

        try {
            ListObjectsRequest requisicao = ListObjectsRequest.builder()
                    .bucket(bucketName)
                    .build();

            List<S3Object> objects = s3Client.listObjects(requisicao).contents();
            System.out.println("Objetos no bucket " + bucketName + ":");
            for (S3Object object : objects) {
                System.out.println("- " + object.key());
            }
        } catch (S3Exception e) {
            System.err.println("Erro ao listar objetos no bucket: " + e.getMessage());
        }

        try {
            String uniqueFileName = UUID.randomUUID().toString();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(uniqueFileName)
                    .build();

            File file = new File("credit_card_fraud_dataset.xlsx");
            s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));

            System.out.println("Arquivo '" + file.getName() + "' enviado com sucesso com o nome: " + uniqueFileName);
        } catch (S3Exception e) {
            System.err.println("Erro ao fazer upload do arquivo: " + e.getMessage());
        }

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
                System.out.println("Arquivo baixado: " + object.key());
            }
        } catch (IOException | S3Exception e) {
            System.err.println("Erro ao fazer download dos arquivos: " + e.getMessage());
        }


        // Extraindo os livros do arquivo
        LeitorExcel leitorExcel = new LeitorExcel();
        List<Compra> comprasList = leitorExcel.extrairCompras("credit_card_fraud_dataset.xlsx");

        System.out.println("Compras extraídas:");
        for (int i = 0; i < comprasList.size(); i++) {
            // Verifica se o dado já existe no banco pelo seu ID
            if (connection.query("select * from compra where id_compra = ?", new BeanPropertyRowMapper<>(Compra.class), comprasList.get(i).getId_compra()).isEmpty()) {
                Integer TransactionID = comprasList.get(i).getId_compra();
                LocalDateTime TransactionDate = LocalDateTime.parse(comprasList.get(i).getData_hora_transacao().substring(0, 19), formatter);
                Double Amount = comprasList.get(i).getValor_transacao();
                Integer MerchantID = comprasList.get(i).getId_empresa();
                String TransactionType = comprasList.get(i).getTipo_transacao();
                String Location = comprasList.get(i).getCidade();
                Integer IsFraud = comprasList.get(i).getFraude();
                connection.update("INSERT INTO compra VALUES (Default,?,?,?,?,?,?,?)", TransactionID, MerchantID, TransactionDate, Amount, TransactionType, Location, IsFraud);

                //connection.update("INSERT INTO logs VALUES (Default, ?, Default)", "Compra : " + TransactionID + " Inserida com sucesso");
            } else {
                System.out.println("Dado: " + connection.query("select * from compra where id_compra = ?", new BeanPropertyRowMapper<>(Compra.class), comprasList.get(i).getId_compra()) + " já presente no banco");
            }
        }
    }
}