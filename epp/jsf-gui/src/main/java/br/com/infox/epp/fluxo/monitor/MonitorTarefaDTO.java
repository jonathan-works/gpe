package br.com.infox.epp.fluxo.monitor;

public class MonitorTarefaDTO {
    private String key;
    private long quantidade;

    public MonitorTarefaDTO(String key, Number quantidade) {
        this.key = key;
        this.quantidade = quantidade.longValue();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Long quantidade) {
        this.quantidade = quantidade;
    }
}
