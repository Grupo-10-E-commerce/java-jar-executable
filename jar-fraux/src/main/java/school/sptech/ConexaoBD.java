package school.sptech;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class ConexaoBD {
    private final DataSource conexao;

    public ConexaoBD() {

        BasicDataSource basicDataSource = new BasicDataSource();

        basicDataSource.setUrl("jdbc:mysql://ip-privado:3306/Fraux?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        basicDataSource.setUsername("username");
        basicDataSource.setPassword("SENHA");

        this.conexao = basicDataSource;
    }
    public JdbcTemplate getConnection(){
        return new JdbcTemplate(conexao);
    }
}