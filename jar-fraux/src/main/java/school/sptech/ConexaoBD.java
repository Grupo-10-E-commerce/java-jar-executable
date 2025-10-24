package school.sptech;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;


import javax.sql.DataSource;

public class ConexaoBD {
    private DataSource conexao;

    public ConexaoBD() {

        BasicDataSource basicDataSource = new BasicDataSource();

        basicDataSource.setUrl("jdbc:mysql://localhost:3306/Fraux");
        basicDataSource.setUsername("root");
        basicDataSource.setPassword("urubu100");

        this.conexao = basicDataSource;
    }
    public JdbcTemplate getConnection(){
        return new JdbcTemplate(conexao);
    }

}
