package school.sptech;

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

public class Main {
    public static void main(String[] args) {

        ConexaoBD conexaoBD = new ConexaoBD();
        JdbcTemplate connection = conexaoBD.getConnection();

        String nomeArquivo = "credit_card_fraud_dataset.xlsx";

        // Extraindo os livros do arquivo
        LeitorExcel leitorExcel = new LeitorExcel();
        List<Compra> comprasList = leitorExcel.extrairCompras(nomeArquivo);

        System.out.println("Compras extraídas:");
        for (int i = 0; i < comprasList.size(); i++) {
            Integer TransactionID = comprasList.get(i).getId_compra();
            String TransactionDate = comprasList.get(i).getData_hora_transacao();
            Double Amount = comprasList.get(i).getValor_transacao();
            Integer MerchantID = comprasList.get(i).getId_empresa();
            String TransactionType = comprasList.get(i).getTipo_transacao();
            String Location = comprasList.get(i).getCidade();
            Integer IsFraud = comprasList.get(i).getFraude();
            // connection.update("INSERT INTO compra VALUES (?,?,?,?,?,?,?)", TransactionID, TransactionDate, Amount, MerchantID, TransactionType, Location, IsFraud);

            // connection.update("INSERT INTO logs VALUES (Default, ?, Default)", "Compra : " + TransactionID + " Inserida com sucesso");
        }


    }
}
