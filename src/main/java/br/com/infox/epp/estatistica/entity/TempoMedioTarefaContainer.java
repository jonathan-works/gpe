package br.com.infox.epp.estatistica.entity;

import br.com.infox.epp.fluxo.entity.NaturezaCategoriaFluxo;
import br.com.infox.epp.tarefa.entity.Tarefa;

public class TempoMedioTarefaContainer {
    /*new map(t as tarefa, ncf as naturezaCategoriaFluxo, count(pet) as instancias, avg(pet.tempoGasto) as mediaTempoGasto)*/
    
    private Tarefa tarefa;
    private NaturezaCategoriaFluxo naturezaCategoriaFluxo;
    private Integer instancias;
    private Double mediaTempoGasto;
    
    public TempoMedioTarefaContainer(Tarefa tarefa,
            NaturezaCategoriaFluxo naturezaCategoriaFluxo, Integer instancias,
            Double mediaTempoGasto) {
        this.tarefa = tarefa;
        this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
        this.instancias = instancias;
        this.mediaTempoGasto = mediaTempoGasto;
    }
    
    public Tarefa getTarefa() {
        return tarefa;
    }
    public void setTarefa(Tarefa tarefa) {
        this.tarefa = tarefa;
    }
    public NaturezaCategoriaFluxo getNaturezaCategoriaFluxo() {
        return naturezaCategoriaFluxo;
    }
    public void setNaturezaCategoriaFluxo(
            NaturezaCategoriaFluxo naturezaCategoriaFluxo) {
        this.naturezaCategoriaFluxo = naturezaCategoriaFluxo;
    }
    public Integer getInstancias() {
        return instancias;
    }
    public void setInstancias(Integer instancias) {
        this.instancias = instancias;
    }
    public Double getMediaTempoGasto() {
        return mediaTempoGasto;
    }
    public void setMediaTempoGasto(Double mediaTempoGasto) {
        this.mediaTempoGasto = mediaTempoGasto;
    }
    
}
