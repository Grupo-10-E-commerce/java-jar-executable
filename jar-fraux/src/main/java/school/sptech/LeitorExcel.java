package school.sptech;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class LeitorExcel {

    public List<Compra> extrairCompras(String credit_card_fraud_dataset) {
        List<Compra> compras = new ArrayList<>();

        try (
                InputStream arquivo = new FileInputStream(credit_card_fraud_dataset);
                Workbook workbook = new XSSFWorkbook(arquivo) // caso seja .xls troque para HSSFWorkbook
        ) {

            System.out.printf("Iniciando leitura do arquivo %s%n", credit_card_fraud_dataset);

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    printarCabecalho(row);
                    continue;
                }

                // Extraindo valor das células e criando objeto Livro
                System.out.println("Lendo linha " + row.getRowNum());

                Integer TransactionID = (int) row.getCell(0).getNumericCellValue();

                String TransactionDate = row.getCell(1).getStringCellValue();

                Double Amount = row.getCell(2).getNumericCellValue();
                Integer MerchantID = (int) row.getCell(3).getNumericCellValue();
                String TransactionType = "";
                if(row.getCell(4).getCellType() == CellType.STRING){
                    TransactionType = row.getCell(4).getStringCellValue();
                } else if (row.getCell(4).getCellType() == CellType.STRING) {
                    TransactionType = row.getCell(4).getStringCellValue();
                }else {
                    TransactionType = null;
                }
                String Location = "";
                if(row.getCell(5).getCellType() == CellType.STRING){
                    Location = row.getCell(5).getStringCellValue();
                } else if (row.getCell(5).getCellType() == CellType.STRING) {
                    Location = row.getCell(5).getStringCellValue();
                }else {
                    Location = null;
                }
                Boolean IsFraud;
                if (row.getCell(6).getNumericCellValue() == 0.) {
                    IsFraud = false;
                } else {
                    IsFraud = true;
                }

                Compra compra = new Compra(TransactionID, TransactionDate, Amount, MerchantID, TransactionType, Location, IsFraud);
                compras.add(compra);
            }

            printarLinhas();
            System.out.println("Leitura do arquivo finalizada");
            printarLinhas();

            return compras;
        } catch (IOException e) {
            return compras;
        }
    }

    private void printarCabecalho(Row row) {
        printarLinhas();
        System.out.println("Lendo cabeçalho");
        for (int i = 0; i < 7; i++) {
            String coluna = row.getCell(i).getStringCellValue();
            System.out.println("Coluna " + i + ": " + coluna);
        }
        printarLinhas();
    }

    private void printarLinhas() {
        System.out.println("-".repeat(20));
    }


}