package school.sptech;

import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDateTime;
import java.util.List;

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
            Integer TransactionID = comprasList.get(i).getTransactionID();
            String TransactionDate = comprasList.get(i).getTransactionDate();
            Double Amount = comprasList.get(i).getAmount();
            Integer MerchantID = comprasList.get(i).getMerchantID();
            String TransactionType = comprasList.get(i).getTransactionType();
            String Location = comprasList.get(i).getLocation();
            Boolean IsFraud = comprasList.get(i).getFraud();
            connection.update("INSERT INTO compra VALUES (?,?,?,?,?,?,?)", TransactionID, TransactionDate, Amount, MerchantID, TransactionType, Location, IsFraud);
            connection.update("INSERT INTO logs VALUES (Default, ?, Default)", "Compra : " + TransactionID + " Inserida com sucesso");
        }

    }
}
