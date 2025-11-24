package school.sptech;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AlertaPersonalizado {

    private Integer idAlerta;
    private Integer idEmpresa;
    private String nomeAlerta;
    private String metodoPagamento;
    private Double valorMinimo;
    private String cidade;
    private Integer mes;
    private Integer ano;
    private Boolean ativo;
    private Integer totalFraudes;
    private Double totalPrejuizo;
    private List<Compra> comprasFraudulentas; // agregação
    public AlertaPersonalizado() {
        this.comprasFraudulentas = new ArrayList<>();
    }

    public boolean slackParams(Compra compra, DateTimeFormatter formatter) {
        if (Boolean.FALSE.equals(ativo)) return false;

        if (valorMinimo != null) {
            if (compra.getValor_transacao() == null ||
                    compra.getValor_transacao() < valorMinimo) {
                return false;
            }
        }

        if (cidade != null && !cidade.isBlank()) {
            if (compra.getCidade() == null ||
                    !cidade.equalsIgnoreCase(compra.getCidade())) {
                return false;
            }
        }

        if (metodoPagamento != null && !metodoPagamento.isBlank()) {
            if (compra.getTipo_transacao() == null ||
                    !metodoPagamento.equalsIgnoreCase(compra.getTipo_transacao())) {
                return false;
            }
        }

        if (mes != null || ano != null) {
            LocalDateTime data = LocalDateTime.parse(
                    compra.getData_hora_transacao().substring(0, 19),
                    formatter
            );

            if (mes != null && data.getMonthValue() != mes) return false;
            if (ano != null && data.getYear() != ano) return false;
        }

        return true;
    }

    public void registrarCompra(Compra compra) {
        if (compra.getFraude() != null && compra.getFraude() == 1) {
            totalFraudes++;
            if (compra.getValor_transacao() != null) {
                totalPrejuizo += compra.getValor_transacao();
            }
        }
    }

    public String getDescricaoFiltros() {
        StringBuilder sb = new StringBuilder();

        if (valorMinimo != null) {
            sb.append("valor ≥ R$ ")
                    .append(String.format("%.2f", valorMinimo))
                    .append("; ");
        }
        if (cidade != null && !cidade.isBlank()) {
            sb.append("cidade = ").append(cidade).append("; ");
        }
        if (metodoPagamento != null && !metodoPagamento.isBlank()) { // NOVO
            sb.append("método = ").append(metodoPagamento).append("; ");
        }
        if (mes != null) {
            sb.append("mês = ").append(mes).append("; ");
        }
        if (ano != null) {
            sb.append("ano = ").append(ano).append("; ");
        }

        if (sb.isEmpty()) {
            return "sem filtros (todas as fraudes)";
        }

        return sb.substring(0, sb.length() - 2);
    }

    public Integer getIdAlerta() {
        return idAlerta;
    }

    public void setIdAlerta(Integer idAlerta) {
        this.idAlerta = idAlerta;
    }

    public Integer getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(Integer idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNomeAlerta() {
        return nomeAlerta;
    }

    public void setNomeAlerta(String nomeAlerta) {
        this.nomeAlerta = nomeAlerta;
    }

    public String getMetodoPagamento() {
        return metodoPagamento;
    }

    public void setMetodoPagamento(String metodoPagamento) {
        this.metodoPagamento = metodoPagamento;
    }

    public Double getValorMinimo() {
        return valorMinimo;
    }

    public void setValorMinimo(Double valorMinimo) {
        this.valorMinimo = valorMinimo;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public Integer getMes() {
        return mes;
    }

    public void setMes(Integer mes) {
        this.mes = mes;
    }

    public Integer getAno() {
        return ano;
    }

    public void setAno(Integer ano) {
        this.ano = ano;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }

    public int getTotalFraudes() {
        return totalFraudes;
    }

    public double getTotalPrejuizo() {
        return totalPrejuizo;
    }

    public List<Compra> getComprasFraudulentas() {
        return comprasFraudulentas;
    }

    public void setComprasFraudulentas(List<Compra> comprasFraudulentas) {
        this.comprasFraudulentas = comprasFraudulentas;
    }
}
