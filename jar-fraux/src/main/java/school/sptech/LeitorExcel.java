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

    public List<Compra> extrairCompras(String requisicao) {
        List<Compra> compras = new ArrayList<>();

        try (
                InputStream arquivo = new FileInputStream(requisicao);
                Workbook workbook = new XSSFWorkbook(arquivo) // caso seja .xls troque para HSSFWorkbook
        ) {

            System.out.printf("Iniciando leitura do arquivo %s%n", requisicao);

            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    printarCabecalho(row);
                    continue;
                }

                // Extraindo valor das células e criando objeto Livro
                System.out.println("Lendo linha " + row.getRowNum());

                Integer id_compra = (int) row.getCell(0).getNumericCellValue();

                String data_hora_transacao = row.getCell(1).getStringCellValue();

                Double valor_transacao = row.getCell(2).getNumericCellValue();
                Integer id_empresa = (int) row.getCell(3).getNumericCellValue();
                String tipo_transacao = "";
                if(row.getCell(4).getCellType() == CellType.STRING){
                    tipo_transacao = row.getCell(4).getStringCellValue();
                } else if (row.getCell(4).getCellType() == CellType.STRING) {
                    tipo_transacao = row.getCell(4).getStringCellValue();
                }else {
                    tipo_transacao = null;
                }
                String cidade = "";
                if(row.getCell(5).getCellType() == CellType.STRING){
                    cidade = row.getCell(5).getStringCellValue();
                } else if (row.getCell(5).getCellType() == CellType.STRING) {
                    cidade = row.getCell(5).getStringCellValue();
                }else {
                    cidade = null;
                }
                Integer fraude = (int) row.getCell(6).getNumericCellValue();

                Compra compra = new Compra(id_compra, data_hora_transacao, valor_transacao, id_empresa, tipo_transacao, cidade, fraude);
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