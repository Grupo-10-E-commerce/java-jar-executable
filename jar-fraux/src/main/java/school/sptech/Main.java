package school.sptech;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        String nomeArquivo = "melhores-livros.xlsx";

        // Extraindo os livros do arquivo
        LeitorExcel leitorExcel = new LeitorExcel();
        List<Compra> comprasList = leitorExcel.extrairLivros(nomeArquivo);

        System.out.println("Livros extraídos:");
        for (Compra compra : comprasList) {
            System.out.println(compra);
        }
    }
}