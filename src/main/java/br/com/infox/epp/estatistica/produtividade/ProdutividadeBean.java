package br.com.infox.epp.estatistica.produtividade;

import java.io.Serializable;

public class ProdutividadeBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private Long quantidadeTarefas;
    private String tempoPrevisto;
    private String localizacao;
    private String papel;
    private String usuario;
    private String tarefa;
    private String mediaTempoGasto;
    private String minimoTempoGasto;
    private String maximoTempoGasto;

    public ProdutividadeBean(String tempoPrevisto, String localizacao,
            String papel, String usuario, String tarefa,
            String mediaTempoGasto, String minimoTempoGasto,
            String maximoTempoGasto, Long quantidadeTarefas) {
        this.tempoPrevisto = tempoPrevisto;
        this.localizacao = localizacao;
        this.papel = papel;
        this.usuario = usuario;
        this.tarefa = tarefa;
        this.mediaTempoGasto = mediaTempoGasto;
        this.minimoTempoGasto = minimoTempoGasto;
        this.maximoTempoGasto = maximoTempoGasto;
        this.quantidadeTarefas = quantidadeTarefas;
    }

    public ProdutividadeBean() {
    }

    public Long getQuantidadeTarefas() {
        return quantidadeTarefas;
    }

    public void setQuantidadeTarefas(Long quantidadeTarefas) {
        this.quantidadeTarefas = quantidadeTarefas;
    }

    public String getTempoPrevisto() {
        return tempoPrevisto;
    }

    public void setTempoPrevisto(String tempoPrevisto) {
        this.tempoPrevisto = tempoPrevisto;
    }

    public String getLocalizacao() {
        return localizacao;
    }

    public void setLocalizacao(String localizacao) {
        this.localizacao = localizacao;
    }

    public String getPapel() {
        return papel;
    }

    public void setPapel(String papel) {
        this.papel = papel;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getTarefa() {
        return tarefa;
    }

    public void setTarefa(String tarefa) {
        this.tarefa = tarefa;
    }

    public String getMediaTempoGasto() {
        return mediaTempoGasto;
    }

    public void setMediaTempoGasto(String mediaTempoGasto) {
        this.mediaTempoGasto = mediaTempoGasto;
    }

    public String getMinimoTempoGasto() {
        return minimoTempoGasto;
    }

    public void setMinimoTempoGasto(String minimoTempoGasto) {
        this.minimoTempoGasto = minimoTempoGasto;
    }

    public String getMaximoTempoGasto() {
        return maximoTempoGasto;
    }

    public void setMaximoTempoGasto(String maximoTempoGasto) {
        this.maximoTempoGasto = maximoTempoGasto;
    }
}
