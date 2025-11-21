package school.sptech;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LeitorExcel {

    private static final DateTimeFormatter DTF =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<Compra> extrairCompras(InputStream arquivo) {
        List<Compra> compras = new ArrayList<>();
        DataFormatter fmt = new DataFormatter();


        try (Workbook wb = new XSSFWorkbook(arquivo)) {
            System.out.println("Iniciando leitura do InputStream");

            Sheet sheet = wb.getSheetAt(0);
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    printarCabecalho(row, fmt);
                    continue;
                }

                System.out.println("Lendo linha " + row.getRowNum());

                Integer id_compra          = getInt(row, 0);
                String  data_hora_transacao= getDateAsText(row.getCell(1), fmt); // trata numérico como data
                Double  valor_transacao    = getDouble(row, 2);
                Integer id_empresa         = getInt(row, 3);
                String  tipo_transacao     = getString(row, 4, fmt);
                String  cidade             = getString(row, 5, fmt);
                Integer fraude             = getInt(row, 6);

                if (id_compra == null) continue;

                compras.add(new Compra(
                        id_compra,
                        data_hora_transacao,
                        valor_transacao,
                        id_empresa,
                        tipo_transacao,
                        cidade,
                        fraude
                ));
            }

            printarLinhas();
            System.out.println("Leitura do arquivo finalizada");
            printarLinhas();
            return compras;

        } catch (IOException e) {
            System.out.println("[ERRO] Falha lendo XLSX: " + e.getMessage());
            return compras;
        }
    }

    // -------- helpers de leitura seguros --------

    private static Integer getInt(Row r, int idx) {
        Cell c = r.getCell(idx);
        if (c == null) return null;
        try {
            return switch (c.getCellType()) {
                case NUMERIC -> (int) c.getNumericCellValue();
                case STRING -> {
                    String s = c.getStringCellValue().trim();
                    yield s.isEmpty() ? null : Integer.parseInt(s);
                }
                case FORMULA -> {
                    try { yield (int) c.getNumericCellValue(); }
                    catch (Exception ignore) { yield null; }
                }
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    private static Double getDouble(Row r, int idx) {
        Cell c = r.getCell(idx);
        if (c == null) return null;
        try {
            return switch (c.getCellType()) {
                case NUMERIC -> c.getNumericCellValue();
                case STRING -> {
                    String s = c.getStringCellValue().trim().replace(",", ".");
                    yield s.isEmpty() ? null : Double.parseDouble(s);
                }
                case FORMULA -> {
                    try { yield c.getNumericCellValue(); }
                    catch (Exception ignore) { yield null; }
                }
                default -> null;
            };
        } catch (Exception e) {
            return null;
        }
    }

    private static String getString(Row r, int idx, DataFormatter fmt) {
        Cell c = r.getCell(idx);
        if (c == null) return null;
        try {
            String v = fmt.formatCellValue(c);
            return v != null && !v.isBlank() ? v.trim() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private static String getDateAsText(Cell c, DataFormatter fmt) {
        if (c == null) return null;
        try {
            if (c.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(c)) {
                Date date = c.getDateCellValue();
                LocalDateTime ldt = LocalDateTime.ofInstant(
                        Instant.ofEpochMilli(date.getTime()),
                        ZoneId.systemDefault()
                );
                return ldt.format(DTF);
            }
            // se não for data numérica, usa formatação textual do Excel
            String v = fmt.formatCellValue(c);
            return v != null && !v.isBlank() ? v.trim() : null;
        } catch (Exception e) {
            return null;
        }
    }

    private void printarCabecalho(Row row, DataFormatter fmt) {
        System.out.println("Colunas do cabecalho:");
        short last = row.getLastCellNum();
        for (int i = 0; i < last; i++) {
            Cell c = row.getCell(i);
            String coluna = c == null ? "(null)" : fmt.formatCellValue(c);
            System.out.println("coluna " + i + ": " + coluna);
        }
        printarLinhas();
    }

    private void printarLinhas() {
        System.out.println("---------------------");
    }
}
