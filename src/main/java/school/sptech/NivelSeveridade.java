package school.sptech;

public enum NivelSeveridade {
    INFO("INFO", "Informação"),
    WARN("WARN", "Aviso"),
    ERROR("ERROR", "Erro");

    private final String codigo;
    private final String descricao;

    NivelSeveridade(String codigo, String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    @Override
    public String toString() {
        return codigo;
    }

    public static NivelSeveridade retornaNivel(String codigo) {
        for (NivelSeveridade nivel : values()) {
            if (nivel.codigo.equalsIgnoreCase(codigo)) {
                return nivel;
            }
        }
        return INFO; // padrão
    }
}