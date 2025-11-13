package school.sptech;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Cron {
    private static final String caminhoLogger = "/home/ubuntu/logger.txt";
    private static final DateTimeFormatter dataHora = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void executarLog(){
        String linha = "Executado às: " + LocalDateTime.now().format(dataHora);

        try(FileWriter fw = new FileWriter(caminhoLogger, true)) {
            fw.write(linha);
            fw.write(System.lineSeparator());
        } catch (IOException e) {
            System.out.println("[ERRO] Não foi possível gerar log: " + e.getMessage());
        }
    }
}