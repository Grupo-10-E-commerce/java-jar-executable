package school.sptech;

public class Compra {

    private Integer id_compra;
    private String data_hora_transacao;
    private Double valor_transacao;
    private Integer id_empresa;
    private String tipo_transacao;
    private String cidade;
    private Integer fraude;

    public Compra() {
    }

    public Compra(Integer id_compra, String data_hora_transacao, Double valor_transacao, Integer id_empresa, String tipo_transacao, String cidade, Integer fraude) {
        id_compra = id_compra;
        data_hora_transacao = data_hora_transacao;
        valor_transacao = valor_transacao;
        id_empresa = id_empresa;
        tipo_transacao = tipo_transacao;
        cidade = cidade;
        fraude = fraude;
    }

    @Override
    public String toString() {
        return "Compra{" +
                "TransactionID=" + id_compra +
                ", TransactionDate='" + data_hora_transacao + '\'' +
                ", Amount=" + valor_transacao +
                ", MerchantID=" + id_empresa +
                ", TransactionType='" + tipo_transacao + '\'' +
                ", Location='" + cidade + '\'' +
                ", IsFraud=" + fraude +
                '}';
    }

    public Integer getId_compra() {
        return id_compra;
    }

    public void setId_compra(Integer id_compra) {
        this.id_compra = id_compra;
    }

    public String getData_hora_transacao() {
        return data_hora_transacao;
    }

    public void setData_hora_transacao(String data_hora_transacao) {
        this.data_hora_transacao = data_hora_transacao;
    }

    public Double getValor_transacao() {
        return valor_transacao;
    }

    public void setValor_transacao(Double valor_transacao) {
        this.valor_transacao = valor_transacao;
    }

    public Integer getId_empresa() {
        return id_empresa;
    }

    public void setId_empresa(Integer id_empresa) {
        this.id_empresa = id_empresa;
    }

    public String getTipo_transacao() {
        return tipo_transacao;
    }

    public void setTipo_transacao(String tipo_transacao) {
        this.tipo_transacao = tipo_transacao;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public Integer getFraude() {
        return fraude;
    }

    public void setFraude(Integer fraude) {
        this.fraude = fraude;
    }
}

