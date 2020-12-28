package br.com.infox.epp.estatistica.produtividade;

import java.io.Serializable;

import br.com.infox.epp.tarefa.type.PrazoEnum;

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

    public ProdutividadeBean(Integer prazo, String localizacao,
            String papel, String usuario, String tarefa,
            Double mediaTempoGasto, Integer minimoTempoGasto,
            Integer maximoTempoGasto, Long quantidadeTarefas, PrazoEnum tipoPrazo) {

        this.localizacao = localizacao;
        this.papel = papel;
        this.usuario = usuario;
        this.tarefa = tarefa;
        this.quantidadeTarefas = quantidadeTarefas;

        this.maximoTempoGasto = PrazoEnum.formatTempo(maximoTempoGasto, tipoPrazo);
        this.mediaTempoGasto = PrazoEnum.formatTempo(mediaTempoGasto.intValue(), tipoPrazo);
        this.minimoTempoGasto = PrazoEnum.formatTempo(minimoTempoGasto, tipoPrazo);

        if (prazo != null) {
            prazo = PrazoEnum.calcPrazo(prazo, tipoPrazo);
        }
        this.tempoPrevisto = PrazoEnum.formatTempo(prazo, tipoPrazo);
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
