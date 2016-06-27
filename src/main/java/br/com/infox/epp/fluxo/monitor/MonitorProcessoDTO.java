package br.com.infox.epp.fluxo.monitor;

public class MonitorProcessoDTO {
    private String key;
    private long quantidade;

    public MonitorProcessoDTO(String key, Number quantidade) {
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
