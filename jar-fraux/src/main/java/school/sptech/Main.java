package school.sptech;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String nomeArquivo = "credit_card_fraud_dataset.xlsx";

        // Extraindo os livros do arquivo
        LeitorExcel leitorExcel = new LeitorExcel();
        List<Compra> comprasList = leitorExcel.extrairLivros(nomeArquivo);

        System.out.println("Compras extraídas:");
        for (Compra compra : comprasList) {
            System.out.println(compra);
        }
    }
}