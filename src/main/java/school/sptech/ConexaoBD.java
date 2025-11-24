package school.sptech;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class ConexaoBD {
    private final DataSource conexao; // tem agregação aqui
    private JdbcTemplate jdbcTemplate = null; // tem agregação aqui

    public ConexaoBD() {
        this.jdbcTemplate = jdbcTemplate;

        BasicDataSource basicDataSource = new BasicDataSource();

        basicDataSource.setUrl("jdbc:mysql://container:porta/Fraux?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("urubu100");

        this.conexao = basicDataSource;
    }
    public JdbcTemplate getConnection(){
        return new JdbcTemplate(conexao);
    }

    public DataSource getConexao() {
        return conexao;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }
}